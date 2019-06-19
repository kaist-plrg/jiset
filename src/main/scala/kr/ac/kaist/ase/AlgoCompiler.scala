package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.parser.TokenParsers
import kr.ac.kaist.ase.algorithm.{ Algorithm, Token, RuntimeSemantics }

case class AlgoCompiler(algoName: String, algo: Algorithm) extends TokenParsers {
  def result: Func = Func(
    name = algoName,
    params = handleDuplicate(algo.params).map(Id(_)),
    body = ISeq(parseAll(stmts, algo.toTokenList) match {
      case Success(res, _) => res
      case NoSuccess(_, reader) => error(s"[AlgoCompiler]:${algo.filename}: $reader")
    })
  )

  def handleDuplicate(l: List[String]): List[String] = {
    def aux(scnt: Map[String, Int], lprev: List[String], lnext: List[String]): List[String] = lnext match {
      case Nil => lprev
      case s :: rest => {
        scnt.lift(s) match {
          case Some(n) => aux(scnt.updated(s, n + 1), lprev :+ (s + n.toString), rest)
          case None => if (rest contains s) {
            aux(scnt.updated(s, 1), lprev :+ (s + "0"), rest)
          } else {
            aux(scnt, lprev :+ s, rest)
          }
        }
      }
    }
    aux(Map(), Nil, l)
  }

  def parseStmt(tokens: List[Token]): Inst = parseAll(stmt, tokens).get

  // short-cut for TODO
  def itodo(msg: String): IExpr = IExpr(etodo(msg))
  def etodo(msg: String): ENotYetImpl = ENotYetImpl(msg)

  // temporal identifiers
  lazy val temp: String = "temp"
  lazy val tempId: Id = Id(temp)

  // ReturnIfAbrupt
  def returnIfAbrupt(name: String): Inst = parseInst(
    s"""if (= (typeof $name) "Completion") {
          if (= $name.Type normal) $name = $name.Value
          else return $name
        }"""
  )

  // empty instruction
  lazy val emptyInst: Inst = ISeq(Nil)

  // list of statements
  lazy val stmts: Parser[List[Inst]] = rep(
    stmt <~ opt(("(" | ".") <~ "see" <~ rest) <~ opt(".") <~ next | step ^^ {
      case tokens => itodo(tokens.mkString(" ").replace("\"", "\\\""))
    }
  ) // TODO flatten

  // running execution context string
  val context = "context"

  ////////////////////////////////////////////////////////////////////////////////
  // Instructions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val stmt: Parser[Inst] =
    returnStmt |
      letStmt |
      returnIfAbruptStmt |
      innerStmt |
      ifStmt |
      callStmt |
      setStmt |
      createStmt |
      throwStmt |
      whileStmt |
      etcStmt |
      ignoreStmt

  // return statements
  lazy val returnStmt =
    "return" ~> ("!" | "?") ~> expr ^^ {
      case e => algo.kind match {
        case RuntimeSemantics => ISeq(List(
          ILet(tempId, e),
          returnIfAbrupt(temp),
          IReturn(EApp(ERef(RefId(Id("WrapCompletion"))), List(ERef(RefId(tempId)))))
        ))
        case _ => ISeq(List(
          ILet(tempId, e),
          returnIfAbrupt(temp),
          IReturn(ERef(RefId(tempId)))
        ))
      }
    } | "return" ~> expr ^^ {
      case e => algo.kind match {
        case RuntimeSemantics => IReturn(EApp(ERef(RefId(Id("WrapCompletion"))), List(e)))
        case _ => IReturn(e)
      }
    }

  // let statements
  lazy val letStmt =
    ("let" ~> id <~ "be") ~ ("?" ~> word <~ "(") ~ ("?" ~> expr <~ ")") ^^ {
      case x ~ n ~ e => ISeq(List(
        ILet(tempId, e),
        returnIfAbrupt(temp),
        ILet(Id(x), EApp(ERef(RefId(Id(n))), List(ERef(RefId(tempId))))),
        returnIfAbrupt((x))
      ))
    } | ("let" ~> id <~ "be") ~ (word <~ "(") ~ ("?" ~> expr <~ ")") ^^ {
      case x ~ n ~ e => ISeq(List(
        ILet(tempId, e),
        returnIfAbrupt(temp),
        ILet(Id(x), EApp(ERef(RefId(Id(n))), List(ERef(RefId(tempId)))))
      ))
    } | ("let" ~> id <~ "be") ~ expr ^^ {
      case x ~ e => ILet(Id(x), e)
    } | ("let" ~> id <~ "be") ~ ("?" ~> expr) ^^ {
      case x ~ e => ISeq(List(
        ILet(Id(x), e),
        returnIfAbrupt(x)
      ))
    }

  // ReturnIfAbrupt statements
  lazy val returnIfAbruptStmt =
    "ReturnIfAbrupt(" ~> id <~ ")" ^^ {
      case x => returnIfAbrupt(x)
    }

  // inner statements
  lazy val innerStmt =
    in ~> stmts <~ out ^^ {
      case list => ISeq(list)
    }

  // if-then-else statements
  lazy val ifStmt =
    ("if" ~> cond <~ "," <~ opt("then")) ~ stmt ~ (opt("." | ";") ~> opt(next) ~>
      ("else" | "otherwise") ~> opt((
        name ~ "must be" ~ rep(not(",") ~ text)
      ) |
        (id ~ "does not currently have a property" ~ id) |
        (id <~ "is an accessor property") |
        ("isaccessordescriptor(" ~> id <~ ") and isaccessordescriptor(") ~ (id <~ ") are both") ~ expr) ~> opt(",") ~> stmt) ^^ {
        case c ~ t ~ e => IIf(c, t, e)
      } | ("if" ~> cond <~ "," <~ opt("then")) ~ stmt ^^ {
        case c ~ t => IIf(c, t, emptyInst)
      }

  // call statements
  lazy val callStmt =
    ("perform" | "call") ~> ("?" | "!") ~> callExpr ^^ {
      case e => ISeq(List(
        ILet(tempId, e),
        returnIfAbrupt(temp)
      ))
    } | ("perform" | "call") ~> callExpr ^^ {
      case e => IExpr(e)
    }

  // set statements
  lazy val setStmt =
    "set" ~ name ~ "'s essential internal methods" ~ rest ^^ {
      case _ => emptyInst
    } | ("set" ~> ref) ~ ("to" ~> expr) ^^ {
      case r ~ e => IAssign(r, e)
    } | ("set" ~> ref) ~ ("to" ~> "?" ~> expr) ^^ {
      case r ~ e => ISeq(List(
        ILet(tempId, e),
        returnIfAbrupt(temp),
        IAssign(r, ERef(RefId(tempId)))
      ))
    }

  // create statements
  lazy val createStmt =
    "create an own data property" ~ rest ^^^ {
      parseInst(s"""{
        dp = (new DataProperty())
        if (? Desc.Value) dp.Value = Desc.Value else dp.Value = undefined
        if (? Desc.Writable) dp.Writable = Desc.Writable else dp.Writable = false
        if (? Desc.Enumerable) dp.Enumerable = Desc.Enumerable else dp.Enumerable = false
        if (? Desc.Configurable) dp.Configurable = Desc.Configurable else dp.Configurable = false
        O.SubMap[P] = dp
      }""")
    } |
      "create an own accessor property" ~ rest ^^^ {
        parseInst(s"""{
        dp = (new AccessorProperty())
        if (? Desc.Get) dp.Get = Desc.Get else dp.Get = undefined
        if (? Desc.Set) dp.Set = Desc.Set else dp.Set = undefined
        if (? Desc.Enumerable) dp.Enumerable = Desc.Enumerable else dp.Enumerable = false
        if (? Desc.Configurable) dp.Configurable = Desc.Configurable else dp.Configurable = false
        O.SubMap[P] = dp
      }""")
      }

  // throw statements
  lazy val throwStmt =
    "throw a" ~> valueExpr <~ "exception" ^^ {
      case e => IReturn(EMap(Ty("Completion"), List(
        EStr("Type") -> parseExpr("throw"),
        EStr("Value") -> e,
        EStr("Target") -> parseExpr("empty")
      )))
    }

  // while statements
  lazy val whileStmt =
    ("repeat, while" ~> cond <~ ",") ~ stmt ^^ {
      case c ~ s => IWhile(c, s)
    }

  // et cetera statements
  lazy val etcStmt =
    "push" ~> expr <~ "onto the execution context stack" ~ rest ^^ {
      case e => IAssign(RefId(Id(context)), e)
    } | "in an implementation - dependent manner , obtain the ecmascript source texts" ~ rest ~ next ~ rest ^^^ {
      parseInst(s"""{
        context.VariableEnvironment = context.Realm.GlobalEnv
        context.LexicalEnvironment = context.Realm.GlobalEnv
        return (run Evaluation of script)
      }""")
    } | "If the code matching the syntactic production that is being evaluated" ~ rest ^^^ {
      parseInst(s"let strict = false")
    } | "if the host requires use of an exotic object" ~ rest ^^^ {
      parseInst("let global = undefined")
    } | "if the host requires that the" ~ rest ^^^ {
      parseInst("let thisValue = undefined")
    } | "for each field of" ~ rest ^^^ {
      parseInst(s"""O.SubMap[P].Value = Desc.Value""") // TODO: move each field of record at ValidateAndApplyPropertyDescriptor
    }

  // ignore statements
  lazy val ignoreStmt = (
    "assert:" |
    "set fields of" |
    "for each property of the global object" | // TODO : set global object properties
    "create any implementation-defined" |
    "no further validation is required" // TODO : should implement goto?? see ValidateAndApplyPropertyDescriptor
  ) ~ rest ^^^ emptyInst

  ////////////////////////////////////////////////////////////////////////////////
  // Expressions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val expr: Parser[Expr] =
    etcExpr |
      valueExpr |
      astExpr |
      completionExpr |
      callExpr |
      newExpr |
      listExpr |
      curExpr |
      algoExpr |
      typeExpr |
      refExpr

  // value expressions
  lazy val valueExpr = opt("the value") ~> value ^^ {
    case "null" => ENull
    case "true" => EBool(true)
    case "false" => EBool(false)
    case "undefined" => EUndef
    case s if s.startsWith("\"") && s.endsWith("\"") => EStr(s.slice(1, s.length - 1))
    case const @ ("empty" | "throw" | "normal") => ERef(RefId(Id(const.replaceAll("-", ""))))
    case err @ ("TypeError" | "ReferenceError") => EMap(Ty(err), Nil)
    case s => etodo(s)
  }

  // AST semantics
  lazy val astExpr =
    "the stringvalue of identifiername" ^^^ {
      parseExpr(s"IdentifierName")
    } | "the result of evaluating" ~> word ^^ {
      case x => parseExpr(s"(run Evaluation of $x)")
    } | (opt("the") ~> word <~ "of") ~ (word | id.filter(x => "code" == x || "script" == x)) ^^ {
      case f ~ x => parseExpr(s"(run $f of $x)")
    } | "IsFunctionDefinition of" ~> id ^^ {
      case x => parseExpr(s"(run IsFunctionDefinition of $x)")
    }

  // completion expressions
  lazy val completionExpr =
    "normalcompletion(" ~> expr <~ ")" ^^ {
      case e => EMap(Ty("Completion"), List(
        EStr("Type") -> parseExpr("normal"),
        EStr("Value") -> e,
        EStr("Target") -> parseExpr("empty")
      ))
    }

  // call expressions
  lazy val callExpr =
    "type(" ~> expr <~ ")" ^^ {
      case e => ETypeOf(e)
    } | "completion(" ~> expr <~ ")" ^^ {
      case e => e
    } | ref ~ ("(" ~> repsep(expr, ",") <~ ")") ^^ {
      case RefId(Id(x)) ~ list => EApp(parseExpr(x), list)
      case (r @ RefProp(b, _)) ~ list => EApp(ERef(r), ERef(b) :: list)
    }

  // new expressions
  lazy val newExpr =
    "a new empty list" ^^^ {
      EList(Nil)
    } | ("a new" ~> ty <~ "containing") ~ (expr <~ "as the binding object") ^^ {
      case t ~ e => EMap(t, List(
        EStr("SubMap") -> EMap(Ty("SubMap"), Nil),
        EStr("BindingObject") -> e
      ))
    } | ("a new" | "a newly created") ~> ty <~ opt(("with" | "that" | "containing") ~ rest) ^^ {
      case t => EMap(t, List(EStr("SubMap") -> EMap(Ty("SubMap"), Nil))) // TODO handle after "with" or "that"
    } | ("a value of type reference whose base value component is" ~> expr) ~
      (", whose referenced name component is" ~> expr) ~
      (", and whose strict reference flag is" ~> expr) ^^ {
        case b ~ r ~ s => EMap(Ty("Reference"), List(
          EStr("BaseValue") -> b,
          EStr("ReferencedName") -> r,
          EStr("StrictReference") -> s
        ))
      } | opt("the") ~> ty ~ ("{" ~> repsep((name <~ ":") ~ expr, ",") <~ "}") ^^ {
        case t ~ list => EMap(t, list.map { case x ~ e => (EStr(x), e) })
      }

  // list expressions
  lazy val listExpr =
    "«" ~> repsep(expr, ",") <~ "»" ^^ {
      case list => EList(list)
    }

  // current expressions
  lazy val curExpr =
    "the current Realm Record" ^^^ {
      parseExpr(s"$context.Realm")
    }

  // algorithm expressions
  lazy val algoExpr =
    "an empty sequence of algorithm steps" ^^^ {
      EFunc(Nil, emptyInst)
    }

  // type expressions
  lazy val typeExpr =
    ("Number" | "Undefined" | "Null" | "String" | "Boolean" | "Symbol" | "Reference") ^^ {
      case tname => EStr(tname.head)
    } |
      ty ^^ {
        case Ty(name) => EStr(name)
      }

  // et cetera expressions
  lazy val etcExpr =
    "the algorithm steps specified in" ~> secno ~> "for the" ~> name <~ "function" ^^ {
      case x => ERef(RefId(Id(x)))
    } | "the number whose value is MV of" ~> name <~ rest ^^ {
      case x => parseExpr(s"(run StringToNumber of $x)")
    } | ("the" ~> id <~ "flag of") ~ id ^^ {
      case e1 ~ e2 if e1 == "withEnvironment" => EBool(false) // TODO : support withEnvironment flag in Object Environment
    } | ("the result of applying the addition operation to" ~> id <~ "and") ~ id ^^ {
      case e1 ~ e2 => EBOp(OPlus, ERef(RefId(Id(e1))), ERef(RefId(Id(e2))))
    } | ("the result of applying the multiplicativeoperator" <~ rest) ^^ {
      case _ => parseExpr(s"( MulOperation (get-syntax MultiplicativeOperator) lnum rnum)")
    } | ("the result of applying the subtraction operation to" <~ rest) ^^ {
      case _ => EBOp(OSub, ERef(RefId(Id("lnum"))), ERef(RefId(Id("rnum"))))
    }

  // reference expressions
  lazy val refExpr =
    ref ^^ {
      case r => ERef(r)
    }

  ////////////////////////////////////////////////////////////////////////////////
  // Conditions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val cond: Parser[Expr] =
    (id <~ "is not present") ~ subCond ^^ {
      case x ~ f => f(EUOp(ONot, EExist(RefId(Id(x)))))
    } | (expr <~ "is different from") ~ expr ~ subCond ^^ {
      case x ~ y ~ f => f(EUOp(ONot, EBOp(OEq, x, y)))
    } | (expr <~ "and") ~ expr <~ "have different results" ^^ {
      case x ~ y => EUOp(ONot, EBOp(OEq, x, y))
    } | (expr <~ "and") ~ (expr <~ "are the same object value") ~ subCond ^^ {
      case x ~ y ~ f => f(EBOp(OEq, x, y))
    } | expr ~ "is not the ordinary object internal method defined in" ~ secno ^^^ {
      EBool(false) // TODO fix
    } | (ref <~ "does not have an own property with key") ~ expr ^^ {
      case r ~ p => EUOp(ONot, EExist(RefProp(RefProp(r, EStr("SubMap")), p)))
    } | (ref <~ "has a binding for the name that is the value of") ~ expr ^^ {
      case r ~ p => EExist(RefProp(RefProp(r, EStr("SubMap")), p))
    } | (expr <~ "is not") ~ expr ~ subCond ^^ {
      case l ~ r ~ f => f(EUOp(ONot, EBOp(OEq, l, r)))
    } | ("both" ~> ref <~ "and") ~ (ref <~ "are absent") ^^ {
      case l ~ r => EBOp(OAnd, EUOp(ONot, EExist(l)), EUOp(ONot, EExist(r)))
    } | (opt("both") ~> expr <~ "and") ~ (expr <~ "are" <~ opt("both")) ~ expr ^^ {
      case l ~ r ~ e => EBOp(OAnd, EBOp(OEq, l, e), EBOp(OEq, r, e))
    } | expr <~ "is neither an objectliteral nor an arrayliteral" ^^ {
      case e => EUOp(ONot, EBOp(OOr, EIsInstanceOf(e, "ObjectLiteral"), EIsInstanceOf(e, "ArrayLiteral")))
    } | expr <~ "is a data property" ^^ {
      case e => EBOp(OEq, ETypeOf(e), EStr("DataProperty"))
    } | expr <~ "is an object" ^^ {
      case e => EBOp(OEq, ETypeOf(e), EStr("OrdinaryObject"))
    } | ("either" ~> cond) ~ ("or" ~> cond) ^^ {
      case c1 ~ c2 => EBOp(OAnd, c1, c2)
    } | expr <~ "is Boolean, String, Symbol, or Number" ^^ {
      case e => EBOp(OOr, EBOp(OEq, e, EStr("Boolean")), EBOp(OOr, EBOp(OEq, e, EStr("String")), EBOp(OOr, EBOp(OEq, e, EStr("Symbol")), EBOp(OEq, e, EStr("Number"))))) // TODO : remove side effect
    } | "every field in" ~> id <~ "is absent" ^^ {
      case x => EBOp(OEq, ERef(RefId(Id(x))), EMap(Ty("PropertyDescriptor"), List()))
    } | (expr <~ "is") ~ expr ~ subCond ^^ {
      case l ~ r ~ f => f(EBOp(OEq, l, r))
    }

  lazy val subCond: Parser[Expr => Expr] =
    "or" ~> opt("if") ~> cond ^^ {
      case r => (l: Expr) => EBOp(OOr, l, r)
    } | "and" ~> opt("if") ~> cond ^^ {
      case r => (l: Expr) => EBOp(OAnd, l, r)
    } | success(x => x)

  ////////////////////////////////////////////////////////////////////////////////
  // Types
  ////////////////////////////////////////////////////////////////////////////////
  lazy val ty: Parser[Ty] =
    "realm record" ^^^ Ty("RealmRecord") |
      "record" ^^^ Ty("Record") |
      "built-in function object" ^^^ Ty("BuiltinFunctionObject") |
      "propertydescriptor" ^^^ Ty("PropertyDescriptor") |
      "property descriptor" ^^^ Ty("PropertyDescriptor") |
      "execution context" ^^^ Ty("ExecutionContext") |
      "lexical environment" ^^^ Ty("LexicalEnvironment") |
      "object environment record" ^^^ Ty("ObjectEnvironmentRecord") |
      "object" ^^^ Ty("OrdinaryObject") |
      "declarative environment record" ^^^ Ty("DeclarativeEnvironmentRecord") |
      "global environment record" ^^^ Ty("GlobalEnvironmentRecord") |
      "completion" ^^^ Ty("Completion")

  ////////////////////////////////////////////////////////////////////////////////
  // References
  ////////////////////////////////////////////////////////////////////////////////
  lazy val ref: Parser[Ref] =
    "the outer lexical environment reference of" ~> ref ^^ {
      case r => RefProp(r, EStr("Outer"))
    } | "the base value component of" ~> name ^^ {
      case x => parseRef(s"$x.BaseValue")
    } | name <~ "'s base value component" ^^ {
      case x => parseRef(s"$x.BaseValue")
    } | "the strict reference flag of" ~> name ^^ {
      case x => parseRef(s"$x.StrictReference")
    } | "the referenced name component of" ~> name ^^ {
      case x => parseRef(s"$x.ReferencedName")
    } | "the binding object for" ~> name ^^ {
      case x => parseRef(s"$x.BindingObject")
    } | ("the value of") ~> ref ^^ {
      case r => r
    } | ("the" ~> name <~ "of") ~ ref ^^ {
      case x ~ r => RefProp(r, EStr(x))
    } | (name <~ "'s own property whose key is") ~ expr ^^ {
      case r ~ p => RefProp(RefProp(RefId(Id(r)), EStr("SubMap")), p)
    } | (name <~ "'s") ~ name <~ opt("attribute") ^^ {
      case b ~ x => RefProp(RefId(Id(b)), EStr(x))
    } | name ~ rep(field) ^^ {
      case x ~ es => es.foldLeft[Ref](RefId(Id(x))) {
        case (r, e) => RefProp(r, e)
      }
    }

  ////////////////////////////////////////////////////////////////////////////////
  // Fields
  ////////////////////////////////////////////////////////////////////////////////
  lazy val field: Parser[Expr] =
    "." ~> name ^^ {
      case x => EStr(x)
    } | "[" ~> expr <~ "]" ^^ {
      case e => e
    }

  ////////////////////////////////////////////////////////////////////////////////
  // Section Numbers
  ////////////////////////////////////////////////////////////////////////////////
  lazy val secno: Parser[List[Int]] =
    number ~ rep("." ~> number) ^^ {
      case n ~ list => n.toInt :: list.map(_.toInt)
    }

  ////////////////////////////////////////////////////////////////////////////////
  // Names
  ////////////////////////////////////////////////////////////////////////////////
  lazy val name: Parser[String] = (
    "outer environment reference" ^^^ "Outer" |
    "the running execution context" ^^^ context |
    "the" ~ ty ~ "for which the method was invoked" ^^^ "this" |
    word |
    "%" ~> word <~ "%" |
    "[[" ~> word <~ "]]" |
    "[[%" ~> word <~ "%]]" |
    id
  )
}
