package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.parser.TokenParsers
import kr.ac.kaist.ase.algorithm.{ Algorithm, Token, RuntimeSemantics }

case class AlgoCompiler(algoName: String, algo: Algorithm) extends TokenParsers {
  var foreachCount: Int = 0
  def result: Func = {
    val (params, varparam) = handleParams(algo.params)
    Func(
      name = algoName,
      params = params,
      varparam = varparam,
      body = ISeq(parseAll(stmts, algo.toTokenList) match {
        case Success(res, _) => res
        case NoSuccess(_, reader) => error(s"[AlgoCompiler]:${algo.filename}: $reader")
      })
    )
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
      case tokens => itodo(tokens.mkString(" ").replace("\\", "\\\\").replace("\"", "\\\""))
    }
  ) // TODO flatten

  // execution context stack string
  val executionStack = "executionStack"
  val context = "context"

  ////////////////////////////////////////////////////////////////////////////////
  // Instructions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val stmt: Parser[Inst] =
    etcStmt |
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
    } | "return" ^^^ {
      IReturn(EMap(Ty("Completion"), List(
        EStr("Type") -> parseExpr("normal"),
        EStr("Value") -> EUndef,
        EStr("Target") -> parseExpr("empty")
      )))
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
    } | ("insert" ~> expr <~ "as the first element of") ~ expr ^^ {
      case x ~ y => IPush(x, y)
    }

  // et cetera statements
  lazy val etcStmt =
    "push" ~> expr <~ ("onto" | "on to") ~ "the execution context stack" ~ rest ^^ {
      case e => ISeq(List(IPush(e, ERef(RefId(Id(executionStack)))), parseInst(s"""
        $context = $executionStack[(- $executionStack.length 1i)]
      """)))
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
    } | "if declaration is declaration : hoistabledeclaration, then" ~> stmt ^^ {
      case s => IIf(EIsInstanceOf(parseExpr("Declaration"), "HoistableDeclaration"), ISeq(List(parseInst("let HoistableDeclaration = Declaration"), s)), ISeq(Nil))
    } | "if statement is statement : labelledstatement , return toplevelvardeclarednames of statement ." ^^^ {
      parseInst(s"""if (is-instance-of Statement LabelledStatement) return Statement.TopLevelVarDeclaredNames else {}""")
    } | "suspend" ~ name ~ "and remove it from the execution context stack" ^^^ {
      parseInst(s"""{
        $context = null
        (pop $executionStack)
      }""")
    } | "suspend the currently running execution context" ^^^ {
      parseInst(s"""$context = null""")
    } | "resume the context that is now on the top of the execution context stack as the running execution context" ^^^ {
      parseInst(s"""$context = $executionStack[(- $executionStack.length 1i)]""")
    } | "let" ~> name <~ "be a newly created ecmascript function object with the internal slots listed in table 27. all of those internal slots are initialized to" ~ value ^^ {
      case x => parseInst(s"""{
        let $x = (new ECMAScriptFunctionObject("SubMap" -> (new SubMap())))
        $x.Call = undefined
        $x.Constructor = undefined
      }""")
    } | "let" ~> name <~ "be the topmost execution context on the execution context stack whose scriptormodule component is not" ~ value ~ "." ~ next ~ "if no such execution context exists, return" ~ value ~ ". otherwise, return" ~ name ~ "'s scriptormodule component." ^^ {
      case x => ISeq(List(
        iForeach(Id(x), parseExpr(executionStack), parseInst(s"""{
          if (! (= $x.ScriptOrModule null)) return $x.ScriptOrModule
        }"""), true),
        parseInst("return null")
      ))
    } | "otherwise , let " ~ id ~ "," ~ id ~ ", and" ~ id ~ "be integers such that" ~ id ~ "≥ 1" ~ rest ^^^ {
      parseInst(s"""return m.getString""")
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
  lazy val valueExpr = opt("the value" | "the string") ~> value ^^ {
    case "null" => ENull
    case "true" => EBool(true)
    case "false" => EBool(false)
    case "NaN" => ENum(Double.NaN)
    case "+0" => ENum(0.0)
    case "-0" => ENum(-0.0)
    case "+∞" => ENum(Double.PositiveInfinity)
    case "undefined" => EUndef
    case s if s.startsWith("\"") && s.endsWith("\"") => EStr(s.slice(1, s.length - 1))
    case err @ ("TypeError" | "ReferenceError") => EMap(Ty(err), Nil)
    case s => etodo(s)
  } | const ^^ {
    case "[empty]" => ERef(RefId(Id("emptySyntax")))
    case const => ERef(RefId(Id(const.replaceAll("-", ""))))
  } | number ^^ { case s => EINum(s.toLong) }

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
    } | ("the result of performing" ~> name <~ "for") ~ name ~ ("with argument" ~> expr) ^^ {
      case f ~ x ~ a => EApp(parseExpr(s"$x.$f"), List(a))
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
      EFunc(Nil, None, emptyInst)
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
    } | ("the" ~> name <~ "that is covered by") ~ expr ^^ {
      case r ~ e => EParseSyntax(e, r)
    } | "CoveredCallExpression of CoverCallExpressionAndAsyncArrowHead" ^^^ {
      parseExpr("(parse-syntax CoverCallExpressionAndAsyncArrowHead CallMemberExpression)")
    } | ("the larger of" ~> expr <~ "and") ~ expr ^^ {
      case x ~ y => etodo(s"larger of $x and $y")
    } | "the steps of an" ~> name <~ "function as specified below" ^^ {
      case x => parseExpr(s"$x")
    } | "the number whose value is MV of" ~> name <~ rest ^^ {
      case x => parseExpr(s"$x.getNumber")
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
    } | ("the string - concatenation of" ~> expr <~ "and") ~ expr ^^ {
      case e1 ~ e2 => EBOp(OPlus, e1, e2)
    } | ("the string - concatenation of" ~> expr <~ "and !") ~ expr ^^ {
      case e1 ~ e2 => EBOp(OPlus, e1, e2) // TODO : support !
    } | "the definition specified in 9.2.1" ^^^ {
      parseExpr("ECMAScriptFunctionObjectDOTCall")
    } | "the definition specified in 9.2.2" ^^^ {
      parseExpr("ECMAScriptFunctionObjectDOTConstruct")
    } | ("the token") ~> value ^^ {
      case x => EStr(x)
    } | "-" ~> expr ^^ {
      case e => EUOp(ONeg, e)
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
      "the function code for" ~ opt("the") ~ name ~ "is strict mode code" |
      "the code matching the syntactic production that is being evaluated is contained in strict mode code") ^^^ {
        EBool(false) // TODO : support strict mode code
      } | (ref <~ "is" ~ ("not present" | "absent")) ~ subCond ^^ {
        case r ~ f => f(EUOp(ONot, EExist(r)))
      } | (expr <~ "is less than zero") ~ subCond ^^ {
        case x ~ f => f(EBOp(OLt, x, ENum(0)))
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
      } | expr <~ "is empty" ^^ {
        case e => parseExpr(s"(= ${beautify(e)}.length 0)")
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
      } | (expr <~ "is") ~ expr ~ ("or" ~> expr) ~ subCond ^^ {
        case e ~ l ~ r ~ f => f(EBOp(OOr, EBOp(OEq, e, l), EBOp(OEq, e, r)))
      }

  lazy val subCond: Parser[Expr => Expr] =
    "or" ~> opt("if") ~> cond ^^ {
      case r => (l: Expr) => EBOp(OOr, l, r)
    } | "and" ~> opt("if") ~> cond ^^ {
      case r => (l: Expr) => EBOp(OAnd, l, r)
    } | guard("," ^^^ { x => x })

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
    } | "the sole element of" ~> ref ^^ {
      case x => RefProp(x, EINum(0))
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
      parseRef(s"StringLiteral.getString")
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
    "the execution context stack" ^^^ executionStack |
    "the" ~ ty ~ "for which the method was invoked" ^^^ "this" |
    "[[" ~> word <~ "]]" |
    opt("the intrinsic object") ~> ("%" ~> word <~ "%" | "[[%" ~> word <~ "%]]") ^^ { case x => s"INTRINSIC_$x" } |
    word |
    id
  )
}
