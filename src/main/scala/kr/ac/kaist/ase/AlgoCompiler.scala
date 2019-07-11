package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.algorithm.{ Algorithm, Token, RuntimeSemantics }
import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.parser.TokenParsers
import kr.ac.kaist.ase.util.Useful._

case class AlgoCompiler(algoName: String, algo: Algorithm) extends TokenParsers {
  def result: Func = {
    val (params, varparam) = handleParams(algo.params)
    Func(
      name = algoName,
      params = params,
      varparam = varparam,
      body = flatten(ISeq(parseAll(stmts, algo.toTokenList) match {
        case Success(res, _) => res
        case NoSuccess(_, reader) => error(s"[AlgoCompiler]:${algo.filename}: $reader")
      }))
    )
  }

  // empty instruction
  lazy val emptyInst: Inst = ISeq(Nil)

  // list of statements
  lazy val stmts: Parser[List[Inst]] = rep(
    stmt <~ opt(".") <~ opt(commentStmt) <~ next |
      step ^^ {
        case tokens => IExpr(ENotYetImpl(tokens.mkString(" ").replace("\\", "\\\\").replace("\"", "\\\"")))
      }
  )

  // execution context stack string
  val executionStack = "GLOBAL_executionStack"
  val context = "GLOBAL_context"

  ////////////////////////////////////////////////////////////////////////////////
  // Instructions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val stmt: Parser[Inst] = (
    etcStmt |
    ignoreStmt |
    commentStmt |
    returnStmt |
    letStmt |
    innerStmt |
    ifStmt |
    callStmt |
    setStmt |
    recordStmt |
    createStmt |
    throwStmt |
    whileStmt |
    forEachStmt |
    appendStmt |
    insertStmt |
    removeStmt
  )

  // return statements
  lazy val returnStmt = (
    "return" ~> expr ^^ {
      case i ~ e => ISeq(i :+ (algo.kind match {
        case RuntimeSemantics if !(algoName contains "InstantiateFunctionObject") =>
          IReturn(EApp(ERef(RefId(Id("WrapCompletion"))), List(e)))
        case _ => IReturn(e)
      }))
    } | "return" ^^^ {
      IReturn(EMap(Ty("Completion"), List(
        EStr("Type") -> parseExpr("CONST_normal"),
        EStr("Value") -> EUndef,
        EStr("Target") -> parseExpr("CONST_empty")
      )))
    }
  )

  // let statements
  lazy val letStmt =
    ("let" ~> id <~ "be") ~ (expr <~ "; if") ~ (cond <~ ", use") ~ expr ^^ {
      case x ~ (i1 ~ e1) ~ (i2 ~ e2) ~ (i3 ~ e3) =>
        ISeq(i2 :+ IIf(e2, ISeq(i3 :+ ILet(Id(x), e3)), ISeq(i1 :+ ILet(Id(x), e1))))
    } |
      ("let" ~> id <~ "be") ~ expr ^^ {
        case x ~ (i ~ e) => ISeq(i :+ ILet(Id(x), e))
      }

  // inner statements
  lazy val innerStmt =
    in ~> stmts <~ out ^^ {
      case list => ISeq(list)
    }

  // if-then-else statements
  lazy val ifStmt =
    ("if" ~> cond <~ "," <~ opt("then")) ~ stmt ~ (
      opt("." | ";" | ",") ~> opt(next) ~> ("else" | "otherwise") ~> opt(
        cond |
          name ~ "is a Reference to an Environment Record binding" |
          "the base of" ~ ref ~ "is an Environment Record" |
          name ~ "must be" ~ rep(not(",") ~ text) |
          id ~ "does not currently have a property" ~ id |
          id <~ "is an accessor property" |
          ("isaccessordescriptor(" ~> id <~ ") and isaccessordescriptor(") ~ (id <~ ") are both") ~ expr
      ) ~> opt(",") ~> stmt
    ) ^^ {
          case (i ~ c) ~ t ~ e => ISeq(i :+ IIf(c, t, e))
        } | ("if" ~> cond <~ "," <~ opt("then")) ~ stmt ^^ {
          case (i ~ c) ~ t => ISeq(i :+ IIf(c, t, emptyInst))
        }

  // call statements
  lazy val callStmt = ("perform" | "call") ~> expr ^^ {
    case i ~ e => ISeq(i :+ IExpr(e))
  } | returnIfAbruptExpr ^^ {
    case i ~ e => ISeq(i :+ IExpr(e))
  }

  // set statements
  lazy val setStmt =
    "set" ~ name ~ "'s essential internal methods" ~ rest ^^ {
      case _ => emptyInst
    } | ("set" ~> ref) ~ ("to" ~> expr) ^^ {
      case (i0 ~ r) ~ (i1 ~ e) => ISeq(i0 ++ i1 :+ IAssign(r, e))
    } | ("set the bound value for" ~> expr <~ "in") ~ expr ~ ("to" ~> expr) ^^ {
      case (i0 ~ p) ~ (i1 ~ e) ~ (i2 ~ v) =>
        ISeq(i0 ++ i1 ++ i2 :+ parseInst(s"${beautify(e)}.SubMap[${beautify(p)}].BoundValue = ${beautify(v)}"))
    } | ("set" ~> ref) ~ ("as specified in" ~> (
      "9.4.4.1" ^^^ parseExpr(getScalaName("ArgumentsExoticObject.GetOwnProperty")) |
      "9.4.4.2" ^^^ parseExpr(getScalaName("ArgumentsExoticObject.DefineOwnProperty")) |
      "9.4.4.3" ^^^ parseExpr(getScalaName("ArgumentsExoticObject.Get")) |
      "9.4.4.4" ^^^ parseExpr(getScalaName("ArgumentsExoticObject.Set")) |
      "9.4.4.5" ^^^ parseExpr(getScalaName("ArgumentsExoticObject.Delete")) |
      "9.4.2.1" ^^^ parseExpr(getScalaName("ArrayExoticObject.DefineOwnProperty"))
    )) ^^ {
        case (i ~ r) ~ e => ISeq(i :+ IAssign(r, e))
      }

  // record statements
  lazy val recordStmt =
    ("record that the binding for" ~> name <~ "in") ~ name <~ "has been initialized" ^^ {
      case x ~ y => parseInst(s"if (! (= $y.SubMap[$x] absent)) $y.SubMap[$x].initialized = true else {}")
    }

  // create statements
  lazy val createStmt =
    "create an own data property" ~ rest ^^^ {
      parseInst(s"""{
        dp = (new DataProperty())
        if (! (= absent Desc.Value)) dp.Value = Desc.Value else dp.Value = undefined
        if (! (= absent Desc.Writable)) dp.Writable = Desc.Writable else dp.Writable = false
        if (! (= absent Desc.Enumerable)) dp.Enumerable = Desc.Enumerable else dp.Enumerable = false
        if (! (= absent Desc.Configurable)) dp.Configurable = Desc.Configurable else dp.Configurable = false
        O.SubMap[P] = dp
      }""")
    } | "create an own accessor property" ~ rest ^^^ {
      parseInst(s"""{
        dp = (new AccessorProperty())
        if (! (= absent Desc.Get)) dp.Get = Desc.Get else dp.Get = undefined
        if (! (= absent Desc.Set)) dp.Set = Desc.Set else dp.Set = undefined
        if (! (= absent Desc.Enumerable)) dp.Enumerable = Desc.Enumerable else dp.Enumerable = false
        if (! (= absent Desc.Configurable)) dp.Configurable = Desc.Configurable else dp.Configurable = false
        O.SubMap[P] = dp
      }""")
    } | ("create a mutable binding in" ~> name <~ "for") ~ name <~ "and record that it is uninitialized" ~ rest ^^ {
      case x ~ y => parseInst(s"""$x.SubMap[$y] = (new MutableBinding("initialized" -> false))""")
    } | ("create an immutable binding in" ~> name <~ "for") ~ name <~ "and record that it is uninitialized" ~ rest ^^ {
      case x ~ y => parseInst(s"""$x.SubMap[$y] = (new ImmutableBinding("initialized" -> false))""")
    }

  // throw statements
  lazy val throwStmt =
    "throw a" ~> valueExpr <~ "exception" ^^ {
      case e => IReturn(EMap(Ty("Completion"), List(
        EStr("Type") -> parseExpr("CONST_throw"),
        EStr("Value") -> e,
        EStr("Target") -> parseExpr("CONST_empty")
      )))
    }

  // while statements
  lazy val whileStmt =
    ("repeat, while" ~> cond <~ opt(",")) ~ stmt ^^ {
      case (i ~ c) ~ s => ISeq(i :+ IWhile(c, s))
    } | "repeat," ~> stmt ^^ {
      case s => IWhile(EBool(true), s)
    }

  // for-each statements
  lazy val forEachStmt =
    ("for each" ~ opt("classelement" | "string" | "element" | "parse node") ~> id) ~ (("in order from" | "in" | "of") ~> expr <~ opt(",") ~ opt("in list order,") ~ "do") ~ stmt ^^ {
      case x ~ (i ~ e) ~ b => ISeq(i :+ forEachList(Id(x), e, b))
    } | ("for each" ~> id) ~ ("in" ~> expr <~ ", in reverse list order , do") ~ stmt ^^ {
      case x ~ (i ~ e) ~ b => ISeq(i :+ forEachList(Id(x), e, b, true))
    }

  // append statements
  lazy val appendStmt = (
    ("append" ~> expr) ~ ("to" ~ opt("the end of") ~> expr) ^^ {
      case (i0 ~ x) ~ (i1 ~ y) => ISeq(i0 ++ i1 :+ IAppend(x, y))
    } | ("append to" ~> expr <~ opt("the elements of")) ~ expr ^^ {
      case (i0 ~ l1) ~ (i1 ~ l2) =>
        val tempId = getTempId
        ISeq(i0 ++ i1 :+ forEachList(tempId, l2, IAppend(ERef(RefId(tempId)), l1)))
    } | ("add" ~> expr <~ "as an element of the list") ~ expr ^^ {
      case (i0 ~ x) ~ (i1 ~ y) =>
        ISeq(i0 ++ i1 :+ IAppend(x, y))
    }
  )

  // append statements
  lazy val insertStmt = (
    ("insert" ~> expr <~ "as the first element of") ~ expr ^^ {
      case (i0 ~ x) ~ (i1 ~ y) =>
        ISeq(i0 ++ i1 :+ IPrepend(x, y))
    }
  )

  // remove statements
  lazy val removeStmt = (
    ("remove the own property with name" ~> name <~ "from") ~ name ^^ {
      case p ~ o => parseInst(s"delete $o.SubMap[$p]")
    } | ("remove the first element from" ~> name <~ "and let") ~ (name <~ "be the value of that element") ^^ {
      case l ~ x => parseInst(s"let $x = (pop $l 0i)")
    }
  )

  // et cetera statements
  lazy val etcStmt =
    "push" ~> expr <~ ("onto" | "on to") ~ "the execution context stack" ~ rest ^^ {
      case i ~ e => ISeq(i ++ List(IAppend(e, ERef(RefId(Id(executionStack)))), parseInst(s"""
        $context = $executionStack[(- $executionStack.length 1i)]
      """)))
    } | "in an implementation - dependent manner , obtain the ecmascript source texts" ~ rest ~ next ~ rest ^^^ {
      parseInst(s"""return (ScriptEvaluationJob script hostDefined)""")
    } | ("let" ~> id <~ "be a new built-in function object that when called performs the action described by") ~ id <~ "." ~ rest ^^ {
      case x ~ y => parseInst(s"""{
        let $x = (new BuiltinFunctionObject("SubMap" -> (new SubMap())))
        $x.Code = $y
      }""") // TODO handle internalSlotsList
    } | "if the host requires use of an exotic object" ~ rest ^^^ {
      parseInst("let global = undefined")
    } | "if the host requires that the" ~ rest ^^^ {
      parseInst("let thisValue = undefined")
    } | ("if" ~> name <~ "is an element of") ~ name <~ ", remove that element from the" ~ name ^^ {
      case x ~ l =>
        val idx = getTemp
        val len = getTemp
        parseInst(s"""{
          let $idx = 0i
          let $len = $l.length
          while (&& (< $idx $len) (! (= $l[$idx] $x))) $idx = (+ $idx 1i)
          if (< $idx $len) (pop $l $idx) else {}
        }""")
    } | "for each field of" ~ rest ^^^ {
      parseInst(s"""O.SubMap[P].Value = Desc.Value""") // TODO: move each field of record at ValidateAndApplyPropertyDescriptor
    } | "parse" ~ id ~ "using script as the goal symbol and analyse the parse result for any early Error conditions" ~ rest ^^^ {
      parseInst(s"""let body = script""")
    } | "if declaration is declaration : hoistabledeclaration, then" ~> stmt ^^ {
      case s => IIf(EIsInstanceOf(parseExpr("Declaration"), "HoistableDeclaration"), ISeq(List(parseInst("let HoistableDeclaration = Declaration"), s)), ISeq(Nil))
    } | "if statement is statement : labelledstatement , return toplevelvardeclarednames of statement ." ^^^ {
      parseInst(s"""if (is-instance-of Statement LabelledStatement) return Statement.TopLevelVarDeclaredNames else {}""")
    } | ("let" ~> name <~ "be a new list of") ~ expr ~ ("with" ~> expr <~ "appended") ^^ {
      case x ~ (i0 ~ l) ~ (i1 ~ v) => ISeq(i0 ++ i1 :+ parseInst(s"""{
        let $x = (copy-obj ${beautify(l)})
        append ${beautify(v)} -> $x
      }"""))
    } | (("suspend" ~ name ~ "and remove it from the execution context stack") | ("pop" ~ name ~ "from the execution context stack" <~ rest)) ^^^ {
      val idx = getTemp
      parseInst(s"""{
        $context = null
        $idx = (- $executionStack.length 1i)
        (pop $executionStack $idx)
      }""")
    } | "suspend the currently running execution context" ^^^ {
      parseInst(s"""$context = null""")
    } | "suspend" ~> name ^^ {
      case x => parseInst(s"""{
        $context = null
        $x = null
      }""")
    } | "remove" ~ id ~ "from the execution context stack and restore" ~ id ~ "as the running execution context" ^^^ {
      val idx = getTemp
      parseInst(s"""{
        $idx = (- $executionStack.length 1i)
        (pop $executionStack $idx)
        $context = $executionStack[(- $idx 1i)]
      }""")
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
        forEachList(Id(x), parseExpr(executionStack), parseInst(s"""{
          if (! (= $x.ScriptOrModule null)) return $x.ScriptOrModule else {}
        }"""), true),
        parseInst("return null")
      ))
    } | "otherwise , let " ~ id ~ "," ~ id ~ ", and" ~ id ~ "be integers such that" ~ id ~ "≥ 1" ~ rest ^^^ {
      parseInst(s"""return (convert m num2str)""")
    } | "for each property of the global object" ~ rest ^^^ {
      val temp = getTemp
      forEachMap(Id("name"), parseExpr("GLOBAL"), parseInst(s"""{
        let desc = GLOBAL[name]
        let $temp = (DefinePropertyOrThrow global name desc)
        if (= (typeof $temp) "Completion") {
          if (= $temp.Type CONST_normal) $temp = $temp.Value
          else return $temp
        } else {}
      }"""))
    }

  // ignore statements
  lazy val ignoreStmt = (
    "set fields of" |
    "create any implementation-defined" |
    "no further validation is required" | // TODO : should implement goto?? see ValidateAndApplyPropertyDescriptor
    "if" ~ id ~ "is a List of errors," |
    "perform any necessary implementation - defined initialization of" |
    "Set the remainder of" ~ id ~ "'s essential internal methods to the default ordinary object definitions specified in 9.1"
  ) ~ rest ^^^ emptyInst

  // statement to comment additional info
  lazy val commentStmt = (
    "assert:" |
    "note:" |
    "this may be" |
    "as defined" |
    (opt("(") <~ "see")
  ) ~ rest ^^^ emptyInst

  ////////////////////////////////////////////////////////////////////////////////
  // Expressions
  ////////////////////////////////////////////////////////////////////////////////

  lazy val expr: Parser[List[Inst] ~ Expr] = (
    etcExpr |
    completionExpr |
    listExpr |
    newExpr |
    valueExpr ^^ { pair(Nil, _) } |
    arithExpr ^^ { pair(Nil, _) } |
    curExpr ^^ { pair(Nil, _) } |
    algoExpr ^^ { pair(Nil, _) } |
    returnIfAbruptExpr |
    callExpr |
    typeExpr ^^ { pair(Nil, _) } |
    refExpr
  ) ~ subExpr ^^ {
      case (i1 ~ e1) ~ (i2 ~ f) => pair(i1 ++ i2, f(e1))
    }

  lazy val subExpr: Parser[List[Inst] ~ (Expr => Expr)] =
    "+" ~> expr ^^ {
      case i ~ r => pair(i, (l: Expr) => EBOp(OPlus, l, r))
    } | success(pair(Nil, x => x))

  // ReturnIfAbrupt
  lazy val returnIfAbruptExpr: Parser[List[Inst] ~ Expr] =
    ("?" ~> expr | "ReturnIfAbrupt(" ~> expr <~ ")") ^^ {
      case i ~ e => returnIfAbrupt(i, e, true)
    } | "!" ~> expr ^^ {
      case i ~ e => returnIfAbrupt(i, e, false)
    }

  // value expressions
  lazy val valueExpr: Parser[Expr] = ("2 32 - 1" ^^^ { ENum(4294967295.0) }) |
    "the numeric value zero" ^^^ { ENum(0.0) } |
    opt("the value" | "the string") ~> value ^^ {
      case "null" => ENull
      case "true" => EBool(true)
      case "false" => EBool(false)
      case "NaN" => ENum(Double.NaN)
      case "+0" => ENum(0.0)
      case "-0" => ENum(-0.0)
      case "+∞" => ENum(Double.PositiveInfinity)
      case "-∞" => ENum(Double.NegativeInfinity)
      case "undefined" => EUndef
      case s if s.startsWith("\"") && s.endsWith("\"") => EStr(s.slice(1, s.length - 1))
      case err if err.endsWith("Error") => parseExpr(s"""(new OrdinaryObject(
      "Prototype" -> INTRINSIC_${err}Prototype,
      "ErrorData" -> undefined,
      "SubMap" -> (new SubMap())
    ))""")

      case s => ENotYetImpl(s)
    } | const ^^ {
      case "[empty]" => parseExpr("CONST_emptySyntax")
      case const => parseExpr("CONST_" + const.replaceAll("-", ""))
    } | (number <~ ".") ~ number ^^ {
      case x ~ y => ENum(s"$x.$y".toDouble)
    } | number ^^ { case s => EINum(java.lang.Long.decode(s)) }

  // arithmetic expressions
  lazy val arithExpr: Parser[Expr] =
    arithElem ~ bop ~ arithElem ^^ {
      case l ~ f ~ r => EBOp(f, l, r)
    }
  lazy val arithElem: Parser[Expr] =
    valueExpr | id ^^ { parseExpr(_) }
  lazy val bop: Parser[BOp] =
    "+" ^^^ OPlus |
      "-" ^^^ OSub

  // completion expressions
  lazy val completionExpr = "normalcompletion(" ~> expr <~ ")" ^^ {
    case i ~ e => pair(i, EMap(Ty("Completion"), List(
      EStr("Type") -> parseExpr("CONST_normal"),
      EStr("Value") -> e,
      EStr("Target") -> parseExpr("CONST_empty")
    )))
  }

  // call expressions
  lazy val callExpr = (
    "type(" ~> expr <~ ")" ^^ {
      case i ~ e => pair(i, ETypeOf(e))
    } | "completion(" ~> expr <~ ")" ^^ {
      case i ~ e => pair(i, e)
    } | name ~ ("for" ~> name <~ "with") ~ expr ~ ("and" ~> expr <~ "as arguments") ^^ {
      case f ~ x ~ (i0 ~ a1) ~ (i1 ~ a2) =>
        pair(i0 ++ i1, EApp(parseExpr(s"$x.$f"), List(a1, a2)))
    } | (opt("the result of performing" | "the result of") ~> name <~ ("for" | "of")) ~ (name | id) ~ ("with argument" ~> expr) ^^ {
      case f ~ x ~ (i ~ a) => pair(i, EApp(parseExpr(s"$x.$f"), List(a)))
    } | (opt("the result of performing" | "the result of") ~> name <~ ("for" | "of")) ~ (name | id) ~ (("with arguments" | "using" | "with" | "passing") ~> expr <~ "and") ~ (expr <~ "as the optional") ~ (expr <~ "argument") ^^ {
      case f ~ x ~ (i0 ~ a1) ~ (i1 ~ a2) ~ (i2 ~ a3) => pair(i0 ++ i1 ++ i2, EApp(parseExpr(s"$x.$f"), List(a1, a2, a3)))
    } | (opt("the result of performing" | "the result of") ~> name <~ ("for" | "of")) ~ (name | id) ~ (("with arguments" | "using" | "with" | "passing") ~> expr <~ "and") ~ (expr <~ opt("as" ~ opt("the") ~ "arguments")) ^^ {
      case f ~ x ~ (i0 ~ a1) ~ (i1 ~ a2) => pair(i0 ++ i1, EApp(parseExpr(s"$x.$f"), List(a1, a2)))
    } | ref ~ ("(" ~> repsep(expr, ",") <~ ")") ^^ {
      case (i0 ~ RefId(Id(x))) ~ list =>
        val i = (i0 /: list) { case (is, i ~ _) => is ++ i }
        pair(i, EApp(parseExpr(x), list.map { case i ~ e => e }))
      case (i0 ~ (r @ RefProp(b, _))) ~ list =>
        val i = (i0 /: list) { case (is, i ~ _) => is ++ i }
        pair(i, EApp(ERef(r), ERef(b) :: list.map { case i ~ e => e }))
    }
  )

  // new expressions
  lazy val newExpr: Parser[List[Inst] ~ Expr] =
    "a new empty list" ^^^ {
      pair(Nil, EList(Nil))
    } | "a" ~> opt("new") ~> " list containing" ~> expr ^^ {
      case i ~ e => pair(i, EList(List(e)))
    } | ("a new" ~> ty <~ "containing") ~ (expr <~ "as the binding object") ^^ {
      case t ~ (i ~ e) => pair(i, EMap(t, List(
        EStr("SubMap") -> EMap(Ty("SubMap"), Nil),
        EStr("BindingObject") -> e
      )))
    } | ("a new" | "a newly created") ~> ty <~ opt(("with" | "that" | "containing") ~ rest) ^^ {
      case t => pair(Nil, EMap(t, List(EStr("SubMap") -> EMap(Ty("SubMap"), Nil)))) // TODO handle after "with" or "that"
    } | ("a value of type reference whose base value component is" ~> expr) ~
      (", whose referenced name component is" ~> expr) ~
      (", and whose strict reference flag is" ~> expr) ^^ {
        case (i0 ~ b) ~ (i1 ~ r) ~ (i2 ~ s) => pair(i0 ++ i1 ++ i2, EMap(Ty("Reference"), List(
          EStr("BaseValue") -> b,
          EStr("ReferencedName") -> r,
          EStr("StrictReference") -> s
        )))
      } | opt("the") ~> ty ~ ("{" ~> repsep((name <~ ":") ~ expr, ",") <~ "}") ^^ {
        case t ~ list =>
          val i = (List[Inst]() /: list) { case (is, _ ~ (i ~ e)) => is ++ i }
          pair(i, EMap(t, list.map { case x ~ (_ ~ e) => (EStr(x), e) }))
      }

  // list expressions
  lazy val listExpr: Parser[List[Inst] ~ Expr] =
    "«" ~> repsep(expr, ",") <~ "»" ^^ {
      case list =>
        val i = (List[Inst]() /: list) { case (is, (i ~ _)) => is ++ i }
        pair(i, EList(list.map { case _ ~ e => e }))
    } | "a List whose elements are the arguments passed to this function" ^^^ {
      pair(Nil, parseExpr("argumentsList"))
    } | "a List whose sole item is" ~> expr ^^ {
      case i ~ e => pair(i, EList(List(e)))
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
    opt("hint") ~> ("Number" | "Undefined" | "Null" | "String" | "Boolean" | "Symbol" | "Reference" | "Object") ^^ {
      case tname => EStr(tname.head)
    } |
      ty ^^ {
        case Ty(name) => EStr(name)
      }

  lazy val binAddExpr =
    (expr <~ "+") ~ expr ^^ {
      case (i1 ~ e1) ~ (i2 ~ e2) => pair(i1 ++ i2, EBOp(OPlus, e1, e2))
    }
  // et cetera expressions
  lazy val etcExpr: Parser[List[Inst] ~ Expr] =
    rest.filter(list => list.dropRight(1).lastOption == Some("RegularExpressionLiteral")) ^^^ {
      pair(Nil, ENotSupported("RegularExpressionLiteral"))
    } | "the parenthesizedexpression that is covered by coverparenthesizedexpressionandarrowparameterlist" ^^^ {
      pair(Nil, EParseSyntax(ERef(RefId(Id("this"))), "ParenthesizedExpression"))
    } | ("the" ~> name <~ "that is covered by") ~ expr ^^ {
      case r ~ (i ~ e) => pair(i, EParseSyntax(e, r))
    } | ("the larger of" ~> expr <~ "and") ~ expr ^^ {
      case (i0 ~ x) ~ (i1 ~ y) => pair(i0 ++ i1, ENotYetImpl(s"larger of $x and $y"))
    } | "the result of adding the value 1 to" ~> name <~ rest ^^ {
      case x => pair(Nil, parseExpr(s"(+ $x 1)"))
    } | ("the result of" ~> expr <~ "passing") ~ expr ~ ("and" ~> expr <~ "as the arguments") ^^ {
      case (i0 ~ f) ~ (i1 ~ x) ~ (i2 ~ y) =>
        pair(i0 ++ i1 ++ i2, EApp(f, List(x, y)))
    } | ("the string - concatenation of" ~> opt("the previous value of") ~> expr <~ "and") ~ expr ^^ {
      case (i0 ~ e1) ~ (i1 ~ e2) =>
        pair(i0 ++ i1, EBOp(OPlus, e1, e2))
    } | "-" ~> expr ^^ {
      case i ~ e => pair(i, EUOp(ONeg, e))
    } | "the number of elements" ~ ("in" | "of") ~> expr ^^ {
      case i ~ e => pair(i, parseExpr(s"${beautify(e)}.length"))
    } | "the string - concatenation of" ~> repsep(expr, "," ~ opt("and")) ^^ {
      case es =>
        val init: List[Inst] ~ Expr = pair(Nil, EStr(""))
        val insts ~ e = (init /: es) { case (i0 ~ l, i1 ~ r) => pair(i0 ++ i1, EBOp(OPlus, l, r)) }
        (pair(insts, e): List[Inst] ~ Expr)
    } | ((
      "the algorithm steps specified in" ~> secno ~> "for the" ~> name <~ "function" ^^ {
        case x => ERef(RefId(Id(x)))
      } | "the algorithm steps defined in ListIterator" ~ rest ^^^ {
        parseExpr("ListIteratornext")
      } | "CoveredCallExpression of CoverCallExpressionAndAsyncArrowHead" ^^^ {
        parseExpr("(parse-syntax CoverCallExpressionAndAsyncArrowHead CallMemberExpression)")
      } | "the completion record that is the result of evaluating" ~> name <~ "in an implementation - defined manner that conforms to the specification of" ~ name ~ "." ~ name ~ "is the" ~ rest ^^ {
        case f => parseExpr(s"($f.Code thisArgument argumentsList undefined)")
      } | "the completion record that is the result of evaluating" ~> name <~ "in an implementation - defined manner that conforms to the specification of" ~ name ~ ". the" ~ rest ^^ {
        case f => parseExpr(s"($f.Code undefined argumentsList newTarget)")
      } | "the steps of an" ~> name <~ "function as specified below" ^^ {
        case x => parseExpr(s"$x")
      } | (("the number whose value is MV of" ~> name) | ("the result of forming the value of the" ~> name)) <~ rest ^^ {
        case x => EParseString(ERef(RefId(Id(x))), PNum)
      } | "a copy of" ~ opt("the List") ~> name ^^ {
        case x => ECopy(ERef(RefId(Id(x))))
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
      } | "the definition specified in 9.2.1" ^^^ {
        parseExpr("ECMAScriptFunctionObjectDOTCall")
      } | "the definition specified in 9.2.2" ^^^ {
        parseExpr("ECMAScriptFunctionObjectDOTConstruct")
      } | "the token" ~> value ^^ {
        case x => EStr(x)
      } | "the empty string" ^^ {
        case x => EStr("")
      } | ("the stringvalue of stringliteral" | "the string value whose code units are the sv of the stringliteral") ^^^ {
        parseExpr(s"(parse-string StringLiteral string)")
      } | opt("the") ~ value.filter(x => x == "this") ~ "value" ^^^ {
        parseExpr("this")
      }
    ) ^^ { case e => pair(Nil, e) })

  // reference expressions
  lazy val refExpr: Parser[List[Inst] ~ Expr] = ref ^^ {
    case i ~ r => pair(i, ERef(r))
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Conditions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val cond: Parser[List[Inst] ~ Expr] = (
    (("the code matched by this" ~> word <~ "is strict mode code") |
      "the function code for" ~ opt("the") ~ name ~ "is strict mode code" |
      "the code matching the syntactic production that is being evaluated is contained in strict mode code") ^^^ {
        pair(Nil, EBool(false)) // TODO : support strict mode code
      } | (name <~ "and") ~ (name <~ "are both") ~ (value <~ "or both") ~ value ^^ {
        case x ~ y ~ v ~ u => pair(Nil, parseExpr(s"(|| (&& (= $x $v) (= $y $v)) (&& (= $x $u) (= $y $u)))"))
      } | name <~ "is a data property" ^^ {
        case x => pair(Nil, parseExpr(s"(IsDataDescriptor $x)"))
      } | name <~ "is an accessor property" ^^ {
        case x => pair(Nil, parseExpr(s"(IsAccessorDescriptor $x)"))
      } | (ref <~ "is" ~ ("not present" | "absent")) ~ subCond ^^ {
        case (i0 ~ r) ~ (i1 ~ f) => pair(i0 ++ i1, f(EUOp(ONot, exists(r))))
      } | expr <~ "is an abrupt completion" ^^ {
        case i ~ x => pair(i, parseExpr(s"""(&& (= (typeof ${beautify(x)}) "Completion") (! (= ${beautify(x)}.Type CONST_normal)))"""))
      } | (expr <~ "<") ~ expr ~ subCond ^^ {
        case (i0 ~ l) ~ (i1 ~ r) ~ (i2 ~ f) => pair(i0 ++ i1 ++ i2, f(EBOp(OLt, l, r)))
      } | (expr <~ "≥") ~ expr ^^ {
        case (i0 ~ l) ~ (i1 ~ r) => pair(i0 ++ i1, EUOp(ONot, EBOp(OLt, l, r)))
      } | (expr <~ ">") ~ expr ^^ {
        case (i0 ~ l) ~ (i1 ~ r) => pair(i0 ++ i1, EBOp(OLt, r, l))
      } | (expr <~ "=") ~ expr ^^ {
        case (i0 ~ l) ~ (i1 ~ r) => pair(i0 ++ i1, EBOp(OEq, r, l))
      } | (expr <~ "≠") ~ expr ^^ {
        case (i0 ~ l) ~ (i1 ~ r) => pair(i0 ++ i1, EUOp(ONot, EBOp(OEq, r, l)))
      } | expr <~ "is not already suspended" ^^ {
        case i ~ e => pair(i, EBOp(OEq, e, ENull))
      } | name <~ "is not empty" ^^ {
        case x => pair(Nil, parseExpr(s"(< 0i $x.length)"))
      } | (expr <~ ">") ~ expr ~ subCond ^^ {
        case (i0 ~ x) ~ (i1 ~ y) ~ (i2 ~ f) => pair(i0 ++ i1 ++ i2, f(EBOp(OLt, y, x)))
      } | (expr <~ "is less than zero") ~ subCond ^^ {
        case (i0 ~ x) ~ (i1 ~ f) => pair(i0 ++ i1, f(EBOp(OLt, x, EINum(0))))
      } | (expr <~ "is different from") ~ expr ~ subCond ^^ {
        case (i0 ~ x) ~ (i1 ~ y) ~ (i2 ~ f) => pair(i0 ++ i1 ++ i2, f(EUOp(ONot, EBOp(OEq, x, y))))
      } | (expr <~ "is not an element of") ~ expr ^^ {
        case (i0 ~ x) ~ (i1 ~ y) => pair(i0 ++ i1, EUOp(ONot, EContains(y, x)))
      } | (expr <~ "is an element of") ~ expr ~ subCond ^^ {
        case (i0 ~ x) ~ (i1 ~ y) ~ (i2 ~ f) => pair(i0 ++ i1 ++ i2, f(EContains(y, x)))
      } | (expr <~ "does not contain") ~ expr ^^ {
        case (i0 ~ x) ~ (i1 ~ y) => pair(i0 ++ i1, EUOp(ONot, EContains(x, y)))
      } | (expr <~ "contains") ~ expr ^^ {
        case (i0 ~ x) ~ (i1 ~ y) => pair(i0 ++ i1, EContains(x, y))
      } | name <~ "does not have a Generator component" ^^ {
        case x => pair(Nil, parseExpr(s"(= $x.Generator absent)"))
      } | "the source code matching" ~ expr ~ "is non-strict code" ^^^ {
        pair(Nil, EBool(true))
      } | (expr <~ "and") ~ expr <~ "have different results" ^^ {
        case (i0 ~ x) ~ (i1 ~ y) => pair(i0 ++ i1, EUOp(ONot, EBOp(OEq, x, y)))
      } | (expr <~ "and") ~ (expr <~ "are the same object value") ~ subCond ^^ {
        case (i0 ~ x) ~ (i1 ~ y) ~ (i2 ~ f) => pair(i0 ++ i1 ++ i2, f(EBOp(OEq, x, y)))
      } | ("the" ~> name <~ "fields of") ~ name ~ ("and" ~> name <~ "are the boolean negation of each other") ^^ {
        case x ~ y ~ z => pair(Nil, parseExpr(s"""(|| (&& (= $y.$x true) (= $z.$x false)) (&& (= $y.$x false) (= $z.$x true)))"""))
      } | (expr <~ "and") ~ (expr <~ ("are the same object value" | "are exactly the same sequence of code units ( same length and same code units at corresponding indices )")) ~ subCond ^^ {
        case (i0 ~ x) ~ (i1 ~ y) ~ (i2 ~ f) => pair(i0 ++ i1 ++ i2, f(EBOp(OEq, x, y)))
      } | expr ~ "is not the ordinary object internal method defined in" ~ secno ^^^ {
        pair(Nil, EBool(false)) // TODO fix
      } | ("the binding for" ~> name <~ "in") ~ (name <~ "is an uninitialized binding") ^^ {
        case x ~ y => pair(Nil, parseExpr(s"(= $y.SubMap[$x].initialized false)"))
      } | (ref <~ "does not have an own property with key") ~ expr ^^ {
        case (i0 ~ r) ~ (i1 ~ p) => pair(i0 ++ i1, EUOp(ONot, exists(RefProp(RefProp(r, EStr("SubMap")), p))))
      } | (ref <~ "has" <~ ("a" | "an")) ~ word <~ "component" ^^ {
        case (i ~ r) ~ n => pair(i, exists(RefProp(r, EStr(n))))
      } | (ref <~ "has" <~ ("a" | "an")) ~ name <~ "field" ^^ {
        case (i ~ r) ~ n => pair(i, exists(RefProp(r, EStr(n))))
      } | (ref <~ "has a binding for the name that is the value of") ~ expr ^^ {
        case (i0 ~ r) ~ (i1 ~ p) => pair(i0 ++ i1, exists(RefProp(RefProp(r, EStr("SubMap")), p)))
      } | ref ~ ("has a" ~> name <~ "internal" ~ ("method" | "slot")) ^^ {
        case (i ~ r) ~ p => pair(i, parseExpr(s"(! (= absent ${beautify(r)}.$p))"))
      } | (ref <~ "is present and its value is") ~ expr ^^ {
        case (i0 ~ r) ~ (i1 ~ e) => pair(i0 ++ i1, EBOp(OAnd, exists(r), EBOp(OEq, ERef(r), e)))
      } | (ref <~ "is present") ~ subCond ^^ {
        case (i0 ~ r) ~ (i1 ~ f) => pair(i0 ++ i1, f(exists(r)))
      } | (expr <~ "is not") ~ expr ~ subCond ^^ {
        case (i0 ~ l) ~ (i1 ~ r) ~ (i2 ~ f) => pair(i0 ++ i1 ++ i2, f(EUOp(ONot, EBOp(OEq, l, r))))
      } | (name <~ "and") ~ (name <~ "are both the same Symbol value") ^^ {
        case x ~ y => pair(Nil, parseExpr(s"""(&& (&& (= (typeof $x) "Symbol") (= (typeof $y) "Symbol")) (= $x $y))"""))
      } | ("both" ~> ref <~ "and") ~ (ref <~ "are absent") ^^ {
        case (i0 ~ l) ~ (i1 ~ r) => pair(i0 ++ i1, EBOp(OAnd, EUOp(ONot, exists(l)), EUOp(ONot, exists(r))))
      } | expr <~ "has any duplicate entries" ^^ {
        case i ~ e => pair(i, EApp(parseExpr("IsDuplicate"), List(e)))
      } | (opt("both") ~> expr <~ "and") ~ (expr <~ "are" <~ opt("both")) ~ expr ^^ {
        case (i0 ~ l) ~ (i1 ~ r) ~ (i2 ~ e) => pair(i0 ++ i1 ++ i2, EBOp(OAnd, EBOp(OEq, l, e), EBOp(OEq, r, e)))
      } | expr <~ "is neither an objectliteral nor an arrayliteral" ^^ {
        case i ~ e => pair(i, EUOp(ONot, EBOp(OOr, EIsInstanceOf(e, "ObjectLiteral"), EIsInstanceOf(e, "ArrayLiteral"))))
      } | expr <~ "is neither" <~ value <~ "nor the active function" ^^ {
        case i ~ e => pair(i, EUOp(ONot, EBOp(OOr, EBOp(OEq, e, EUndef), EBOp(OEq, e, parseExpr(s"$context.Function")))))
      } | (expr <~ "is neither") ~ (expr <~ "nor") ~ expr ^^ {
        case (i1 ~ e1) ~ (i2 ~ e2) ~ (i3 ~ e3) => pair(i1 ++ i2 ++ i3, EUOp(ONot, EBOp(OOr, EBOp(OEq, e1, e2), EBOp(OEq, e1, e3))))
      } | expr <~ "is" <~ value <~ " , " <~ value <~ "or not supplied" ^^ {
        case i ~ e => pair(i, EBOp(OOr, EBOp(OOr, EBOp(OEq, e, ENull), EBOp(OEq, e, EUndef)), EBOp(OEq, e, EAbsent)))
      } | (expr <~ "is") ~ (valueExpr <~ ",") ~ (valueExpr <~ ",") ~ (valueExpr <~ ",") ~ (valueExpr <~ ",") ~ ("or" ~> valueExpr) ^^ {
        case (i1 ~ e1) ~ e2 ~ e3 ~ e4 ~ e5 ~ e6 =>
          pair(i1, EBOp(OOr, EBOp(OOr, EBOp(OOr, EBOp(OOr, EBOp(OEq, e1, e2), EBOp(OEq, e1, e3)), EBOp(OEq, e1, e4)), EBOp(OEq, e1, e5)), EBOp(OEq, e1, e6)))
      } | expr <~ "is empty" ^^ {
        case i ~ e => pair(i, parseExpr(s"(= ${beautify(e)}.length 0)"))
      } | expr <~ "is neither a variabledeclaration nor a forbinding nor a bindingidentifier" ^^ {
        case i ~ e => pair(i, EUOp(ONot, EBOp(OOr, EBOp(OOr, EIsInstanceOf(e, "VariableDeclaration"), EIsInstanceOf(e, "ForBinding")), EIsInstanceOf(e, "BindingIdentifier"))))
      } | expr <~ "is a variabledeclaration , a forbinding , or a bindingidentifier" ^^ {
        case i ~ e => pair(i, EBOp(OOr, EBOp(OOr, EIsInstanceOf(e, "VariableDeclaration"), EIsInstanceOf(e, "ForBinding")), EIsInstanceOf(e, "BindingIdentifier")))
      } | "statement is statement : labelledstatement" ^^^ {
        pair(Nil, EIsInstanceOf(ERef(RefId(Id("Statement"))), "LabelledStatement"))
      } | expr <~ "is a data property" ^^ {
        case i ~ e => pair(i, EBOp(OEq, ETypeOf(e), EStr("DataProperty")))
      } | expr <~ "is an object" ^^ {
        case i ~ e => pair(i, EBOp(OEq, ETypeOf(e), EStr("Object")))
      } | ("either" ~> cond) ~ ("or" ~> cond) ^^ {
        case (i0 ~ c1) ~ (i1 ~ c2) => pair(i0 ++ i1, EBOp(OOr, c1, c2))
      } | name ~ ("is either" ~> expr) ~ ("or" ~> expr) ^^ {
        case x ~ (i0 ~ e1) ~ (i1 ~ e2) =>
          val e0 = parseExpr(x)
          pair(i0 ++ i1, EBOp(OOr, EBOp(OEq, e0, e1), EBOp(OEq, e0, e2)))
      } | expr <~ "is Boolean, String, Symbol, or Number" ^^ {
        case i ~ e => pair(i, EBOp(OOr, EBOp(OEq, e, EStr("Boolean")), EBOp(OOr, EBOp(OEq, e, EStr("String")), EBOp(OOr, EBOp(OEq, e, EStr("Symbol")), EBOp(OEq, e, EStr("Number")))))) // TODO : remove side effect
      } | "every field in" ~> id <~ "is absent" ^^ {
        case x => pair(Nil, parseExpr(s"""
          (&& (= absent $x.Value)
          (&& (= absent $x.Writable)
          (&& (= absent $x.Get)
          (&& (= absent $x.Set)
          (&& (= absent $x.Enumerable)
          (= absent $x.Configurable))))))"""))
      } | (expr <~ ("is the same as" | "is the same Number value as" | "is")) ~ expr ~ subCond ^^ {
        case (i0 ~ l) ~ (i1 ~ r) ~ (i2 ~ f) => pair(i0 ++ i1 ++ i2, f(EBOp(OEq, l, r)))
      } | (expr <~ "is") ~ expr ~ ("or" ~> expr) ~ subCond ^^ {
        case (i0 ~ e) ~ (i1 ~ l) ~ (i2 ~ r) ~ (i3 ~ f) => pair(i0 ++ i1 ++ i2 ++ i3, f(EBOp(OOr, EBOp(OEq, e, l), EBOp(OEq, e, r))))
      }
  )

  lazy val subCond: Parser[List[Inst] ~ (Expr => Expr)] =
    "or" ~> opt("if") ~> cond ^^ {
      case i ~ r => pair(i, (l: Expr) => EBOp(OOr, l, r))
    } | "and" ~> opt("if") ~> cond ^^ {
      case i ~ r => pair(i, (l: Expr) => EBOp(OAnd, l, r))
    } | guard("," ^^^ pair(Nil, x => x))

  ////////////////////////////////////////////////////////////////////////////////
  // Types
  ////////////////////////////////////////////////////////////////////////////////
  lazy val ty: Parser[Ty] =
    "realm record" ^^^ Ty("RealmRecord") |
      "record" ^^^ Ty("Record") |
      "built-in function object" ^^^ Ty("BuiltinFunctionObject") |
      "arguments exotic object" ^^^ Ty("ArgumentsExoticObject") |
      "propertydescriptor" ^^^ Ty("PropertyDescriptor") |
      "property descriptor" ^^^ Ty("PropertyDescriptor") |
      opt("ecmascript code") ~ "execution context" ^^^ Ty("ExecutionContext") |
      "lexical environment" ^^^ Ty("LexicalEnvironment") |
      "object environment record" ^^^ Ty("ObjectEnvironmentRecord") |
      "object" ^^^ Ty("OrdinaryObject") |
      "declarative environment record" ^^^ Ty("DeclarativeEnvironmentRecord") |
      "function environment record" ^^^ Ty("FunctionEnvironmentRecord") |
      "global environment record" ^^^ Ty("GlobalEnvironmentRecord") |
      "completion" ^^^ Ty("Completion") |
      "script record" ^^^ Ty("ScriptRecord") |
      "array exotic object" ^^^ Ty("ArrayExoticObject")

  ////////////////////////////////////////////////////////////////////////////////
  // References
  ////////////////////////////////////////////////////////////////////////////////
  lazy val ref: Parser[List[Inst] ~ Ref] = (
    "the base value component of" ~> name ^^ {
      case x => parseRef(s"$x.BaseValue")
    } | name <~ "'s base value component" ^^ {
      case x => parseRef(s"$x.BaseValue")
    } | "the strict reference flag of" ~> name ^^ {
      case x => parseRef(s"$x.StrictReference")
    } | ("the value currently bound to" ~> name <~ "in") ~ name ^^ {
      case x ~ y => parseRef(s"$y.SubMap[$x].BoundValue")
    } | "the referenced name component of" ~> name ^^ {
      case x => parseRef(s"$x.ReferencedName")
    } | "the binding object for" ~> name ^^ {
      case x => parseRef(s"$x.BindingObject")
    } | opt("the") ~> "stringvalue of identifiername" ^^^ {
      parseRef(s"IdentifierName")
    } | "the result of evaluating" ~> refWithOrdinal ^^ {
      case x => RefProp(x, EStr("Evaluation"))
    } | ("the result of" ~> name <~ "of") ~ name ^^ {
      case x ~ y => parseRef(s"$y.$x")
    } | "IsFunctionDefinition of" ~> id ^^ {
      case x => parseRef(s"$x.IsFunctionDefinition")
    } | "this" ~ name ^^^ {
      parseRef("this")
    }
  ) ^^ {
      case r => pair(Nil, r)
    } | ("the value of") ~> ref ^^ {
      case i ~ r => pair(i, r)
    } | "the outer lexical environment reference of" ~> ref ^^ {
      case i ~ r => pair(i, RefProp(r, EStr("Outer")))
    } | "the sole element of" ~> ref ^^ {
      case i ~ x => pair(i, RefProp(x, EINum(0)))
    } | "the parsed code that is" ~> ref ^^ {
      case i ~ r => pair(i, r)
    } | "EvaluateBody of" ~> ref ^^ {
      case i ~ r => pair(i, RefProp(r, EStr("EvaluateBody")))
    } | "the EnvironmentRecord component of" ~> ref ^^ {
      case i ~ r => pair(i, RefProp(r, EStr("EnvironmentRecord")))
    } | (name <~ "'s own property whose key is") ~ ref ^^ {
      case r ~ (i ~ p) => pair(i, RefProp(RefProp(RefId(Id(r)), EStr("SubMap")), ERef(p)))
    } | "the second to top element" ~> "of" ~> ref ^^ {
      case i ~ r => pair(i, RefProp(r, EBOp(OSub, ERef(RefProp(r, EStr("length"))), EINum(2))))
    } | (opt("the") ~> name <~ opt("fields") ~ "of") ~ refWithOrdinal ^^ {
      case x ~ y => pair(Nil, RefProp(y, EStr(x)))
    } | (name <~ "'s") ~ name <~ opt("value" | "attribute") ^^ {
      case b ~ x => pair(Nil, RefProp(RefId(Id(b)), EStr(x)))
    } | ("the" ~> id <~ "flag of") ~ ref ^^ {
      case x ~ (i ~ r) if x == "withEnvironment" => pair(i, RefProp(r, EStr(x)))
    } | name ~ rep(field) ^^ {
      case x ~ es =>
        val i = (List[Inst]() /: es) { case (is, i ~ _) => is ++ i }
        pair(i, (es.map { case i ~ e => e }).foldLeft[Ref](RefId(Id(x))) {
          case (r, e) => RefProp(r, e)
        })
    }
  lazy val refWithOrdinal: Parser[Ref] =
    "the first" ~> word ^^ {
      case x => RefId(Id(x + "0"))
    } | "the second" ~> word ^^ {
      case x => RefId(Id(x + "1"))
    } | "this" ~ name ^^^ {
      RefId(Id("this"))
    } | name ^^ {
      case x => RefId(Id(x))
    }

  ////////////////////////////////////////////////////////////////////////////////
  // Fields
  ////////////////////////////////////////////////////////////////////////////////
  lazy val field: Parser[List[Inst] ~ Expr] =
    "." ~> name ^^ {
      case x => pair(Nil, EStr(x))
    } | "[" ~> expr <~ "]" ^^ {
      case i ~ e => pair(i, e)
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
    "the arguments object" ^^^ "args" |
    "[[" ~> word <~ "]]" |
    opt("the intrinsic object") ~> ("%" ~> word <~ "%" | "[[%" ~> word <~ "%]]") ^^ { case x => s"INTRINSIC_$x" } |
    ("@@" ~> word | "[[@@" ~> word <~ "]]") ^^ { case x => s"SYMBOL_$x" } |
    word |
    id
  )

  ////////////////////////////////////////////////////////////////////////////////
  // Helpers
  ////////////////////////////////////////////////////////////////////////////////
  // get temporal identifiers
  private var idCount: Int = 0
  private def getTemp: String = {
    val i = idCount
    idCount += 1
    s"__x${i}__"
  }
  private def getTempId: Id = Id(getTemp)

  // existence check
  private def exists(expr: Expr): Expr = EUOp(ONot, EBOp(OEq, expr, EAbsent))
  private def exists(ref: Ref): Expr = exists(ERef(ref))

  // for-each instrutions for lists
  private def forEachList(id: Id, expr: Expr, body: Inst, reversed: Boolean = false): Inst = {
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
  private def forEachMap(id: Id, expr: Expr, body: Inst, reversed: Boolean = false): Inst = {
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
  private def handleParams(l: List[String]): (List[Id], Option[Id]) = {
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
  private def returnIfAbrupt(
    insts: List[Inst],
    expr: Expr,
    vulnerable: Boolean = true
  ): List[Inst] ~ Expr = (insts, expr) match {
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

  // flatten instructions
  private def flatten(inst: Inst): Inst = inst match {
    case IIf(cond, thenInst, elseInst) =>
      IIf(cond, flatten(thenInst), flatten(elseInst))
    case IWhile(cond, body) =>
      IWhile(cond, flatten(body))
    case ISeq(insts) =>
      def aux(cur: List[Inst], remain: List[Inst]): List[Inst] = remain match {
        case Nil => cur.reverse
        case ISeq(list) :: rest => aux(cur, list ++ rest)
        case inst :: rest => aux(flatten(inst) :: cur, rest)
      }
      aux(Nil, insts) match {
        case List(inst) => inst
        case insts => ISeq(insts)
      }
    case i => i
  }

  // create pair of parsing results
  private val pair = `~`

  // logging
  private val DEBUG_ALGO_NAME = "StatementList1Evaluation0"
  private def logParser[T](parser: Parser[T]): Parser[T] = parser ^^ { log(_) }
  private def message(str: String): Parser[Nothing] =
    logParser(guard(step) ^^ { s"$str: " + _ }) ~> failure("")
  private def log[T](t: T): T = {
    if (algoName == DEBUG_ALGO_NAME) println(t)
    t
  }
}
