package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.parser.TokenParsers
import kr.ac.kaist.ase.algorithm.{ Algorithm, Token, RuntimeSemantics }

case class AlgoCompiler(algoName: String, algo: Algorithm) extends TokenParsers {
  var foreachCount: Int = 0
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
  def iForeach(id: Id, expr: Expr, body: Inst, reversed: Boolean = false) = {
    val f = IForeach(id, expr, body, foreachCount, reversed)
    foreachCount = foreachCount + 1
    f
  }

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
    stmt <~ opt(("as defined" <~ rest) | (("(" | ".") <~ "see" <~ rest)) <~ opt(".") <~ next | step ^^ {
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
      forEachStmt |
      pushStmt |
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
    ("if" ~> cond <~ "," <~ opt("then")) ~ stmt ~ (opt("." | ";" | ",") ~> opt(next) ~>
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

  // for-each statements
  lazy val forEachStmt =
    ("for each" ~ opt("string" | "element" | "parse node") ~> id) ~ ("in" ~> expr <~ "," ~ opt("in list order,") ~ "do") ~ stmt ^^ {
      case x ~ e ~ i => iForeach(Id(x), e, i)
    } | ("for each" ~> id) ~ ("in" ~> expr <~ ", in reverse list order , do") ~ stmt ^^ {
      case x ~ e ~ i => iForeach(Id(x), e, i, true)
    }

  // push statements
  lazy val pushStmt =
    ("append" ~> expr) ~ ("to" ~> expr) ^^ {
      case x ~ y => IPush(x, y)
    } | ("append to" ~> expr <~ "the elements of") ~ expr ^^ {
      case l1 ~ l2 => iForeach(Id("temp"), l2, IPush(ERef(RefId(Id("temp"))), l1))
    }

  // et cetera statements
  lazy val etcStmt =
    "push" ~> expr <~ ("onto" | "on to") ~ "the execution context stack" ~ rest ^^ {
      case e => IAssign(RefId(Id(context)), e)
    } | "in an implementation - dependent manner , obtain the ecmascript source texts" ~ rest ~ next ~ rest ^^^ {
      parseInst(s"""return (ScriptEvaluationJob script hostDefined)""")
    } | "if the host requires use of an exotic object" ~ rest ^^^ {
      parseInst("let global = undefined")
    } | "if the host requires that the" ~ rest ^^^ {
      parseInst("let thisValue = undefined")
    } | "for each field of" ~ rest ^^^ {
      parseInst(s"""O.SubMap[P].Value = Desc.Value""") // TODO: move each field of record at ValidateAndApplyPropertyDescriptor
    } | "parse" ~ id ~ "using script as the goal symbol and analyse the parse result for any early Error conditions" ~ rest ^^^ {
      parseInst(s"""let body = script""")
    } | "if statement is statement : labelledstatement , return toplevelvardeclarednames of statement ." ^^^ {
      parseInst(s"""if (is-instance-of Statement LabelledStatement) return Statement.TopLevelVarDeclaredNames else {}""")
    }

  // ignore statements
  lazy val ignoreStmt = (
    "assert:" |
    "note:" |
    "set fields of" |
    "for each property of the global object" | // TODO : set global object properties
    "create any implementation-defined" |
    "no further validation is required" | // TODO : should implement goto?? see ValidateAndApplyPropertyDescriptor
    "if" ~ id ~ "is a List of errors," |
    "suspend the currently running execution context" | // TODO : should be re-considered.
    "suspend" ~ id ~ "and remove it from the execution context stack" | // TODO : should be re-considered.
    "resume the context that is now on the top of the execution context stack as the running execution context" | // TODO : should be re-considered.
    "record that" // TODO : should be re-considered.
  ) ~ rest ^^^ emptyInst

  ////////////////////////////////////////////////////////////////////////////////
  // Expressions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val expr: Parser[Expr] =
    etcExpr |
      valueExpr |
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
    case "NaN" => ENum(Double.NaN)
    case "+0" => ENum(0.0)
    case "-0" => ENum(-0.0)
    case "undefined" => EUndef
    case s if s.startsWith("\"") && s.endsWith("\"") => EStr(s.slice(1, s.length - 1))
    case const @ ("empty" | "throw" | "normal") => ERef(RefId(Id(const.replaceAll("-", ""))))
    case err @ ("TypeError" | "ReferenceError") => EMap(Ty(err), Nil)
    case s => etodo(s)
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
    } | "a new list containing" ~> expr ^^ {
      case e => EList(List(e))
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
      case x => parseExpr(s"$x.toNumber")
    } | ("the" ~> id <~ "flag of") ~ id ^^ {
      case e1 ~ e2 if e1 == "withEnvironment" => EBool(false) // TODO : support withEnvironment flag in Object Environment
    } | ("the result of applying the addition operation to" ~> id <~ "and") ~ id ^^ {
      case e1 ~ e2 => EBOp(OPlus, ERef(RefId(Id(e1))), ERef(RefId(Id(e2))))
    } | ("the result of applying the multiplicativeoperator" <~ rest) ^^ {
      case _ => parseExpr(s"( MulOperation (get-syntax MultiplicativeOperator) lnum rnum)")
    } | ("the result of applying the subtraction operation to" <~ rest) ^^ {
      case _ => EBOp(OSub, ERef(RefId(Id("lnum"))), ERef(RefId(Id("rnum"))))
    } | (("the result of performing abstract equality comparison" ~> id <~ "= =") ~ id) ^^ {
      case x1 ~ x2 => EApp(ERef(RefId(Id("AbstractEqualityComparison"))), List(ERef(RefId(Id(x1))), ERef(RefId(Id(x2)))))
    } | (("the result of performing strict equality comparison" ~> id <~ "= = =") ~ id) ^^ {
      case x1 ~ x2 => EApp(ERef(RefId(Id("StrictEqualityComparison"))), List(ERef(RefId(Id(x1))), ERef(RefId(Id(x2)))))
    } | ("the result of negating" ~> id <~ rest) ^^ {
      case x => EUOp(ONeg, ERef(RefId(Id(x))))
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
    (("the code matched by this" ~> word <~ "is strict mode code") |
      "the code matching the syntactic production that is being evaluated is contained in strict mode code") ^^^ {
        EBool(false) // TODO : support strict mode code
      } | (id <~ "is not present") ~ subCond ^^ {
        case x ~ f => f(EUOp(ONot, EExist(RefId(Id(x)))))
      } | (expr <~ "is different from") ~ expr ~ subCond ^^ {
        case x ~ y ~ f => f(EUOp(ONot, EBOp(OEq, x, y)))
      } | (expr <~ "is not an element of") ~ expr ^^ {
        case x ~ y => EUOp(ONot, EContains(y, x))
      } | (expr <~ "does not contain") ~ expr ^^ {
        case x ~ y => EUOp(ONot, EContains(x, y))
      } | (expr <~ "and") ~ expr <~ "have different results" ^^ {
        case x ~ y => EUOp(ONot, EBOp(OEq, x, y))
      } | (expr <~ "and") ~ (expr <~ "are the same object value") ~ subCond ^^ {
        case x ~ y ~ f => f(EBOp(OEq, x, y))
      } | ("the" ~> name <~ "fields of") ~ name ~ ("and" ~> name <~ "are the boolean negation of each other") ^^ {
        case x ~ y ~ z => parseExpr(s"""(|| (&& (= $y.$x true) (= $z.$x false)) (&& (= $y.$x false) (= $z.$x true)))""")
      } | (expr <~ "and") ~ (expr <~ ("are the same object value" | "are exactly the same sequence of code units ( same length and same code units at corresponding indices )")) ~ subCond ^^ {
        case x ~ y ~ f => f(EBOp(OEq, x, y))
      } | expr ~ "is not the ordinary object internal method defined in" ~ secno ^^^ {
        EBool(false) // TODO fix
      } | (ref <~ "does not have an own property with key") ~ expr ^^ {
        case r ~ p => EUOp(ONot, EExist(RefProp(RefProp(r, EStr("SubMap")), p)))
      } | (ref <~ "has a") ~ word <~ "component" ^^ {
        case r ~ n => EExist(RefProp(r, EStr(n)))
      } | (ref <~ "has a binding for the name that is the value of") ~ expr ^^ {
        case r ~ p => EExist(RefProp(RefProp(r, EStr("SubMap")), p))
      } | (ref <~ "is present and its value is") ~ expr ^^ {
        case r ~ e => EBOp(OAnd, EExist(r), EBOp(OEq, ERef(r), e))
      } | (ref <~ "is present") ~ subCond ^^ {
        case r ~ f => f(EExist(r))
      } | (expr <~ "is not") ~ expr ~ subCond ^^ {
        case l ~ r ~ f => f(EUOp(ONot, EBOp(OEq, l, r)))
      } | ("both" ~> ref <~ "and") ~ (ref <~ "are absent") ^^ {
        case l ~ r => EBOp(OAnd, EUOp(ONot, EExist(l)), EUOp(ONot, EExist(r)))
      } | (opt("both") ~> expr <~ "and") ~ (expr <~ "are" <~ opt("both")) ~ expr ^^ {
        case l ~ r ~ e => EBOp(OAnd, EBOp(OEq, l, e), EBOp(OEq, r, e))
      } | expr <~ "is neither an objectliteral nor an arrayliteral" ^^ {
        case e => EUOp(ONot, EBOp(OOr, EIsInstanceOf(e, "ObjectLiteral"), EIsInstanceOf(e, "ArrayLiteral")))
      } | expr <~ "is neither a variabledeclaration nor a forbinding nor a bindingidentifier" ^^ {
        case e => EUOp(ONot, EBOp(OOr, EBOp(OOr, EIsInstanceOf(e, "VariableDeclaration"), EIsInstanceOf(e, "ForBinding")), EIsInstanceOf(e, "BindingIdentifier")))
      } | expr <~ "is a variabledeclaration , a forbinding , or a bindingidentifier" ^^ {
        case e => EBOp(OOr, EBOp(OOr, EIsInstanceOf(e, "VariableDeclaration"), EIsInstanceOf(e, "ForBinding")), EIsInstanceOf(e, "BindingIdentifier"))
      } | "statement is statement : labelledstatement" ^^^ {
        EIsInstanceOf(ERef(RefId(Id("Statement"))), "LabelledStatement")
      } | expr <~ "is a data property" ^^ {
        case e => EBOp(OEq, ETypeOf(e), EStr("DataProperty"))
      } | expr <~ "is an object" ^^ {
        case e => EBOp(OEq, ETypeOf(e), EStr("OrdinaryObject"))
      } | ("either" ~> cond) ~ ("or" ~> cond) ^^ {
        case c1 ~ c2 => EBOp(OOr, c1, c2)
      } | expr <~ "is Boolean, String, Symbol, or Number" ^^ {
        case e => EBOp(OOr, EBOp(OEq, e, EStr("Boolean")), EBOp(OOr, EBOp(OEq, e, EStr("String")), EBOp(OOr, EBOp(OEq, e, EStr("Symbol")), EBOp(OEq, e, EStr("Number"))))) // TODO : remove side effect
      } | "every field in" ~> id <~ "is absent" ^^ {
        case x => parseExpr(s"""(!
          (|| (? $x.Value)
          (|| (? $x.Writable)
          (|| (? $x.Get)
          (|| (? $x.Set)
          (|| (? $x.Enumerable)
          (? $x.Configurable)))))))""")
      } | (expr <~ ("is the same as" | "is the same Number value as" | "is")) ~ expr ~ subCond ^^ {
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
      opt("ecmascript code") ~ "execution context" ^^^ Ty("ExecutionContext") |
      "lexical environment" ^^^ Ty("LexicalEnvironment") |
      "object environment record" ^^^ Ty("ObjectEnvironmentRecord") |
      "object" ^^^ Ty("OrdinaryObject") |
      "declarative environment record" ^^^ Ty("DeclarativeEnvironmentRecord") |
      "global environment record" ^^^ Ty("GlobalEnvironmentRecord") |
      "completion" ^^^ Ty("Completion") |
      "script record" ^^^ Ty("ScriptRecord")

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
    } | opt("the") ~> "stringvalue of identifiername" ^^^ {
      parseRef(s"IdentifierName")
    } | "the stringvalue of stringliteral" ^^^ {
      parseRef(s"StringLiteral") // TODO : SV of stringLiteral ( see 11.8.4.1 )
    } | "the result of evaluating" ~> nameWithOrdinal ^^ {
      case x => parseRef(s"$x.Evaluation")
    } | "IsFunctionDefinition of" ~> id ^^ {
      case x => parseRef(s"$x.IsFunctionDefinition")
    } | (opt("the") ~> name <~ opt("fields") ~ "of") ~ nameWithOrdinal ^^ {
      case x ~ y => parseRef(s"$y.$x")
    } | (name <~ "'s own property whose key is") ~ expr ^^ {
      case r ~ p => RefProp(RefProp(RefId(Id(r)), EStr("SubMap")), p)
    } | (name <~ "'s") ~ name <~ opt("attribute") ^^ {
      case b ~ x => RefProp(RefId(Id(b)), EStr(x))
    } | name ~ rep(field) ^^ {
      case x ~ es => es.foldLeft[Ref](RefId(Id(x))) {
        case (r, e) => RefProp(r, e)
      }
    }

  lazy val nameWithOrdinal =
    "the first" ~> word ^^ {
      case x => x + "0"
    } | "the second" ~> word ^^ {
      case x => x + "1"
    } | name

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
    "%" ~> word <~ "%" ^^ { case x => s"INTRINSIC__$word" } |
    "[[" ~> word <~ "]]" |
    "[[%" ~> word <~ "%]]" |
    id
  )
}
