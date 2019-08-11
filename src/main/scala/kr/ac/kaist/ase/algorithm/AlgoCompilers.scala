package kr.ac.kaist.ase.algorithm

import kr.ac.kaist.ase.LINE_SEP
import kr.ac.kaist.ase.error.UnexpectedShift
import kr.ac.kaist.ase.parser.TokenParsers
import kr.ac.kaist.ase.util.Useful._
import scala.util.{ Try, Success, Failure }

trait AlgoCompilers extends TokenParsers {
  import kr.ac.kaist.ase.core.Parser._
  import kr.ac.kaist.ase.core._

  // instructions
  val stmt: P[Inst]

  // expressions
  val expr: P[I[Expr]]

  // conditions
  val cond: P[I[Expr]]

  // types
  val ty: Parser[Ty]

  // references
  val ref: P[I[Ref]]

  // fields
  val field: P[I[Expr]]

  // section numbers
  val section: P[String]

  ////////////////////////////////////////////////////////////////////////////////
  // Helpers
  ////////////////////////////////////////////////////////////////////////////////
  // failed steps
  def failedToken: PackratParser[Token] = normal | in ~> stmts <~ out ^^^ StepList(Nil)
  def failedStep: PackratParser[List[String]] = rep(failedToken) ~ next ^^ {
    case s ~ k => failed += k -> s; s.map(_.toString)
  }

  // empty instruction
  lazy val emptyInst: Inst = ISeq(Nil)

  // type alias
  type P[A] = PackratParser[A]
  type I[A] = List[Inst] ~ A

  // list of statements
  lazy val stmts: P[List[Inst]] = rep(stmt <~ next | failedStep ^^ { tokens =>
    IExpr(ENotYetImpl(tokens.mkString(" ").replace("\\", "\\\\").replace("\"", "\\\"")))
  })

  // start notations
  lazy val starStmt: P[Inst] = star ^^ { case s => IExpr(ENotYetImpl(s"stmt: $s")) }
  lazy val starExpr: P[I[Expr]] = star ^^ { case s => pair(Nil, ENotYetImpl(s"expr: $s")) }
  lazy val starCond: P[I[Expr]] = star ^^ { case s => pair(Nil, ENotYetImpl(s"cond: $s")) }

  // get temporal identifiers
  private var idCount: Int = 0
  val TEMP_PRE: String = "__x"
  val TEMP_POST: String = "__"
  def getTemp: String = {
    val i = idCount
    idCount += 1
    s"$TEMP_PRE$i$TEMP_POST"
  }
  def getTempId: Id = Id(getTemp)

  // several checks
  def checkEq(l: Expr, r: Expr): Expr = EBOp(OEq, l, r)
  def checkNot(e: Expr): Expr = EUOp(ONot, e)
  def checkNEq(l: Expr, r: Expr): Expr = checkNot(checkEq(l, r))
  def exists(expr: Expr): Expr = EUOp(ONot, EBOp(OEq, expr, EAbsent))
  def exists(ref: Ref): Expr = exists(ERef(ref))

  // for-each instrutions for lists
  def forEachList(id: Id, expr: Expr, body: Inst, reversed: Boolean = false): Inst = {
    val list = getTemp
    val idx = getTemp
    parseInst(
      if (reversed) s"""{
        let $list = ${beautify(expr)}
        let $idx = $list.length
        while (< 0i $idx) {
          $idx = (- $idx 1i)
          let ${beautify(id)} = $list[$idx]
          ${beautify(body)}
        }
      }"""
      else s"""{
        let $list = ${beautify(expr)}
        let $idx = 0i
        while (< $idx $list.length) {
          let ${beautify(id)} = $list[$idx]
          ${beautify(body)}
          $idx = (+ $idx 1i)
        }
      }"""
    )
  }

  // for-each instrutions for maps
  def forEachMap(id: Id, expr: Expr, body: Inst, reversed: Boolean = false): Inst = {
    val list = getTemp
    val idx = getTemp
    parseInst(s"""{
      let $list = (map-keys ${beautify(expr)})
      let $idx = 0i
      while (< $idx $list.length) {
        let ${beautify(id)} = $list[$idx]
        ${beautify(body)}
        $idx = (+ $idx 1i)
      }
    }""")
  }

  // handle duplicated params and variable-length params
  def handleParams(l: List[String]): (List[Id], Option[Id]) = {
    def aux(scnt: Map[String, Int], lprev: List[Id], lnext: List[String]): List[Id] = lnext match {
      case Nil => lprev
      case s :: rest => {
        scnt.lift(s) match {
          case Some(n) => aux(scnt + (s -> (n + 1)), Id(s"$s$n") :: lprev, rest)
          case None => if (rest contains s) {
            aux(scnt + (s -> 1), Id(s + "0") :: lprev, rest)
          } else {
            aux(scnt, Id(s) :: lprev, rest)
          }
        }
      }
    }
    aux(Map(), Nil, l) match {
      case Id(x) :: tl if x.startsWith("...") =>
        (tl.reverse, Some(Id(x.substring(3))))
      case l => (l.reverse, None)
    }
  }

  // ReturnIfAbrupt
  def returnIfAbrupt(
    insts: List[Inst],
    expr: Expr,
    vulnerable: Boolean = true
  ): I[Expr] = (insts, expr) match {
    case (i, (e @ ERef(RefId(Id(x))))) => pair(i :+ parseInst(s"""
      if (= (typeof $x) "Completion") {
        if (= $x.Type CONST_normal) $x = $x.Value
        else return $x
      } else {}"""), e)
    case (i, e) =>
      val temp = getTemp
      pair(i :+ parseInst(
        if (vulnerable) s"""{
        let $temp = ${beautify(e)}
        if (= (typeof $temp) "Completion") {
          if (= $temp.Type CONST_normal) $temp = $temp.Value
          else return $temp
        } else {}
      }"""
        else s"""{
        let $temp = ${beautify(e)}
        if (= (typeof $temp) "Completion") {
          $temp = $temp.Value
        } else {}
      }"""
      ), parseExpr(temp))
  }

  // normalize temporal identifiers
  def normalizeTempIds(inst: Inst): Inst = (new Walker {
    var count: Int = 0
    def newId: String = {
      val s = s"$TEMP_PRE$count$TEMP_POST"
      count += 1
      s
    }
    var idMap: Map[String, String] = Map()
    override def walk(id: Id): Id = id.name match {
      case s if s.startsWith(TEMP_PRE) && s.endsWith(TEMP_POST) => Id(idMap.getOrElse(s, {
        val newS = newId
        idMap += (s -> newS)
        newS
      }))
      case _ => id
    }
  }).walk(inst)

  // flatten instructions
  def flatten(inst: Inst): Inst = FlattenWalker.walk(inst)
  object FlattenWalker extends Walker {
    override def walk(inst: Inst): Inst = inst match {
      case ISeq(insts) =>
        def aux(cur: List[Inst], remain: List[Inst]): List[Inst] = remain match {
          case Nil => cur.reverse
          case ISeq(list) :: rest => aux(cur, list ++ rest)
          case inst :: rest => aux(walk(inst) :: cur, rest)
        }
        aux(Nil, insts) match {
          case List(inst) => inst
          case insts => ISeq(insts)
        }
      case i => super.walk(i)
    }
  }

  // conversions
  def toERef(x: Id, y: Expr): ERef = ERef(toRef(x, y))
  def toERef(x: String, y: Expr): ERef = ERef(toRef(x, y))
  def toERef(x: Id, y: String): ERef = ERef(toRef(x, y))
  def toERef(x: String, y: String): ERef = ERef(toRef(x, y))
  def toERef(id: Id): ERef = ERef(toRef(id))
  def toERef(str: String): ERef = ERef(toRef(str))
  def toRef(x: Id, y: Expr): Ref = RefProp(toRef(x), y)
  def toRef(x: String, y: Expr): Ref = RefProp(toRef(x), y)
  def toRef(x: Id, y: String): Ref = RefProp(toRef(x), EStr(y))
  def toRef(x: String, y: String): Ref = RefProp(toRef(x), EStr(y))
  def toRef(id: Id): Ref = RefId(id)
  def toRef(str: String): Ref = toRef(Id(str))

  // create pair of parsing results
  val pair = `~`
  def concat(a: List[Inst], b: I[Expr]): I[Expr] = b match {
    case bi ~ be => pair(a ++ bi, be)
  }

  // get access
  def getAccess(x: String, y: String): I[Expr] = {
    val temp = getTemp
    pair(List(IAccess(Id(temp), toERef(x), EStr(y))), toERef(temp))
  }

  // get access
  def getAccess(
    x: String,
    f: String,
    list: List[I[Expr]]
  ): I[Expr] = {
    val temp = getTempId
    val temp2 = getTempId
    val i = list.map { case i ~ _ => i }.flatten
    val r = IAccess(temp, toERef(x), EStr(f))
    val e = IApp(temp2, toERef(temp), list.map { case _ ~ e => e })
    pair(i ++ List(r, e), toERef(temp2))
  }

  // get instruction
  def getInst(ie: I[Expr]): Inst = ISeq(ie._1)

  // get return
  def getRet(ie: I[Expr]): Inst = ie match {
    case i ~ e => ISeq(i :+ IReturn(e))
  }

  // get call
  def getCall(name: String, list: List[I[Expr]]): I[Expr] = getCall(toERef(name), list)
  def getCall(f: Expr, list: List[I[Expr]]): I[Expr] = {
    val temp = getTempId
    val i = list.map { case i ~ _ => i }.flatten
    val args = list.map { case _ ~ e => e }
    val app = IApp(temp, f, args)
    pair(i :+ app, toERef(temp))
  }

  // get completions
  def getThrowCompletion(e: Expr): I[Expr] = getThrowCompletion(pair(Nil, e))
  def getThrowCompletion(ie: I[Expr]): I[Expr] = getCall("ThrowCompletion", List(ie))
  def getWrapCompletion(e: Expr): I[Expr] = getWrapCompletion(pair(Nil, e))
  def getWrapCompletion(ie: I[Expr]): I[Expr] = getCall("WrapCompletion", List(ie))
  def getNormalCompletion(e: Expr): I[Expr] = getNormalCompletion(pair(Nil, e))
  def getNormalCompletion(ie: I[Expr]): I[Expr] = getCall("NormalCompletion", List(ie))

  // binary operator calculations
  def calc(not: Boolean, rev: Boolean, bop: BOp, left: Expr, right: Expr): Expr = {
    val (l, r) =
      if (rev) (right, left)
      else (left, right)
    val expr = EBOp(bop, l, r)
    if (not) EUOp(ONot, expr) else expr
  }

  // check abrupt completion
  def isAbruptCompletion(x: String): Expr = {
    EBOp(OAnd, checkEq(ETypeOf(toERef(x)), EStr("Completion")), checkNEq(toERef(x, "Type"), toERef("CONST_normal")))
  }

  // separators
  def sep(s: P[Any]): P[String] = (
    literal(",") ||| literal(",") ~ s ||| s
  ) ^^^ ""

  // metions
  lazy val mention: P[String] = literal("(") ~ rep1(normal.filter(_ != Text(")"))) ~ literal(")") ^^^ ""
}
