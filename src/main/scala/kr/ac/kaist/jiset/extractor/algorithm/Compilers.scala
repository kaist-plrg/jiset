package kr.ac.kaist.jiset.extractor.algorithm

import kr.ac.kaist.jiset.{ error => _, _ }
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.algorithm.token._
import kr.ac.kaist.jiset.util.Useful._
import scala.Console.RED
import spray.json._

trait Compilers extends TokenListParsers {
  import kr.ac.kaist.jiset.ir._
  import kr.ac.kaist.jiset.ir.Parser._
  import kr.ac.kaist.jiset.ir.JsonProtocol._

  // version
  val version: String

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

  ////////////////////////////////////////////////////////////////////////////////
  // Helpers
  ////////////////////////////////////////////////////////////////////////////////
  // failed steps
  def failedTokens: PackratParser[List[Token]] =
    normal ^^ { List(_) } | in ~> stmts <~ out ^^^ List(In, Out)
  def failedStep: PackratParser[Inst] = rep(failedTokens) ~ next ^^ {
    case tss ~ k =>
      val tokens = tss.flatten
      failed += k -> tokens
      val str = tokens.map(_.beautified).mkString(" ")
      val failedInst = manualSteps.getOrElse(str, IExpr(ENotSupported(str)))
      failedInst.line = k
      failedInst
  }

  // manual steps
  private case class Pair(tokens: String, inst: Inst)
  val manualSteps: Map[String, Inst] = try {
    implicit lazy val PairProtocol = jsonFormat2(Pair)
    val pairs = readJson[List[Pair]](s"$VERSION_DIR/rule.json")
    (for (Pair(str, inst) <- pairs) yield str -> inst).toMap
  } catch {
    case e: Throwable =>
      println
      printlnColor(RED)(s"Failed to load $VERSION_DIR/rule.json")
      println
      printlnColor(RED)(e.getMessage)
      Map()
  }

  // word for camel cases
  def camelWord: P[String] = word.filter(_.head.isUpper)

  // empty instruction
  lazy val emptyInst: Inst = ISeq(Nil)

  // type alias
  type P[A] = PackratParser[A]
  type I[A] = List[Inst] ~ A

  // list of statements
  lazy val stmts: P[List[Inst]] =
    rep(stmt ~ next ^^ { case i ~ k => i.line = k; i } | failedStep)

  // start notations
  lazy val starStmt: P[Inst] = star ^^ { case s => IExpr(ENotSupported(s"stmt: $s")) }
  lazy val starExpr: P[I[Expr]] = star ^^ { case s => pair(Nil, ENotSupported(s"expr: $s")) }
  lazy val starCond: P[I[Expr]] = star ^^ { case s => pair(Nil, ENotSupported(s"cond: $s")) }

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
  def exists(expr: Expr): Expr = isNEq(expr, EAbsent)
  def exists(ref: Ref): Expr = exists(ERef(ref))
  def isEq(l: Expr, r: Expr): Expr = EBOp(OEq, l, r)
  def isNEq(l: Expr, r: Expr): Expr = not(isEq(l, r))
  def not(e: Expr): Expr = e match {
    case EUOp(ONot, e) => e
    case _ => EUOp(ONot, e)
  }

  // get error objects
  def getErrorObj(name: String): Expr = Expr("(new OrdinaryObject())")

  // for-each instrutions for lists
  def forEachList(id: Id, expr: Expr, body: Inst, reversed: Boolean = false): Inst = {
    val list = getTempId
    val idx = getTempId
    if (reversed) ISeq(List(
      ILet(list, expr),
      ILet(idx, toERef(list, "length")),
      IWhile(EBOp(OLt, EINum(0), toERef(idx)), ISeq(List(
        IAssign(toRef(idx), EBOp(OSub, toERef(idx), EINum(1))),
        ILet(id, toERef(list, toERef(idx))),
        body
      )))
    ))
    else ISeq(List(
      ILet(list, expr),
      ILet(idx, EINum(0)),
      IWhile(EBOp(OLt, toERef(idx), toERef(list, "length")), ISeq(List(
        ILet(id, toERef(list, toERef(idx))),
        body,
        IAssign(toRef(idx), EBOp(OPlus, toERef(idx), EINum(1)))
      )))
    ))
  }

  // for-each instrutions for maps
  def forEachMap(id: Id, expr: Expr, body: Inst, reversed: Boolean = false): Inst =
    forEachList(id, EKeys(expr), body, reversed)

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

  // IfAbruptRejectPromise
  def ifAbruptRejectPromise(
    insts: List[Inst],
    expr: Expr,
    capexpr: Ref
  ): List[Inst] ~ Expr = (insts, expr, capexpr) match {
    case (i, (e @ ERef(RefId(Id(x)))), ce) =>
      val temp = getTempId
      pair(i :+ IIf(
        EIsCompletion(e),
        IIf(
          isEq(toERef(x, "Type"), toERef("CONST_normal")),
          IAssign(toRef(x), toERef(x, "Value")),
          ISeq(List(
            IApp(temp, toERef("Call"), List(ERef(RefProp(ce, EStr("Reject"))), EUndef, EList(List(toERef(x, "Value"))))),
            IIf(
              EBOp(OAnd, EIsCompletion(toERef(temp)), isNEq(toERef(temp, "Type"), toERef("CONST_normal"))),
              IReturn(toERef(temp)),
              emptyInst
            ),
            IReturn(ERef(RefProp(ce, EStr("Promise"))))
          ))
        ),
        emptyInst
      ), e)
    case (i, e, ce) =>
      val temp = getTempId
      val temp2 = getTempId
      pair(i :+ ISeq(List(
        ILet(temp, e),
        IIf(
          EIsCompletion(toERef(temp)),
          IIf(
            isEq(toERef(temp, "Type"), toERef("CONST_normal")),
            IAssign(toRef(temp), toERef(temp, "Value")),
            ISeq(List(
              IApp(temp2, toERef("Call"), List(ERef(RefProp(ce, EStr("Reject"))), EUndef, EList(List(toERef(temp, "Value"))))),
              IIf(
                EBOp(OAnd, EIsCompletion(toERef(temp2)), isNEq(toERef(temp2, "Type"), toERef("CONST_normal"))),
                IReturn(toERef(temp2)),
                emptyInst
              ),
              IReturn(ERef(RefProp(ce, EStr("Promise"))))
            ))
          ),
          emptyInst
        )
      )), toERef(temp))
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

  // emptiness check
  def isEmptyInsts(insts: List[Inst]): Boolean =
    insts.forall(inst => flatten(inst) == ISeq(Nil))

  // post process
  def postProcess(inst: Inst): Inst = normalizeTempIds(flatten(inst))

  // flatten instructions
  def flatten(inst: Inst): Inst = FlattenWalker.walk(inst)
  object FlattenWalker extends Walker {
    override def walk(inst: Inst): Inst = inst match {
      case ISeq(insts) =>
        def assign(insts: List[Inst], k: Int): List[Inst] =
          if (k == -1) insts else insts.map(i => { i.line = k; i })
        def aux(cur: List[Inst], remain: List[Inst]): List[Inst] = remain match {
          case Nil => cur.reverse
          case (seq @ ISeq(list)) :: rest => aux(cur, assign(list, seq.line) ++ rest)
          case inst :: rest => aux(walk(inst) :: cur, rest)
        }
        val newInst = aux(Nil, assign(insts, inst.line)) match {
          case List(inst) => inst
          case insts => ISeq(insts)
        }
        newInst.line = inst.line
        newInst
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
  def toERef(str: String, fs: List[I[Expr]]): I[Expr] = {
    val i ~ r = toRef(str, fs)
    pair(i, ERef(r))
  }
  def toRef(x: Id, y: Expr): Ref = RefProp(toRef(x), y)
  def toRef(x: String, y: Expr): Ref = RefProp(toRef(x), y)
  def toRef(x: Id, y: String): Ref = RefProp(toRef(x), EStr(y))
  def toRef(x: String, y: String): Ref = RefProp(toRef(x), EStr(y))
  def toRef(id: Id): Ref = RefId(id)
  def toRef(str: String): Ref = toRef(Id(str))
  def toRef(str: String, fs: List[I[Expr]]): I[Ref] = {
    val i = fs.map { case i ~ _ => i }.flatten
    val es = fs.map { case _ ~ e => e }
    pair(i, es.foldLeft[Ref](toRef(str))(RefProp(_, _)))
  }
  def toBigInt(e: Expr): Expr = EConvert(e, CNumToBigInt, Nil)

  // create pair of parsing results
  val pair = `~`
  def concat(a: List[Inst], b: I[Expr]): I[Expr] = b match {
    case bi ~ be => pair(a ++ bi, be)
  }

  // get access
  def getAccess(
    f: String,
    ix: I[Expr],
    list: List[I[Expr]]
  ): I[Ref] = {
    val i0 ~ x = ix
    val temp = getTempId
    val i = list.foldLeft(i0) { case (is, i ~ _) => is ++ i }
    val as = list.map { case i ~ e => e }
    pair(i :+ IAccess(temp, x, EStr(f), as), toRef(temp))
  }

  // get instruction
  def getInst(ie: I[Expr]): Inst = ISeq(ie._1)

  // get return
  def getRet(e: Expr): Inst = getRet(pair(Nil, e))
  def getRet(ieOpt: Option[I[Expr]]): Inst =
    ieOpt.fold(getRet(EUndef))(getRet(_))
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

  // get lists
  def getList(list: List[I[Expr]]): I[Expr] = {
    val i = list.map { case i ~ _ => i }.flatten
    pair(i, EList(list.map { case _ ~ e => e }))
  }

  // get copy lists
  def getCopyList(il0: I[Expr], il1: I[Expr]): I[Expr] = {
    val i0 ~ l0 = il0
    val i1 ~ l1 = il1
    val elem = getTempId
    val newList = getTempId
    pair(i0 ++ i1 ++ List(
      ILet(newList, ECopy(l0)),
      forEachList(elem, l1, IAppend(toERef(elem), toERef(newList)))
    ), toERef(newList))
  }
  def getCopyList(il: I[Expr], appended: List[I[Expr]], prepend: Boolean = false): I[Expr] = {
    val i ~ l = il
    appended match {
      case Nil => pair(i, ECopy(l))
      case _ =>
        val newList = getTempId
        val i0 = appended.map { case i ~ _ => i }.flatten
        val as = appended.map { case _ ~ e => e }
        val f =
          if (prepend) IPrepend(_, toERef(newList))
          else IAppend(_, toERef(newList))
        pair((i ++ i0 :+ ILet(newList, ECopy(l))) ++ as.map(f), toERef(newList))
    }
  }

  // binary operator calculations
  def calc(n: Boolean, rev: Boolean, bop: BOp, left: Expr, right: Expr): Expr = {
    val (l, r) =
      if (rev) (right, left)
      else (left, right)
    val expr = EBOp(bop, l, r)
    if (n) not(expr) else expr
  }

  // check abrupt completion
  def isAbruptCompletion(x: String): Expr = {
    EBOp(OAnd, EIsCompletion(toERef(x)), isNEq(toERef(x, "Type"), toERef("CONST_normal")))
  }

  // separators
  def sep(s: P[Any]): P[String] = (
    literal(",") ||| literal(",") ~ s ||| s
  ) ^^^ ""

  // metions
  lazy val mention: P[String] = literal("(") ~ rep1(normal.filter(_ != Text(")"))) ~ literal(")") ^^^ ""

  // intrinsic prefix
  val INTRINSIC_PRE = "INTRINSIC_"

  // suspend context
  def suspend(x: String, removed: Boolean = false): Inst = {
    if (removed) {
      ISeq(List(
        IAssign(toRef(context), ENull),
        IIf(
          isEq(toERef(executionStack, EBOp(OSub, toERef(executionStack, "length"), EINum(1))), toERef(x)),
          IExpr(EPop(toERef(executionStack), EBOp(OSub, toERef(executionStack, "length"), EINum(1)))),
          emptyInst
        )
      ))
    } else IAssign(toRef(context), ENull)
  }

  // infinity check
  def isInfinity(expr: Expr): Expr = EBOp(
    OOr,
    EBOp(OEq, expr, ENum(Double.PositiveInfinity)),
    EBOp(OEq, expr, ENum(Double.NegativeInfinity))
  )

  // select rather than not supported
  def select(left: Parser[I[Expr]], right: Parser[I[Expr]]): Parser[I[Expr]] = new Parser[I[Expr]] {
    def apply(in: Input) = {
      val lres = left(in)
      val rres = right(in)

      (lres, rres) match {
        case (s1 @ Success(_ ~ l, next1), s2 @ Success(_ ~ r, next2)) =>
          if (next2.pos < next1.pos) s1
          else if (next1.pos < next2.pos) s2
          else (l, r) match {
            case (_, ENotSupported(_)) => s1
            case (ENotSupported(_), _) => s2
            case _ => s1
          }
        case (s1 @ Success(_, _), _) => s1
        case (_, s2 @ Success(_, _)) => s2
        case (e1 @ Error(_, _), _) => e1
        case (f1 @ Failure(_, next1), ns2 @ NoSuccess(_, next2)) => if (next2.pos < next1.pos || next2.pos == next1.pos) f1 else ns2
      }
    }
    override def toString = "|||"
  }

  def noExc[T](x: => T): Boolean = try { x; true } catch { case e: Throwable => false }

  def log[T](p: Parser[T], msg: String = ""): P[T] = p ^^ {
    x =>
      val logWithMsg = if (msg != "") s"[$msg] $x" else x
      println(logWithMsg)
      x
  }

  // execution context stack string
  val executionStack = "EXECUTION_STACK"
  val context = "CONTEXT"
  val typedArrayInfo = "TYPED_ARRAY_INFO"
  val agent = "AGENT"
  val symbolRegistry = "SYMBOL_REGISTRY"
  val realm = "REALM"
  val scriptBody = "SCRIPT_BODY"
  val jobQueue = "JOB_QUEUE"
  val retcont = "RET_CONT"

  // predefined variables
  val ANY_BOOL = "AnyBool"
}
