package kr.ac.kaist.jiset.algorithm

import kr.ac.kaist.ires.ir.Parser._
import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.error.UnexpectedShift
import kr.ac.kaist.jiset.parser.TokenParsers
import kr.ac.kaist.jiset.util.Useful._
import scala.util.{ Try, Success, Failure }

case class GeneralAlgoCompiler(
    algoName: String,
    algo: Algorithm
) extends GeneralAlgoCompilerHelper

trait GeneralAlgoCompilerHelper extends AlgoCompilers {
  val algoName: String
  val algo: Algorithm
  lazy val result: (Func, Map[Int, List[Token]]) = {
    val (params, varparam) = handleParams(algo.params)
    val func = Func(
      name = algoName,
      params = params,
      varparam = varparam,
      body = normalizeTempIds(flatten(ISeq(parseAll(stmts, algo.toTokenList) match {
        case Success(res, _) => res
        case f @ NoSuccess(_, reader) => error(s"[AlgoCompilerFailed] ${algo.filename}")
      })))
    )
    (func, failed)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Instructions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val stmt: P[Inst] = {
    etcStmt | ignoreStmt | (
      innerStmt |||
      returnStmt |||
      returnContStmt |||
      letStmt |||
      ifStmt |||
      callStmt |||
      setStmt |||
      incrementStmt |||
      decrementStmt |||
      throwStmt |||
      whileStmt |||
      forEachStmt |||
      appendStmt |||
      insertStmt |||
      removeStmt |||
      suspendStmt |||
      pushStmt |||
      assertStmt |||
      starStmt
    )
  } <~ opt("." | ";") ~ opt(comment) | comment
  lazy val comment: P[Inst] =
    "assert:" ~> cond <~ guard("." ~ next) ^^ { case i ~ e => ISeq(i :+ IAssert(e)) } |
      (
        "assert:" |
        "note" |
        "this may be" |
        "as defined" |
        "( if" |
        "this call will always return" ~ value |
        (opt("(") <~ ("see" | "it may be"))
      ) ~ rest ^^^ emptyInst

  // ignore statements
  lazy val ignoreStmt: P[Inst] = (
    "set fields of" |
    "need to defer setting the" |
    "create any implementation-defined" |
    "no further validation is required" |
    "if" ~ id ~ "is a List of errors," |
    "order the elements of" ~ id ~ "so they are in the same relative order as would" |
    "Perform any implementation or host environment defined processing of" |
    "Perform any implementation or host environment defined job initialization using" |
    "Once a generator enters"
  ) ~ rest ^^^ emptyInst

  // inner statements
  lazy val innerStmt: P[Inst] = in ~> stmts <~ out ^^ { ISeq(_) }

  // return statements
  lazy val returnStmt: P[Inst] = "Return" ~> opt(expr) ^^ {
    case None => getRet(getNormalCompletion(EUndef))
    case Some(ie) => algo.kind match {
      case StaticSemantics => getRet(ie)
      case Method if algoName == "OrdinaryGetOwnProperty" => getRet(ie)
      case _ => getRet(getWrapCompletion(ie))
    }
  }

  // return continuation statements
  lazy val returnContStmt: P[Inst] = "ReturnCont" ~> opt(expr ~ opt("to" ~> expr)) ^^ {
    case None => getInst(getCall(retcont, List(getNormalCompletion(EUndef))))
    case Some(ie ~ None) => getInst(getCall(retcont, List(ie)))
    case Some((i ~ f) ~ Some(ie)) => ISeq(i :+ getInst(getCall(EPop(f, EINum(0)), List(ie))))
  }

  // let binding statements
  lazy val letStmt = ("Let" ~> id <~ "be") ~ expr ~ opt(("; if" ~> cond <~ ", use") ~ expr) ^^ {
    case x ~ (i ~ e) ~ None => ISeq(i :+ ILet(Id(x), e))
    case x ~ (i1 ~ e1) ~ Some((i2 ~ e2) ~ (i3 ~ e3)) =>
      ISeq(i2 :+ IIf(e2, ISeq(i3 :+ ILet(Id(x), e3)), ISeq(i1 :+ ILet(Id(x), e1))))
  }

  // if-then-else statements
  lazy val ifStmt = {
    ("if" ~> cond <~ "," ~
      opt("then")) ~ stmt ~ opt(opt("." | ";" | ",") ~ opt(next) ~
        ("else" | "otherwise") ~ opt(ignoreCond | cond) ~ opt(",") ~> stmt)
  } ^^ {
    case (i ~ c) ~ t ~ None => ISeq(i :+ IIf(c, t, emptyInst))
    case (i ~ c) ~ t ~ Some(e) => ISeq(i :+ IIf(c, t, e))
  } ||| ("if" ~> (nt | id) <~ "is" ~ opt("the production")) ~ grammar ~ ("," ~ opt("then") ~> stmt) ^^ {
    case x ~ Grammar(y, ss) ~ s =>
      val pre = ss.map(s => IAccess(Id(s), toERef(x), EStr(s)))
      IIf(EIsInstanceOf(toERef(x), y), ISeq(pre :+ s), emptyInst)
  }
  lazy val ignoreCond: P[I[Expr]] = (
    "the order of evaluation needs to be reversed to preserve left to right evaluation" |
    name ~ "is added as a single item rather than spread" |
    name ~ "contains a formal parameter mapping for" ~ name |
    name ~ "is a Reference to an Environment Record binding" |
    "the base of" ~ ref ~ "is an Environment Record" |
    name ~ "must be" ~ rep(not(",") ~ text) |
    id ~ "does not currently have a property" ~ id |
    ("isaccessordescriptor(" ~> id <~ ") and isaccessordescriptor(") ~ (id <~ ") are both") ~ expr
  ) ^^^ pair(Nil, EBool(true))

  // call statements
  lazy val callStmt: P[Inst] = (("perform" | "call") ~> expr ||| returnIfAbruptExpr) ^^ {
    case i ~ e => ISeq(i :+ IExpr(e))
  }

  // set statements
  lazy val setStmt = "set" ~> setRef ~ {
    "to" ~> expr ||| (
      "as" ~ ("described" | "specified") ~ "in" |||
      "to the definition specified in"
    ) ~> section ^^ { case s => pair(Nil, toERef(s)) }
  } ^^ { case (i0 ~ r) ~ (i1 ~ e) => ISeq(i0 ++ i1 :+ IAssign(r, e)) }
  lazy val setRef: P[I[Ref]] =
    ref ||| opt("the") ~> (camelWord <~ "of") ~ refBase ^^ { case f ~ b => pair(Nil, toRef(b, f)) }

  // increment statements
  lazy val incrementStmt = (("increment" | "increase") ~> ref <~ "by") ~ expr ^^ {
    case (i0 ~ x) ~ (i1 ~ y) => ISeq(i0 ++ i1 :+ IAssign(x, EBOp(OPlus, ERef(x), y)))
  }

  // decrement statements
  lazy val decrementStmt = (("decrement" | "decrease") ~> ref <~ "by") ~ expr ^^ {
    case (i0 ~ x) ~ (i1 ~ y) => ISeq(i0 ++ i1 :+ IAssign(x, EBOp(OSub, ERef(x), y)))
  }

  // throw statements
  lazy val throwStmt = "throw a" ~> expr <~ "exception" ^^ {
    case ie => getRet(getCall("ThrowCompletion", List(ie)))
  }

  // while statements
  lazy val whileStmt = "repeat" ~ opt(",") ~> opt("while" ~> cond <~ opt(",")) ~ stmt ^^ {
    case Some(i ~ c) ~ s => ISeq(i :+ IWhile(c, s))
    case None ~ s => IWhile(EBool(true), s)
  }

  // for-each statements
  lazy val forEachStmt = {
    (opt("repeat" ~ opt(",")) ~> "for each" ~ rep(nt | text) ~> id) ~
      (("in order from" | "in" | "of" | "from" | "that is an element of") ~> expr) ~
      (opt(mention) ~ opt(",") ~> (
        opt("in list order," | "in original insertion order,") ^^^ false |||
        "in reverse list order," ^^^ true
      )) ~ (opt("do") ~> stmt)
  } ^^ {
    case x ~ (i ~ e) ~ isRev ~ b => ISeq(i :+ forEachList(Id(x), e, b, isRev))
  }

  // append statements
  lazy val appendStmt: P[Inst] = ("append" | "add") ~> (
    (expr <~ {
      "to" ~ opt("the end of") |||
        "as" ~ ("an" | "the last") ~ "element of" ~ opt("the list" ~ opt("that is"))
    }) ~ expr ^^ {
      case (i0 ~ x) ~ (i1 ~ y) => ISeq(i0 ++ i1 :+ IAppend(x, y))
    } ||| (("the elements of" | "each item in") ~> expr <~ "to the end of") ~ expr ^^ {
      case (i0 ~ l1) ~ (i1 ~ l2) =>
        val tempId = getTempId
        ISeq(i0 ++ i1 :+ forEachList(tempId, l1, IAppend(toERef(tempId), l2)))
    } ||| ("to" ~> expr <~ opt("the elements of")) ~ expr ^^ {
      case (i0 ~ l1) ~ (i1 ~ l2) =>
        val tempId = getTempId
        ISeq(i0 ++ i1 :+ forEachList(tempId, l2, IAppend(toERef(tempId), l1)))
    }
  )

  // insert statements
  lazy val insertStmt: P[Inst] = ("insert" ~> expr <~ "as the first element of") ~ expr ^^ {
    case (i0 ~ x) ~ (i1 ~ y) => ISeq(i0 ++ i1 :+ IPrepend(x, y))
  }

  // remove statements
  lazy val removeStmt: P[Inst] = (
    ("remove the first element from" ~> id <~ "and let") ~
    (id <~ "be the value of" ~ ("that" | "the") ~ "element")
  ) ^^ { case l ~ x => ILet(Id(x), EPop(toERef(l), EINum(0))) }

  // suspend statements
  lazy val suspendStmt: P[Inst] = (
    "suspend" ~> id ~ opt("and remove it from the execution context stack") ^^ {
      case x ~ opt => suspend(x, !opt.isEmpty)
    } ||| "suspend the currently running execution context" ^^^ {
      suspend(context)
    }
  )

  // push statements
  lazy val pushStmt: P[Inst] = (
    "push" ~> expr <~ ("onto" | "on to") ~ "the execution context stack;" ~
    id ~ "is now the running execution context" ^^ {
      case i ~ e => ISeq(i ++ List(IAppend(e, toERef(executionStack)), parseInst(s"""
        $context = $executionStack[(- $executionStack.length 1i)]
      """)))
    }
  )

  // assert statements
  lazy val assertStmt: P[Inst] = ("assert:" | "note:") ~> cond <~ guard("." ~ next) ^^ { case i ~ e => ISeq(i :+ IAssert(e)) } |
    ("assert:" | "note:") ~> rest ^^^ emptyInst

  ////////////////////////////////////////////////////////////////////////////////
  // Expressions
  ////////////////////////////////////////////////////////////////////////////////

  lazy val expr: P[I[Expr]] = (
    arithExpr ||| term
  )

  lazy val term: P[I[Expr]] = (
    "(" ~> expr <~ ")" |||
    etcExpr |||
    valueExpr |||
    returnIfAbruptExpr |||
    callExpr |||
    newExpr |||
    listExpr |||
    listCopyExpr |||
    algorithmExpr |||
    accessExpr |||
    containsExpr |||
    coveredByExpr |||
    strConcatExpr |||
    syntaxExpr |||
    argumentExpr |||
    refExpr |||
    referenceExpr |||
    starExpr
  )

  // arithmetic expressions
  lazy val arithExpr: P[I[Expr]] = (
    expr ~ rep1(bop ~ term) ^^ {
      case ie ~ ps => ps.foldLeft(ie) {
        case (i0 ~ l, b ~ (i1 ~ r)) => pair(i0 ++ i1, EBOp(b, l, r))
      }
    } ||| uop ~ expr ^^ {
      case u ~ (i ~ e) => pair(i, EUOp(u, e))
    }
  )
  lazy val bop: P[BOp] = (
    "×" ^^^ OMul |
    "/" ^^^ ODiv |
    "+" ^^^ OPlus |
    ("-" | "minus") ^^^ OSub |
    "modulo" ^^^ OUMod |
    "&" ^^^ OBAnd |
    "^" ^^^ OBXOr |
    "|" ^^^ OBOr
  )
  lazy val uop: P[UOp] = "-" ^^^ ONeg

  // value expressions
  lazy val valueExpr: P[I[Expr]] = valueParser ^^ { pair(Nil, _) }

  // ReturnIfAbrupt
  lazy val returnIfAbruptExpr: P[I[Expr]] = opt("the result of" ~ opt("performing")) ~> (
    ("?" ~> expr | "ReturnIfAbrupt(" ~> expr <~ ")") ^^ {
      case i ~ e => returnIfAbrupt(i, e, true)
    } | "!" ~> expr ^^ {
      case i ~ e => returnIfAbrupt(i, e, false)
    } | ("IfAbruptRejectPromise(" ~> expr <~ ", ") ~ (ref <~ ")") ^^ {
      case (i0 ~ e) ~ (i1 ~ r) => ifAbruptRejectPromise(i0 ++ i1, e, r)
    }
  )

  // call expressions
  lazy val callExpr: P[I[Expr]] = (
    callRef ~ ("(" ~> repsep(expr, ",") <~ ")") ^^ {
      case (r: RefId) ~ list => getCall(ERef(r), list)
      case (r @ RefProp(b, _)) ~ list => getCall(ERef(r), pair(Nil, ERef(b)) :: list)
    } ||| {
      "the result of" ~ (rep(not("comparison") ~ word) ~ "comparison") ~>
        expr ~ compOp ~ expr ~ opt("with" ~ id ~ "equal to" ~> expr)
    } ^^ {
      case l ~ f ~ r ~ opt => getCall(toERef(f), List(l, r) ++ opt.toList)
    }
  )
  lazy val callRef: P[Ref] = (
    word |||
    "forin / ofheadevaluation" ^^^ { "ForInOfHeadEvaluation" } |||
    "forin / ofbodyevaluation" ^^^ { "ForInOfBodyEvaluation" }
  ) ^^ { toRef(_) } ||| id ~ callField ^^ { case x ~ y => toRef(x, y) }
  lazy val callField: P[Expr] = "." ~> (internalName | camelWord ^^ { EStr(_) })
  lazy val compOp: P[String] = (
    "==" ^^^ "AbstractEqualityComparison" |||
    "===" ^^^ "StrictEqualityComparison" |||
    "<" ^^^ "AbstractRelationalComparison"
  )

  // new expressions
  lazy val newExpr: P[I[Expr]] = (
    ("a new" | "a newly created") ~> ty ~ opt(("with" | "that" | "containing") ~> extraFields) ^^ {
      case t ~ fs =>
        pair(Nil, EMap(t, (EStr("SubMap") -> EMap(Ty("SubMap"), Nil)) :: fs.getOrElse(Nil)))
    } ||| "a newly created" ~> valueValue <~ "object" ^^ {
      case e => pair(Nil, e)
    } ||| opt("the" | "a" ~ opt("new")) ~> ty ~ ("{" ~> repsep((internalName <~ ":") ~ expr, ",") <~ "}") ^^ {
      case t ~ list =>
        val i = list.map { case _ ~ (i ~ _) => i }.flatten
        pair(i, EMap(t, list.map { case x ~ (_ ~ e) => (x, e) }))
    }
  )
  lazy val extraFields: P[List[(Expr, Expr)]] = (
    "a" ~> internalName <~ "internal slot" ^^ { case x => List(x -> EUndef) } |||
    "internal slots" ~> rep1sep(internalName, sep("and")) ^^ { case xs => xs.map(_ -> EUndef) } |||
    id <~ "as the binding object" ^^ { case x => List(EStr("BindingObject") -> toERef(x)) } |||
    ("the internal slots listed in table" ~ number | opt("initially has") ~> "no fields" | "no bindings") ^^^ Nil
  )

  // list expressions
  lazy val listExpr: P[I[Expr]] = "a new empty list" ^^^ pair(Nil, EList(Nil)) ||| {
    // multiple expressions
    "«" ~> repsep(expr, ",") <~ "»" |||
      ("a" ~ opt("new") ~ "list" ~ opt("containing")) ~> (
        // one element
        opt("whose sole item is" | "only" | ("the" ~ ("one" | "single") ~ "element" ~ opt("," | "which is"))) ~> expr ^^ { List(_) } |||
        // two elements
        (("the elements, in order, of" ~> expr <~ "followed by") ~ expr |
          (expr <~ "followed by the elements , in order , of") ~ expr) ^^ { case x ~ y => List(x, y) }
      )
  } ^^ { getList(_) }

  // list copy expressions
  lazy val listCopyExpr: P[I[Expr]] = (
    "a new List which is a copy of" ~> expr ^^ { getCopyList(_, Nil) } |||
    "a copy of" ~ opt("the list") ~> expr ^^ { getCopyList(_, Nil) } |||
    ("a copy of" | "a new list of") ~> expr ~ ("with" ~> expr <~ "appended") ^^ { case x ~ y => getCopyList(x, List(y)) } |||
    "a copy of" ~ opt("the List") ~> (expr <~ "with all the elements of") ~ (expr <~ "appended") ^^ { case x ~ y => getCopyList(x, y) } |||
    ("a new list containing the same values as the list" ~> expr <~ "in the same order followed by the same values as the list") ~ (expr <~ "in the same order") ^^ { case x ~ y => getCopyList(x, y) }
  )

  // algorithm expressions
  lazy val algorithmExpr: P[I[Expr]] = (
    "an empty sequence of algorithm steps" ^^^ EMap(Ty("algorithm"), List(
      EStr("name") -> EStr(""),
      EStr("length") -> EINum(0),
      EStr("step") -> toERef("EmptyFunction")
    )) |||
    "the algorithm steps" ~ ("specified" | "defined") ~ "in" ~> algorithmName ^^ {
      case (name, len, stepS) => EMap(Ty("algorithm"), List(
        EStr("name") -> EStr(name),
        EStr("length") -> EINum(len),
        EStr("step") -> toERef(stepS)
      ))
    }
  ) ^^ { pair(Nil, _) }
  lazy val algorithmName: P[(String, Int, String)] = (
    secno ~ "for the" ~> intrinsicName <~ "function" ^^ {
      ("", 0, _)
    } |||
    "ListIterator" ~ code.filter(_ == "next") ~ "(" ~ secno ~ ")" ^^^ ("next", 0, "ListIteratornext") |||
    "GetCapabilitiesExecutor Functions" ^^^ ("", 2, "GLOBALDOTGetCapabilitiesExecutorFunctions") |||
    "Promise Resolve Functions (" <~ secno <~ ")" ^^^ ("", 1, "GLOBALDOTPromiseResolveFunctions") |||
    "Promise Reject Functions (" <~ secno <~ ")" ^^^ ("", 1, "GLOBALDOTPromiseRejectFunctions") |||
    "Await Fulfilled Functions" ^^^ ("", 1, "GLOBALDOTAwaitFulfilledFunctions") |||
    "Await Rejected Functions" ^^^ ("", 1, "GLOBALDOTAwaitRejectedFunctions") |||
    "Async-from-Sync Iterator Value Unwrap Functions" ^^^ ("", 1, "GLOBALDOTAsyncfromSyncIteratorValueUnwrapFunctions") |||
    "AsyncGeneratorResumeNext Return Processor Fulfilled Functions" ^^^ ("", 1, "GLOBALDOTAsyncGeneratorResumeNextReturnProcessorFulfilledFunctions") |||
    "AsyncGeneratorResumeNext Return Processor Rejected Functions" ^^^ ("", 1, "GLOBALDOTAsyncGeneratorResumeNextReturnProcessorRejectedFunctions")

  )

  // access expressions
  lazy val accessExpr: P[I[Expr]] = accessRef ^^ { case i ~ r => pair(i, ERef(r)) }
  lazy val accessRef: P[I[Ref]] = (
    (opt("the result of" ~ opt("performing")) ~>
      ("evaluating" ^^^ "Evaluation" | opt("the") ~> (camelWord | nt) <~ ("for" | "of")) ~ expr ~
      opt(("using" | "with" | "passing") ~ opt("arguments" | "argument") ~>
        repsep(expr <~ opt("as the optional" ~ id ~ "argument"), ", and" | "," | "and") <~
        opt("as" ~ opt("the") ~ ("arguments" | "argument")))) ^^ {
        case f ~ ix ~ optList => getAccess(f, ix, optList)
      }
  )

  // contains expressions
  lazy val containsExpr: P[I[Expr]] =
    opt("the result of") ~> ((id | nt) <~ camelWord.filter(_ == "Contains")) ~ (nt ^^ { EStr(_) } | id ^^ { toERef(_) }) ^^ {
      case x ~ y =>
        // `Contains` static semantics
        val a = getTempId
        val b = getTempId
        pair(List(
          IAccess(a, toERef(x), EStr("Contains")),
          IApp(b, toERef(a), List(y))
        ), toERef(b))
    }

  // covered-by expressions
  lazy val coveredByExpr: P[I[Expr]] = ("the" ~> nt <~ "that is covered by") ~ ref ^^ {
    case x ~ (i ~ r) => pair(i, EParseSyntax(ERef(r), EStr(x), Nil))
  }

  // string-concatenation expressions
  lazy val strConcatExpr: P[I[Expr]] =
    "the string-concatenation of" ~> rep1sep(opt("the previous value of") ~> expr, sep("and")) ^^ {
      case es => es.reduce[I[Expr]] {
        case (i0 ~ l, i1 ~ r) => pair(i0 ++ i1, EBOp(OPlus, l, r))
      }
    }

  // syntax expressions
  lazy val syntaxExpr: P[I[Expr]] =
    "the" ~> ("source text" | "code") ~ ("matched by" | "matching") ~> ref ^^ {
      case i ~ r => pair(i, EGetSyntax(ERef(r)))
    }

  // argument expressions
  lazy val argumentExpr: P[I[Expr]] = (
    "a List whose elements are the arguments passed to this function" |||
    "the List of arguments passed to this function" |||
    "a List containing the arguments passed to this function" |||
    "a List consisting of all of the arguments passed to this function" |||
    "a List whose elements are , in left to right order , the arguments that were passed to this function invocation" |||
    "the" ~ id ~ "that was passed to this function by [ [ Call ] ] or [ [ Construct ] ]"
  ) ^^^ { pair(Nil, toERef("argumentsList")) } ||| (
      "the number of arguments passed to this function call"
    ) ^^^ { pair(Nil, toERef("argumentsList", "length")) }

  // reference expressions
  lazy val refExpr: P[I[Expr]] = ref ^^ {
    case i ~ r => pair(i, ERef(r))
  }

  // ECMAScript reference expressions
  lazy val referenceExpr: P[I[Expr]] = (
    ("a value of type reference whose base value component is" ~> expr) ~
    (", whose referenced name component is" ~> expr) ~
    (", and whose strict reference flag is" ~> expr) ^^ {
      case (i0 ~ b) ~ (i1 ~ r) ~ (i2 ~ s) => pair(i0 ++ i1 ++ i2, EMap(Ty("Reference"), List(
        EStr("BaseValue") -> b,
        EStr("ReferencedName") -> r,
        EStr("StrictReference") -> s
      )))
    } | ("a value of type Reference that is a Super Reference whose base value component is" ~> expr) ~
    (", whose referenced name component is" ~> expr) ~
    (", whose thisValue component is" ~> expr) ~
    (", and whose strict reference flag is" ~> expr) ^^ {
      case (i0 ~ b) ~ (i1 ~ r) ~ (i2 ~ t) ~ (i3 ~ s) => pair(i0 ++ i1 ++ i2 ++ i3, EMap(Ty("Reference"), List(
        EStr("BaseValue") -> b,
        EStr("ReferencedName") -> r,
        EStr("thisValue") -> t,
        EStr("StrictReference") -> s
      )))
    }
  )

  ////////////////////////////////////////////////////////////////////////////////
  // values
  ////////////////////////////////////////////////////////////////////////////////
  lazy val valueParser: P[Expr] = (
    valueValue |||
    expValue |||
    codeValue |||
    constValue |||
    numberValue |||
    grammarValue |||
    absentValue |||
    hexValue |||
    activeFunctionValue |||
    stringValue |||
    internalName
  )

  // values with tag `value`
  lazy val valueValue: P[Expr] = opt("the value") ~> value ^^ {
    case "undefined" => EUndef
    case "NaN" => ENum(Double.NaN)
    case "null" => ENull
    case "+0" => EINum(0L)
    case "-0" => ENum(-0.0)
    case "true" => EBool(true)
    case "false" => EBool(false)
    case "+∞" => ENum(Double.PositiveInfinity)
    case "-∞" => ENum(Double.NegativeInfinity)
    case s if s.startsWith("\"") && s.endsWith("\"") => EStr(s.slice(1, s.length - 1))
    case s if Try(s.toLong).isSuccess => EINum(s.toLong)
    case s if Try(s.toDouble).isSuccess => ENum(s.toDouble)
    case err if err.endsWith("Error") => getErrorObj(err)
    case s => ENotSupported(s)
  }

  // exponential values
  lazy val expValue: P[Expr] = (
    number ~ sup.filter(ts => parseAll(number, ts).successful) ^^ {
      case x ~ List(y: Text) =>
        val a = x.toInt
        val b = y.getContent.toInt
        EINum(math.pow(a, b).longValue)
    }
  )

  // values with tag `code`
  lazy val codeValue: P[Expr] = opt("the" ~ opt("single - element") ~ "string") ~> code <~ opt("(" ~ rep(normal.filter(_ != Text(")"))) ~ ")") ^^ {
    case s if s.startsWith("\"") && s.endsWith("\"") => EStr(s.slice(1, s.length - 1))
    case s @ ("super" | "this") => EStr(s)
    case s => ENotSupported(s)
  }

  // values with tag `const`
  lazy val constValue: P[Expr] = const ^^ {
    case "[empty]" => EAbsent
    case const => toERef("CONST_" + const.replaceAll("-", ""))
  }

  // number values
  lazy val numberValue: P[Expr] = opt("the numeric value") ~> (
    (number <~ ".") ~ number ^^ {
      case x ~ y => ENum(s"$x.$y".toDouble)
    } ||| number ^^ {
      case s => EINum(java.lang.Long.decode(s))
    } ||| "zero" ^^^ {
      EINum(0L)
    }
  ) ||| "the Number value for" ~> id ^^ { toERef(_) }

  // grammar values
  lazy val grammarValue: P[Expr] = "the grammar symbol" ~> nt ^^ {
    case x => EStr(x)
  }

  // absent values
  lazy val absentValue: P[Expr] =
    ("absent" | "not supplied" | "not present") ^^^ EAbsent

  // hex values
  lazy val hexValue: P[Expr] =
    opt("the") ~> opt("code unit") ~> hex <~ "(" ~ rep(normal.filter(_ != Text(")"))) ~ ")" ^^ {
      case x => EStr(Character.toChars(x).mkString)
    } ||| hex ^^ { EINum(_) }
  lazy val hex: P[Int] = text.filter(_ startsWith "0x") ^^ {
    case s => Integer.parseInt(s.substring(2), 16)
  }

  // active function object
  lazy val activeFunctionValue: P[Expr] =
    "the active function" ~ opt("object") ^^^ toERef(context, "Function")

  // string values
  lazy val stringValue: P[Expr] =
    "the empty String" ^^^ EStr("")

  ////////////////////////////////////////////////////////////////////////////////
  // Conditions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val cond: P[I[Expr]] = _cond <~ guard("," | in | ("." ~ next)) | etcCond
  lazy val _cond: P[I[Expr]] = (
    bopCond |||
    condOpCond |||
    rhsCond |||
    bothCond |||
    strictModeCond |||
    containsCond |||
    suppliedCond |||
    emptyCond
  )

  // binary operator conditions
  lazy val bopCond: P[I[Expr]] = expr ~ condBOp ~ expr ^^ {
    case (i0 ~ x) ~ ((b, n, r)) ~ (i1 ~ y) => pair(i0 ++ i1, calc(n, r, b, x, y))
  }
  lazy val condBOp: P[(BOp, Boolean, Boolean)] = (
    ("=" | "equals") ^^^ (OEq, false, false) |||
    ("≠" | "is not equal to" | "is different from") ^^^ (OEq, true, false) |||
    ("<" | "is less than") ^^^ (OLt, false, false) |||
    "≥" ^^^ (OLt, true, false) |||
    (">" | "is greater than") ^^^ (OLt, false, true) |||
    "≤" ^^^ (OLt, true, true)
  )

  // conditional operators
  lazy val condOpCond: P[I[Expr]] = _cond ~ (condOp <~ opt("if")) ~ _cond ^^ {
    case (i ~ l) ~ ((op, _)) ~ (i1 ~ r) if isEmptyInsts(i1) => pair(i, EBOp(op, l, r))
    case (i0 ~ l) ~ ((op, f)) ~ (i1 ~ r) =>
      val temp = getTempId
      val (t, e) = f(ISeq(i1 :+ IAssign(toRef(temp), r)))
      pair(i0 ++ List(ILet(temp, l), IIf(toERef(temp), t, e)), toERef(temp))
  }
  lazy val condOp: P[(BOp, Inst => (Inst, Inst))] = opt(",") ~> (
    "or" ^^^ { (OOr, (x: Inst) => (emptyInst, x)) } |||
    "and" ^^^ { (OAnd, (x: Inst) => (x, emptyInst)) }
  )

  // right-hand side conditions
  lazy val rhsCond: P[I[Expr]] = expr ~ rep1sep(rhs, sep("or")) ^^ {
    case (i ~ r) ~ fs => concat(i, fs.map(_(r)).reduce[I[Expr]] {
      case ((i0 ~ x), (i1 ~ y)) => pair(i0 ++ i1, EBOp(OOr, x, y))
    })
  }
  lazy val rhs: P[Expr => I[Expr]] = equalRhs ||| notEqualRhs
  lazy val equalRhs: P[Expr => I[Expr]] = {
    "is" ~ opt("present and" ~ ("its value is" | "has value")) | "has the value"
  } ~ opt("either") ~> rep1sep(rhsExpr <~ guard("," | "or" | "and" | in | ("." ~ next)), sep("or")) ^^ {
    case fs => (l: Expr) => fs.map(_(l)).reduce[I[Expr]] {
      case ((i0 ~ l), (i1 ~ r)) => pair(i0 ++ i1, EBOp(OOr, l, r))
    }
  }
  lazy val notEqualRhs: P[Expr => I[Expr]] = {
    "is" ~ ("not" ~ opt("the same as" | "one of") | "neither")
  } ~> rep1sep(rhsExpr <~ guard("," | "or" | "and" | "nor" | in | ("." ~ next)), sep("nor" | "or")) ^^ {
    case fs => (l: Expr) => fs.map(_(l)).reduce[I[Expr]] {
      case ((i0 ~ l), (i1 ~ r)) => pair(i0 ++ i1, EBOp(OOr, l, r))
    } match { case i ~ e => pair(i, not(e)) }
  }
  lazy val rhsExpr: P[Expr => I[Expr]] = (
    ("supplied" | "present") <~ opt("as a parameter") ^^^ { (e: Expr) => pair(Nil, isNEq(e, EAbsent)) } |||
    valueParser ^^ { case x => (e: Expr) => pair(Nil, isEq(e, x)) } |||
    "the token" ~> code ^^ { case y => (e: Expr) => pair(Nil, isEq(EGetSyntax(e), EStr(y))) } |||
    (id | camelWord) ^^ { case x => (e: Expr) => pair(Nil, isEq(e, toERef(x))) } |||
    callName ^^ { case f => (e: Expr) => getCall(f, List(pair(Nil, e))) } |||
    ("a completion") ^^ { case _ => (e: Expr) => pair(Nil, EIsCompletion(e)) } |||
    ("a" | "an") ~> ty.filter(_ != Ty("Completion")) ^^ { case t => (e: Expr) => pair(Nil, isEq(ETypeOf(e), EStr(t.name))) } |||
    opt("a" | "an") ~> nt ^^ { case x => (e: Expr) => pair(Nil, EIsInstanceOf(e, x)) }
  )
  lazy val callName: P[String] = ("a" | "an") ~> (
    "array index" ^^^ "IsArrayIndex" |||
    "accessor property" ^^^ "IsAccessorDescriptor" |||
    "data property" ^^^ "IsDataDescriptor" |||
    "abrupt completion" ^^^ "IsAbruptCompletion"
  )

  // both conditions
  val bothCond: P[I[Expr]] =
    (opt("both") ~> expr <~ "and") ~ (expr <~ "are" ~ opt("both")) ~
      rep1sep(bothRhsExpr, sep("or" ~ opt("both"))) ^^ {
        case (i0 ~ x) ~ (i1 ~ y) ~ fs => concat(i0 ++ i1, fs.map(_(x, y)).reduce[I[Expr]] {
          case ((i0 ~ l), (i1 ~ r)) => pair(i0 ++ i1, EBOp(OOr, l, r))
        })
      }
  lazy val bothRhsExpr: P[(Expr, Expr) => I[Expr]] = (
    rhsExpr ^^ {
      case f => (l: Expr, r: Expr) => (f(l), f(r)) match {
        case (i0 ~ l, i1 ~ r) => pair(i0 ++ i1, EBOp(OAnd, l, r))
      }
    } ||| "the same" ~> camelWord <~ "value" ^^^ {
      (l: Expr, r: Expr) => pair(Nil, isEq(l, r))
    }
  )

  // strict mode conditions
  val strictModeCond: P[I[Expr]] = (
    "the Directive Prologue of FunctionStatementList contains a Use Strict Directive" |||
    opt("the source code matching") ~ expr ~ "is strict mode code" |||
    "the directive prologue of statementList contains a use strict directive" |||
    "the code matching the syntactic production that is being evaluated is contained in strict mode code" |||
    "the code matched by the syntactic production that is being evaluated is strict mode code" |||
    refBase ~> "is contained in strict mode code" |||
    "the function code for" ~> ("this" ~> opt(nt | "this" | ty)) <~ "is strict mode code"
  ) ^^^ pair(Nil, EBool(true)) |||
    opt("the source code matching") ~ expr ~ "is non-strict code" ^^^ pair(Nil, EBool(false))

  // contains conditions
  val containsCond: P[I[Expr]] = (
    (ref <~ "does not have" ~ ("a" | "an")) ~ containsField <~ opt(containsPost) ^^ {
      case (i ~ r) ~ f => pair(i, isEq(ERef(RefProp(r, f)), EAbsent))
    } | (ref <~ ("has" | "have") <~ ("a" | "an")) ~ containsField <~ opt(containsPost) ^^ {
      case (i ~ r) ~ f => pair(i, isNEq(ERef(RefProp(r, f)), EAbsent))
    } ||| (expr <~ "does not contain") ~ expr ^^ {
      case (i0 ~ x) ~ (i1 ~ y) => pair(i0 ++ i1, not(EContains(x, y)))
    } ||| (expr <~ "contains") ~ expr ^^ {
      case (i0 ~ x) ~ (i1 ~ y) => pair(i0 ++ i1, EContains(x, y))
    } ||| (expr <~ "is not" ~ ("in" | "an element of")) ~ expr ^^ {
      case (i0 ~ x) ~ (i1 ~ y) => pair(i0 ++ i1, not(EContains(y, x)))
    } ||| (expr <~ "is" ~ ("in" | "an element of")) ~ expr ^^ {
      case (i0 ~ x) ~ (i1 ~ y) => pair(i0 ++ i1, EContains(y, x))
    } ||| containsExpr
  )
  lazy val containsField: P[Expr] = id ^^ toERef | fieldName
  lazy val containsPost: P[String] =
    ("field" | "internal" ~ ("slot" | "method") | "component") ^^^ ""

  // supplied conditions
  lazy val suppliedCond: P[I[Expr]] = id <~ "was supplied" ^^ {
    case x => pair(Nil, isNEq(toERef(x), EAbsent))
  }

  // empty conditions
  lazy val emptyCond: P[I[Expr]] = (
    ref <~ ("has no elements" | "is empty" | "is an empty list") ^^ {
      case i ~ r => pair(i, isEq(ERef(RefProp(r, EStr("length"))), EINum(0)))
    } ||| ref <~ ("is not empty" | "has any elements" | "is not an empty list") ^^ {
      case i ~ r => pair(i, EBOp(OLt, EINum(0), ERef(RefProp(r, EStr("length")))))
    }
  )

  ////////////////////////////////////////////////////////////////////////////////
  // Types
  ////////////////////////////////////////////////////////////////////////////////
  lazy val ty: P[Ty] = (
    "AsyncGeneratorRequest" ^^^ "AsyncGeneratorRequest" |||
    "Cyclic Module Record" ^^^ "CyclicModuleRecord" |||
    "ExportEntry Record" ^^^ "ExportEntryRecord" |||
    "ImportEntry Record" ^^^ "ImportEntryRecord" |||
    "PromiseCapability" ^^^ "PromiseCapability" |||
    "ResolvedBinding Record" ^^^ "ResolvedBindingRecord" |||
    "PromiseReaction" ^^^ "PromiseReaction" |||
    "ReadSharedMemory" ^^^ "ReadSharedMemory" |||
    "Shared Data Block" ^^^ "SharedDataBlock" |||
    "Source Text Module Record" ^^^ "SourceTextModuleRecord" |||
    "WriteSharedMemory" ^^^ "WriteSharedMemory" |||
    "arguments exotic object" ^^^ "ArgumentsExoticObject" |||
    "array exotic object" ^^^ "ArrayExoticObject" |||
    "bound function exotic object" ^^^ "BoundFunctionExoticObject" |||
    "built-in function object" ^^^ "BuiltinFunctionObject" |||
    "chosen value record" ^^^ "ChosenValueRecord" |||
    "completion" ^^^ "Completion" |||
    "declarative environment record" ^^^ "DeclarativeEnvironmentRecord" |||
    "function environment record" ^^^ "FunctionEnvironmentRecord" |||
    "global environment record" ^^^ "GlobalEnvironmentRecord" |||
    "lexical environment" ^^^ "LexicalEnvironment" |||
    "module environment record" ^^^ "ModuleEnvironmentRecord" |||
    "object environment record" ^^^ "ObjectEnvironmentRecord" |||
    "object" ^^^ "OrdinaryObject" |||
    "pendingjob" ^^^ "PendingJob" |||
    "property descriptor" ^^^ "PropertyDescriptor" |||
    "propertydescriptor" ^^^ "PropertyDescriptor" |||
    "proxy exotic object" ^^^ "ProxyExoticObject" |||
    "realm record" ^^^ "RealmRecord" |||
    "record" ^^^ "Record" |||
    "script record" ^^^ "ScriptRecord" |||
    ("exotic String object" | "string exotic object") ^^^ "StringExoticObject" |||
    opt("ecmascript code") ~ "execution context" ^^^ "ExecutionContext"
  ) ^^ Ty

  ////////////////////////////////////////////////////////////////////////////////
  // References
  ////////////////////////////////////////////////////////////////////////////////
  lazy val ref: P[I[Ref]] = opt(refPre) ~> opt("the") ~> (
    boundValueRef |||
    ordinalRef |||
    ownKeyRef |||
    topElemRef |||
    fieldRef |||
    lengthRef |||
    flagRef
  )

  // references with fields
  lazy val fieldRef: P[I[Ref]] = (
    (fieldName <~ ("of" | "for")) ~ refBase ^^ {
      case f ~ b => pair(Nil, toRef(b, f))
    } ||| (refBase <~ "'s") ~ (fieldName ||| camelWord ^^ { EStr(_) }) <~ opt("value" | "attribute") ^^ {
      case b ~ x => pair(Nil, RefProp(RefId(Id(b)), x))
    } ||| refBase ~ rep(field) ^^ {
      case x ~ es =>
        val i = es.foldLeft(List[Inst]()) { case (is, i ~ _) => is ++ i }
        pair(i, (es.map { case i ~ e => e }).foldLeft[Ref](RefId(Id(x))) {
          case (r, e) => RefProp(r, e)
        })
    }
  )

  // bound value references
  lazy val boundValueRef: P[I[Ref]] = (("bound value for" | "value currently bound to") ~> id <~ "in") ~ id ^^ {
    case x ~ y => pair(Nil, parseRef(s"$y.SubMap[$x].BoundValue"))
  }

  // ordinal references
  lazy val ordinalRef: P[I[Ref]] = (ordinal <~ "element of") ~ (accessRef ||| ref) ^^ {
    case k ~ (i ~ r) => pair(i, RefProp(r, EINum(k)))
  }

  // own key references
  lazy val ownKeyRef: P[I[Ref]] = (refBase <~ "'s own property whose key is") ~ id ^^ {
    case b ~ p => pair(Nil, RefProp(toRef(b, "SubMap"), toERef(p)))
  }

  // top elements references
  lazy val topElemRef: P[I[Ref]] = ordinal ~ ("to top element of" ~> ref) ^^ {
    case k ~ (i ~ r) => pair(i, RefProp(r, EBOp(OSub, ERef(RefProp(r, EStr("length"))), EINum(k + 1))))
  }

  // length references
  lazy val lengthRef: P[I[Ref]] = (
    "length of" ~> ref |||
    "number of" ~ (opt("code unit") ~ "elements" | "code units") ~ ("in" | "of") ~ opt("the List") ~> ref
  ) ^^ { case i ~ r => pair(i, RefProp(r, EStr("length"))) }

  // flag references
  lazy val flagRef: P[I[Ref]] = (id <~ "flag of") ~ ref ^^ {
    case x ~ (i ~ r) if x == "withEnvironment" => pair(i, RefProp(r, EStr(x)))
  }

  lazy val refBase: P[String] = opt("the") ~> (
    "running execution context" ^^^ context |||
    "current Realm Record" ^^^ realm |||
    "arguments object" ^^^ "args" |||
    "execution context stack" ^^^ executionStack |||
    ty ~ "for which the method was invoked" ^^^ "this" |||
    "this" ~ opt(nt | "this" | ty) ^^^ "this" |||
    value.filter(_ == "this") ~ "value" ^^^ "this" |||
    "reference" ~> id |||
    intrinsicName |||
    symbolName |||
    camelWord |||
    opt(ordinal) ~ nt ^^ { case k ~ x => x + k.getOrElse("") } |||
    opt("reference" | nt) ~> id <~ opt("flag")
  )
  lazy val refPre: P[Unit] = opt("the") ~> (
    "hint" |||
    "list that is" ~ opt("the value of") |||
    "string value of" |||
    "value of" |||
    "parsed code that is" |||
    "code unit"
  ) ^^^ ()
  lazy val ordinal: P[Int] = opt("the") ~> (
    ("sole" | "first") ^^^ 0 |
    "second" ^^^ 1 |
    "third" ^^^ 2
  )
  lazy val field: P[I[Expr]] = (
    "." ~> internalName ^^ {
      case x => pair(Nil, x)
    } | "[" ~> expr <~ "]" ^^ {
      case i ~ e => pair(i, e)
    }
  )
  lazy val fieldName: P[Expr] = opt("the") ~> (
    "base value component" ^^^ "BaseValue" |||
    "thisValue component" ^^^ "thisValue" |||
    "outer environment reference" ^^^ "Outer" |||
    "outer lexical environment reference" ^^^ "Outer" |||
    "strict reference flag" ^^^ "StrictReference" |||
    "referenced name component" ^^^ "ReferencedName" |||
    "binding object" ^^^ "BindingObject" |||
    camelWord <~ ("fields" | "component")
  ) ^^ { EStr(_) } ||| {
      opt("the") ~> internalName <~ opt("field" | "internal" ~ ("method" | "slot"))
    }

  ////////////////////////////////////////////////////////////////////////////////
  // Section Numbers
  ////////////////////////////////////////////////////////////////////////////////
  lazy val secno: P[List[Int]] =
    number ~ rep("." ~> number) ^^ {
      case n ~ list => n.toInt :: list.map(_.toInt)
    }

  // method pointed by sections
  lazy val section: P[String] = (
    "9.2.1" ^^^ "ECMAScriptFunctionObjectDOTCall" |
    "9.2.2" ^^^ "ECMAScriptFunctionObjectDOTConstruct" |
    "9.4.1.1" ^^^ "BoundFunctionExoticObject.Call" |
    "9.4.1.2" ^^^ "BoundFunctionExoticObject.Construct" |
    "9.4.2.1" ^^^ "ArrayExoticObject.DefineOwnProperty" |
    "9.4.3.1" ^^^ "StringExoticObject.GetOwnProperty" |
    "9.4.3.2" ^^^ "StringExoticObject.DefineOwnProperty" |
    "9.4.3.3" ^^^ "StringExoticObject.OwnPropertyKeys" |
    "9.4.4.1" ^^^ "ArgumentsExoticObject.GetOwnProperty" |
    "9.4.4.2" ^^^ "ArgumentsExoticObject.DefineOwnProperty" |
    "9.4.4.3" ^^^ "ArgumentsExoticObject.Get" |
    "9.4.4.4" ^^^ "ArgumentsExoticObject.Set" |
    "9.4.4.5" ^^^ "ArgumentsExoticObject.Delete" |
    "9.4.5.1" ^^^ "IntegerIndexedExoticObject.GetOwnProperty" |
    "9.4.5.2" ^^^ "IntegerIndexedExoticObject.HasProperty" |
    "9.4.5.3" ^^^ "IntegerIndexedExoticObject.DefineOwnProperty" |
    "9.4.5.4" ^^^ "IntegerIndexedExoticObject.Get" |
    "9.4.5.5" ^^^ "IntegerIndexedExoticObject.Set" |
    "9.4.5.6" ^^^ "IntegerIndexedExoticObject.OwnPropertyKeys" |
    "9.5.12" ^^^ "ProxyExoticObject.Call" |
    "9.5.13" ^^^ "ProxyExoticObject.Construct"
  ) ^^ { getScalaName(_) }

  ////////////////////////////////////////////////////////////////////////////////
  // Names
  ////////////////////////////////////////////////////////////////////////////////
  lazy val name: P[String] = refBase
  lazy val intrinsicName: P[String] =
    opt("the") ~> opt("intrinsic object") ~> "%" ~> word <~ "%" ^^ { INTRINSIC_PRE + _ }
  lazy val symbolName: P[String] =
    "@@" ~> word ^^ { case x => s"SYMBOL_$x" }
  lazy val internalName: P[Expr] =
    "[[" ~> (intrinsicName ||| symbolName ||| word) <~ "]]" ^^ { EStr(_) }

  ////////////////////////////////////////////////////////////////////////////////
  // Etc parsers
  ////////////////////////////////////////////////////////////////////////////////
  // etc statements
  lazy val etcStmt: P[Inst] = failure("")

  // etc expressions
  lazy val etcExpr: P[I[Expr]] = failure("")

  // etc conditions
  lazy val etcCond: P[I[Expr]] = failure("")
}
