package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.algorithm
import algorithm.{ AlgoKind, Algorithm, Token, StaticSemantics }
import algorithm.{ Method, Grammar, AlgoCompilers, Text }
import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.LINE_SEP
import scala.util.{ Try, Success, Failure }

import kr.ac.kaist.ase.error.UnexpectedShift
import kr.ac.kaist.ase.parser.TokenParsers
import kr.ac.kaist.ase.util.Useful._

case class AlgoCompiler(algoName: String, algo: Algorithm) extends AlgoCompilerHelper {
  val kind = algo.kind
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
}

trait AlgoCompilerHelper extends AlgoCompilers {
  val algoName: String
  val kind: AlgoKind

  // execution context stack string
  val executionStack = "GLOBAL_executionStack"
  val context = "GLOBAL_context"
  val realm = "REALM"
  val jobQueue = "GLOBAL_jobQueue"
  val retcont = "__ret__"

  ////////////////////////////////////////////////////////////////////////////////
  // Instructions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val stmt: P[Inst] = {
    etcStmt | (
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
      starStmt
    )
  } <~ opt(".") ~ opt(comment) | comment

  // inner statements
  lazy val innerStmt: P[Inst] = in ~> stmts <~ out ^^ { ISeq(_) }

  // return statements
  lazy val returnStmt: P[Inst] = "Return" ~> opt(expr) ^^ {
    case None => getRet(getNormalCompletion(EUndef))
    case Some(ie) => kind match {
      case StaticSemantics => getRet(ie)
      case Method if algoName == "OrdinaryGetOwnProperty" => getRet(ie)
      case _ => getRet(getWrapCompletion(ie))
    }
  }

  // return continuation statements
  lazy val returnContStmt: P[Inst] = "ReturnCont" ~> opt(expr ~ opt("to" ~> expr)) ^^ {
    case None => getInst(getCall(retcont, List(getNormalCompletion(EUndef))))
    case Some(ie ~ None) => getInst(getCall(retcont, List(ie)))
    case Some((i ~ f) ~ Some(ie)) => ISeq(i :+ getInst(getCall(f, List(ie))))
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
  }

  // call statements
  lazy val callStmt: P[Inst] = (("perform" | "call") ~> expr ||| returnIfAbruptExpr) ^^ {
    case i ~ e => ISeq(i :+ IExpr(e))
  }

  // set statements
  lazy val setStmt = "set" ~> setRef ~ {
    "to" ~> expr ||| "as" ~ ("described" | "specified") ~ "in" ~> (
      section ^^ { case s => pair(Nil, toERef(s)) }
    )
  } ^^ { case (i0 ~ r) ~ (i1 ~ e) => ISeq(i0 ++ i1 :+ IAssign(r, e)) }
  lazy val setRef: P[I[Ref]] =
    ref ||| opt("the") ~> (camelWord <~ "of") ~ refBase ^^ { case f ~ b => pair(Nil, toRef(b, f)) }

  // increment statements
  lazy val incrementStmt = ("increment" ~> ref <~ "by") ~ expr ^^ {
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
  lazy val whileStmt = "repeat," ~> opt("while" ~> cond <~ opt(",")) ~ stmt ^^ {
    case Some(i ~ c) ~ s => ISeq(i :+ IWhile(c, s))
    case None ~ s => IWhile(EBool(true), s)
  }

  // for-each statements
  lazy val forEachStmt = {
    ("for each" ~ rep(nt | text) ~> id) ~
      (("in order from" | "in" | "of" | "from" | "that is an element of") ~> expr) ~
      (opt(mention) ~ opt(",") ~> (
        opt("in list order," | "in original insertion order,") ^^^ false |||
        "in reverse list order," ^^^ true
      )) ~ ("do" ~> stmt)
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

  // append statements
  lazy val insertStmt: P[Inst] = ("insert" ~> expr <~ "as the first element of") ~ expr ^^ {
    case (i0 ~ x) ~ (i1 ~ y) => ISeq(i0 ++ i1 :+ IPrepend(x, y))
  }

  // remove statements
  lazy val removeStmt: P[Inst] = (
    ("remove the first element from" ~> id <~ "and let") ~
    (id <~ "be the value of" ~ ("that" | "the") ~ "element")
  ) ^^ { case l ~ x => ILet(Id(x), EPop(toERef(l), EINum(0))) }

  ////////////////////////////////////////////////////////////////////////////////
  // Expressions
  ////////////////////////////////////////////////////////////////////////////////

  lazy val expr: P[I[Expr]] = (
    etcExpr |||
    arithExpr |||
    valueExpr |||
    returnIfAbruptExpr |||
    callExpr |||
    newExpr |||
    listExpr |||
    listCopyExpr |||
    algorithmExpr |||
    accessExpr |||
    containsExpr |||
    refExpr |||
    starExpr
  )

  // arithmetic expressions
  lazy val arithExpr: P[I[Expr]] = "(" ~> expr <~ ")" ||| expr ~ bop ~ expr ^^ {
    case (i0 ~ l) ~ b ~ (i1 ~ r) => pair(i0 ++ i1, EBOp(b, l, r))
  }
  lazy val bop: P[BOp] = (
    "×" ^^^ OMul |
    "/" ^^^ ODiv |
    "+" ^^^ OPlus |
    ("-" | "minus") ^^^ OSub |
    "modulo" ^^^ OMod |
    "&" ^^^ OBAnd |
    "^" ^^^ OBXOr |
    "|" ^^^ OBOr
  )

  // value expressions
  lazy val valueExpr: P[I[Expr]] = valueParser ^^ { pair(Nil, _) }

  // ReturnIfAbrupt
  lazy val returnIfAbruptExpr: P[I[Expr]] = opt("the result of" ~ opt("performing")) ~> (
    ("?" ~> expr | "ReturnIfAbrupt(" ~> expr <~ ")") ^^ {
      case i ~ e => returnIfAbrupt(i, e, true)
    } | "!" ~> expr ^^ {
      case i ~ e => returnIfAbrupt(i, e, false)
    } | ("IfAbruptRejectPromise(" ~> expr <~ ", ") ~ (expr <~ ")") ^^ {
      case (i0 ~ e0) ~ (i1 ~ e1) => ifAbruptRejectPromise(i0 ++ i1, e0, e1)
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
    } ||| opt("the") ~> ty ~ ("{" ~> repsep((internalName <~ ":") ~ expr, ",") <~ "}") ^^ {
      case t ~ list =>
        val i = list.map { case _ ~ (i ~ _) => i }.flatten
        pair(i, EMap(t, list.map { case x ~ (_ ~ e) => (x, e) }))
    }
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
    "a copy of" ~ opt("the list") ~> expr ^^ { getCopyList(_, Nil) } |||
    ("a copy of" | "a new list of") ~> expr ~ ("with" ~> expr <~ "appended") ^^ { case x ~ y => getCopyList(x, List(y)) } |||
    "a copy of" ~ opt("the List") ~> (expr <~ "with all the elements of") ~ (expr <~ "appended") ^^ { case x ~ y => getCopyList(x, y) } |||
    ("a new list containing the same values as the list" ~> expr <~ "in the same order followed by the same values as the list") ~ (expr <~ "in the same order") ^^ { case x ~ y => getCopyList(x, y) }
  )

  // algorithm expressions
  lazy val algorithmExpr: P[I[Expr]] = (
    "an empty sequence of algorithm steps" ^^^ EFunc(Nil, None, emptyInst) |||
    "the algorithm steps" ~ ("specified" | "defined") ~ "in" ~> algorithmName ^^ { toERef(_) }
  ) ^^ { pair(Nil, _) }
  lazy val algorithmName: P[String] = (
    secno ~ "for the" ~> intrinsicName <~ "function" |||
    "ListIterator next ()" ^^^ "ListIteratornext" |||
    "GetCapabilitiesExecutor Functions" ^^^ "GLOBALDOTGetCapabilitiesExecutorFunctions" |||
    "Promise Resolve Functions" ^^^ "GLOBALDOTPromiseResolveFunctions" |||
    "Promise Reject Functions" ^^^ "GLOBALDOTPromiseRejectFunctions" |||
    "Await Fulfilled Functions" ^^^ "GLOBALDOTAwaitFulfilledFunctions" |||
    "Await Rejected Functions" ^^^ "GLOBALDOTAwaitRejectedFunctions" |||
    "Async-from-Sync Iterator Value Unwrap Functions" ^^^ "GLOBALDOTAsyncfromSyncIteratorValueUnwrapFunctions"
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
  lazy val containsExpr =
    (id <~ camelWord.filter(_ == "Contains")) ~ (nt ^^ { EStr(_) } | id ^^ { toERef(_) }) ^^ {
      case x ~ y =>
        // TODO `Contains` static semantics
        // val a = getTempId
        // val b = getTempId
        // pair(List(
        //   IAccess(a, toERef(x), EStr("Contains")),
        //   IApp(b, toERef(a), List(y))
        // ), toERef(b))
        if (y == EStr("ScriptBody"))
          pair(Nil, parseExpr("true"))
        else
          pair(Nil, parseExpr("false"))
    }

  // reference expressions
  lazy val refExpr: P[I[Expr]] = ref ^^ {
    case i ~ r => pair(i, ERef(r))
  }

  ////////////////////////////////////////////////////////////////////////////////
  // values
  ////////////////////////////////////////////////////////////////////////////////
  lazy val valueParser: P[Expr] = (
    valueValue |||
    expValue |||
    codeValue |||
    constValue |||
    numberValue |||
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
    case s if Try(s.toLong).isSuccess => EINum(s.toLong)
    case s if Try(s.toDouble).isSuccess => ENum(s.toDouble)
    case err if err.endsWith("Error") => getErrorObj(err)
    case s => ENotYetImpl(s)
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
  lazy val codeValue: P[Expr] = opt("the" ~ opt("single - element") ~ "string") ~> code ^^ {
    case s if s.startsWith("\"") && s.endsWith("\"") => EStr(s.slice(1, s.length - 1))
    case s @ ("super" | "this") => EStr(s)
    case s => ENotYetImpl(s)
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
  )

  ////////////////////////////////////////////////////////////////////////////////
  // Conditions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val cond: P[I[Expr]] = _cond <~ guard(",") ^^ {
    case ie =>
      // print("#")
      ie
  } | etcCond
  lazy val _cond: P[I[Expr]] = (
    expr ~ condBOp ~ expr ^^ {
      case (i0 ~ x) ~ ((b, n, r)) ~ (i1 ~ y) => pair(i0 ++ i1, calc(n, r, b, x, y))
    } ||| (_cond ~ condOp <~ opt("if")) ~ _cond ^^ {
      case (i ~ l) ~ ((op, _)) ~ (Nil ~ r) => pair(i, EBOp(op, l, r))
      case (i0 ~ l) ~ ((op, f)) ~ (i1 ~ r) =>
        val temp = getTempId
        val (t, e) = f(ISeq(i1 :+ IAssign(toRef(temp), r)))
        pair(i0 ++ List(ILet(temp, l), IIf(toERef(temp), t, e)), toERef(temp))
    }
  )
  val condBOp: P[(BOp, Boolean, Boolean)] = (
    "=" ^^^ (OEq, false, false) |||
    ("≠" | "is different from") ^^^ (OEq, true, false) |||
    "<" ^^^ (OLt, false, false) |||
    "≥" ^^^ (OLt, true, false) |||
    ">" ^^^ (OLt, false, true) |||
    "≤" ^^^ (OLt, true, true)
  )
  val rhs: P[Expr] = (
    equalRhs
  )
  val equalRhs: P[Expr] = (
    "is" ~ opt("present and its value is") |||
    "has the value"
  ) ~> (valueParser ||| id ^^ { toERef(_) })
  val condOp: P[(BOp, Inst => (Inst, Inst))] = (
    "or" ^^^ { (OOr, (x: Inst) => (emptyInst, x)) } |||
    "and" ^^^ { (OAnd, (x: Inst) => (x, emptyInst)) }
  )

  // etc conditions
  lazy val etcCond: P[I[Expr]] = (
    (("the code matched by" ~> name <~ "is strict mode code") |
      "the source text matching" ~> name <~ "is strict mode code" |
      "the function code for" ~ opt("the") ~ name ~ "is strict mode code" |
      "the code matching the syntactic production that is being evaluated is contained in strict mode code" |
      "the directive prologue of statementList contains a use strict directive") ^^^ {
        pair(Nil, EBool(false)) // TODO : support strict mode code
      } | "no arguments were passed to this function invocation" ^^^ {
        pair(Nil, parseExpr(s"(= argumentsList.length 0i)"))
      } | name ~ "< 0xD800 or" ~ name ~ "> 0xDBFF or" ~ name ~ "+ 1 =" ~ name ^^^ {
        pair(Nil, EBool(true)) // TODO : based on real code unit
      } | name <~ "is an Identifier and StringValue of" ~ name ~ "is the same value as the StringValue of IdentifierName" ^^ {
        case x => pair(Nil, parseExpr(s"(&& (is-instance-of $x Identifier) (= (get-syntax $x) (get-syntax IdentifierName)))"))
      } | name <~ "is a ReservedWord" ^^ {
        case x => pair(Nil, parseExpr(s"(! (is-instance-of $x Identifier))"))
      } | (name <~ "is the token") ~ code ^^ {
        case x ~ y => pair(Nil, EBOp(OEq, EGetSyntax(toERef(x)), EStr(y)))
      } | (name <~ "does not have" <~ ("a" | "an")) ~ (expr <~ "internal slot") ~ subCond ^^ {
        case x ~ (_ ~ y) ~ f => concat(Nil, f(parseExpr(s"(= $x[${beautify(y)}] absent)")))
      } | name <~ "does not have all of the internal slots of an Array Iterator Instance (22.1.5.3)" ^^ {
        case x => pair(Nil, parseExpr(s"""
          (|| (= absent $x.IteratedObject)
          (|| (= absent $x.ArrayIteratorNextIndex)
          (= absent $x.ArrayIterationKind)))"""))
      } | name <~ "is a FunctionDeclaration, a GeneratorDeclaration, an AsyncFunctionDeclaration, or an AsyncGeneratorDeclaration" ^^ {
        case x => pair(Nil, parseExpr(s"""
          (|| (is-instance-of $x FunctionDeclaration)
          (|| (is-instance-of $x GeneratorDeclaration)
          (|| (is-instance-of $x AsyncFunctionDeclaration)
          (is-instance-of $x AsyncGeneratorDeclaration))))"""))
      } | name <~ "is not one of NewTarget, SuperProperty, SuperCall," ~ code ~ opt(",") ~ "or" ~ code ^^ {
        case x => pair(Nil, parseExpr(s"""(!
          (|| (is-instance-of $x NewTarget)
          (|| (is-instance-of $x SuperProperty)
          (|| (is-instance-of $x SuperCall)
          (|| (= $x "super") (= $x "this"))))))"""))
      } | (name <~ "and") ~ (name <~ "are both") ~ (value <~ "or both") ~ value ^^ {
        case x ~ y ~ v ~ u => pair(Nil, parseExpr(s"(|| (&& (= $x $v) (= $y $v)) (&& (= $x $u) (= $y $u)))"))
      } | name <~ "is a data property" ^^ {
        case x =>
          val temp = getTemp
          pair(List(parseInst(s"app $temp = (IsDataDescriptor $x)")), toERef(temp))
      } | (name <~ "is") ~ (valueParser <~ "and") ~ (name <~ "is") ~ valueParser ^^ {
        case x ~ v ~ y ~ u => pair(Nil, parseExpr(s"(&& (= $x ${beautify(v)}) (= $y ${beautify(u)}))"))
      } | name <~ "is an array index" ^^ {
        case x =>
          val temp = getTemp
          pair(List(parseInst(s"app $temp = (IsArrayIndex $x)")), toERef(temp))
      } | name <~ "is an accessor property" ^^ {
        case x =>
          val temp = getTemp
          pair(List(parseInst(s"app $temp = (IsAccessorDescriptor $x)")), toERef(temp))
      } | name <~ "does not have all of the internal slots of a String Iterator Instance (21.1.5.3)" ^^ {
        case x => pair(Nil, parseExpr(s"""(|| (= $x.IteratedString absent) (= $x.StringIteratorNextIndex absent))"""))
      } | (ref <~ "is" ~ ("not present" | "absent") <~ ", or is either") ~ (valueParser <~ "or") ~ valueParser ~ subCond ^^ {
        case (i0 ~ r) ~ e1 ~ e2 ~ f => concat(i0, f(EBOp(OOr, EBOp(OOr, EUOp(ONot, exists(r)), EBOp(OEq, ERef(r), e1)), EBOp(OEq, ERef(r), e2))))
      } | (ref <~ "is" ~ ("not present" | "absent")) ~ subCond ^^ {
        case (i0 ~ r) ~ f => concat(i0, f(EUOp(ONot, exists(r))))
      } | expr <~ "is not an abrupt completion" ^^ {
        case i ~ x => pair(i, parseExpr(s"""(! (&& (= (typeof ${beautify(x)}) "Completion") (! (= ${beautify(x)}.Type CONST_normal))))"""))
      } | expr <~ "is an abrupt completion" ^^ {
        case i ~ x => pair(i, parseExpr(s"""(&& (= (typeof ${beautify(x)}) "Completion") (! (= ${beautify(x)}.Type CONST_normal)))"""))
      } | expr <~ "is a normal completion" ^^ {
        case i ~ x => pair(i, parseExpr(s"""(&& (= (typeof ${beautify(x)}) "Completion") (= ${beautify(x)}.Type CONST_normal))"""))
      } | (expr <~ "<") ~ expr ~ subCond ^^ {
        case (i0 ~ l) ~ (i1 ~ r) ~ f => concat(i0 ++ i1, f(EBOp(OLt, l, r)))
      } | (expr <~ "≥") ~ expr ~ subCond ^^ {
        case (i0 ~ l) ~ (i1 ~ r) ~ f => concat(i0 ++ i1, f(EUOp(ONot, EBOp(OLt, l, r))))
      } | (expr <~ ">") ~ expr ~ subCond ^^ {
        case (i0 ~ l) ~ (i1 ~ r) ~ f => concat(i0 ++ i1, f(EBOp(OLt, r, l)))
      } | (expr <~ "≤") ~ expr ~ subCond ^^ {
        case (i0 ~ l) ~ (i1 ~ r) ~ f => concat(i0 ++ i1, f(EUOp(ONot, EBOp(OLt, r, l))))
      } | (expr <~ "=") ~ expr ~ subCond ^^ {
        case (i0 ~ l) ~ (i1 ~ r) ~ f => concat(i0 ++ i1, f(EBOp(OEq, r, l)))
      } | (expr <~ "≠") ~ expr ~ subCond ^^ {
        case (i0 ~ l) ~ (i1 ~ r) ~ f => concat(i0 ++ i1, f(EUOp(ONot, EBOp(OEq, r, l))))
      } | expr <~ "is not already suspended" ^^ {
        case i ~ e => pair(i, EBOp(OEq, e, ENull))
      } | name <~ "has no elements" ^^ {
        case x => pair(Nil, parseExpr(s"(= 0i $x.length)"))
      } | name <~ ("is not empty" | "has any elements" | "is not an empty list") ^^ {
        case x => pair(Nil, parseExpr(s"(< 0i $x.length)"))
      } | (expr <~ ">") ~ expr ~ subCond ^^ {
        case (i0 ~ x) ~ (i1 ~ y) ~ f => concat(i0 ++ i1, f(EBOp(OLt, y, x)))
      } | (expr <~ "is less than") ~ expr ~ subCond ^^ {
        case (i0 ~ x) ~ (i1 ~ y) ~ f => concat(i0 ++ i1, f(EBOp(OLt, x, y)))
      } | (expr <~ "is different from") ~ expr ~ subCond ^^ {
        case (i0 ~ x) ~ (i1 ~ y) ~ f => concat(i0 ++ i1, f(EUOp(ONot, EBOp(OEq, x, y))))
      } | (expr <~ "is not" ~ ("in" | "an element of")) ~ expr ^^ {
        case (i0 ~ x) ~ (i1 ~ y) => pair(i0 ++ i1, EUOp(ONot, EContains(y, x)))
      } | (expr <~ "is" ~ ("in" | "an element of")) ~ expr ~ subCond ^^ {
        case (i0 ~ x) ~ (i1 ~ y) ~ f => concat(i0 ++ i1, f(EContains(y, x)))
      } | (ref <~ "is absent or has the value") ~ valueParser ^^ {
        case (i ~ x) ~ v =>
          val l = beautify(x)
          val r = beautify(v)
          pair(i, parseExpr(s"(|| (= $l absent) (= $l $r))"))
      } | (ref <~ "has the value") ~ valueParser ^^ {
        case (i ~ x) ~ v =>
          val l = beautify(x)
          val r = beautify(v)
          pair(i, parseExpr(s"(= $l $r)"))
      } | name <~ "does not have a Generator component" ^^ {
        case x => pair(Nil, parseExpr(s"(= $x.Generator absent)"))
      } | "the source code matching" ~ expr ~ "is strict mode code" ^^^ {
        pair(Nil, EBool(false)) // TODO strict
      } | "the source code matching" ~ expr ~ "is non-strict code" ^^^ {
        pair(Nil, EBool(true)) // TODO strict
      } | (expr <~ "and") ~ expr <~ "have different results" ^^ {
        case (i0 ~ x) ~ (i1 ~ y) => pair(i0 ++ i1, EUOp(ONot, EBOp(OEq, x, y)))
      } | ("the" ~> name <~ "fields of") ~ name ~ ("and" ~> name <~ "are the boolean negation of each other") ^^ {
        case x ~ y ~ z => pair(Nil, parseExpr(s"""(|| (&& (= $y.$x true) (= $z.$x false)) (&& (= $y.$x false) (= $z.$x true)))"""))
      } | name ~ ("and" ~> name <~ "are not the same Realm Record") ^^ {
        case x ~ y => pair(Nil, parseExpr(s"(! (= $x $y))"))
      } | (expr <~ "and") ~ (expr <~ ("are the same" ~ ("object" | "number") ~ "value" | "are exactly the same sequence of code units ( same length and same code units at corresponding indices )")) ~ subCond ^^ {
        case (i0 ~ x) ~ (i1 ~ y) ~ f => concat(i0 ++ i1, f(EBOp(OEq, x, y)))
      } | expr ~ "is not the ordinary object internal method defined in" ~ secno ^^^ {
        pair(Nil, EBool(false)) // TODO fix
      } | ("the binding for" ~> name <~ "in") ~ (name <~ "is an uninitialized binding") ^^ {
        case x ~ y => pair(Nil, parseExpr(s"(= $y.SubMap[$x].initialized false)"))
      } | (ref <~ "does not have an own property with key") ~ expr ^^ {
        case (i0 ~ r) ~ (i1 ~ p) => pair(i0 ++ i1, EUOp(ONot, exists(RefProp(RefProp(r, EStr("SubMap")), p))))
      } | (ref <~ ("has" | "have") <~ ("a" | "an")) ~ word <~ "component" ^^ {
        case (i ~ r) ~ n => pair(i, exists(RefProp(r, EStr(n))))
      } | (ref <~ "has" <~ ("a" | "an")) ~ (name ^^ { case x => EStr(x) } | internalName) <~ "field" ^^ {
        case (i ~ r) ~ n => pair(i, exists(RefProp(r, n)))
      } | (name <~ "does not have a binding for") ~ name ^^ {
        case x ~ y => pair(Nil, parseExpr(s"(= absent $x.SubMap[$y])"))
      } | ("the binding for" ~> name <~ "in") ~ (name <~ "is a strict binding") ^^ {
        case x ~ y => pair(Nil, parseExpr(s"(&& (! (= absent $y.SubMap[$x].strict)) $y.SubMap[$x].strict)"))
      } | ("the binding for" ~> name <~ "in") ~ (name <~ "has not yet been initialized") ^^ {
        case x ~ y => pair(Nil, parseExpr(s"(&& (! (= absent $y.SubMap[$x].initialized)) (! $y.SubMap[$x].initialized))"))
      } | ("the binding for" ~> name <~ "in") ~ (name <~ "is a mutable binding") ^^ {
        case x ~ y => pair(Nil, parseExpr(s"""(= (typeof $y.SubMap[$x]) "MutableBinding")"""))
      } | (ref <~ "has a binding for the name that is the value of") ~ expr ^^ {
        case (i0 ~ r) ~ (i1 ~ p) => pair(i0 ++ i1, exists(RefProp(RefProp(r, EStr("SubMap")), p)))
      } | ref ~ ("has" ~ ("a" | "an") ~> internalName <~ "internal" ~ ("method" | "slot")) ~ subCond ^^ {
        case (i ~ r) ~ p ~ f => concat(i, f(parseExpr(s"(! (= absent ${beautify(r)}[${beautify(p)}]))")))
      } | (ref <~ "is present and its value is") ~ expr ^^ {
        case (i0 ~ r) ~ (i1 ~ e) => pair(i0 ++ i1, EBOp(OAnd, exists(r), EBOp(OEq, ERef(r), e)))
      } | (ref <~ "is present" <~ opt("as a parameter")) ~ subCond ^^ {
        case (i0 ~ r) ~ f => concat(i0, f(exists(r)))
      } | (expr <~ "is not" <~ opt("the same as")) ~ expr ~ subCond ^^ {
        case (i0 ~ l) ~ (i1 ~ r) ~ f => concat(i0 ++ i1, f(EUOp(ONot, EBOp(OEq, l, r))))
      } | (name <~ "and") ~ (name <~ "are both the same Symbol value") ^^ {
        case x ~ y => pair(Nil, parseExpr(s"""(&& (&& (= (typeof $x) "Symbol") (= (typeof $y) "Symbol")) (= $x $y))"""))
      } | ("both" ~> ref <~ "and") ~ (ref <~ "are absent") ^^ {
        case (i0 ~ l) ~ (i1 ~ r) => pair(i0 ++ i1, EBOp(OAnd, EUOp(ONot, exists(l)), EUOp(ONot, exists(r))))
      } | expr <~ "has any duplicate entries" ^^ {
        case i ~ e =>
          val temp = getTempId
          pair(i :+ IApp(temp, parseExpr("IsDuplicate"), List(e)), ERef(RefId(temp)))
      } | (opt("both") ~> expr <~ "and") ~ (expr <~ "are" <~ opt("both")) ~ expr ^^ {
        case (i0 ~ l) ~ (i1 ~ r) ~ (i2 ~ e) => pair(i0 ++ i1 ++ i2, EBOp(OAnd, EBOp(OEq, l, e), EBOp(OEq, r, e)))
      } | expr <~ "is neither an objectliteral nor an arrayliteral" ^^ {
        case i ~ e => pair(i, EUOp(ONot, EBOp(OOr, EIsInstanceOf(e, "ObjectLiteral"), EIsInstanceOf(e, "ArrayLiteral"))))
      } | expr <~ "is" <~ opt("either") <~ "an objectliteral or an arrayliteral" ^^ {
        case i ~ e => pair(i, EBOp(OOr, EIsInstanceOf(e, "ObjectLiteral"), EIsInstanceOf(e, "ArrayLiteral")))
      } | expr <~ "is neither" <~ value <~ "nor the active function" ^^ {
        case i ~ e => pair(i, EUOp(ONot, EBOp(OOr, EBOp(OEq, e, EUndef), EBOp(OEq, e, parseExpr(s"$context.Function")))))
      } | (expr <~ "is neither") ~ (expr <~ "nor") ~ expr ^^ {
        case (i1 ~ e1) ~ (i2 ~ e2) ~ (i3 ~ e3) => pair(i1 ++ i2 ++ i3, EUOp(ONot, EBOp(OOr, EBOp(OEq, e1, e2), EBOp(OEq, e1, e3))))
      } | expr <~ "is" <~ value <~ " , " <~ value <~ "or not supplied" ^^ {
        case i ~ e => pair(i, EBOp(OOr, EBOp(OOr, EBOp(OEq, e, ENull), EBOp(OEq, e, EUndef)), EBOp(OEq, e, EAbsent)))
      } | (expr <~ "is") ~ (valueParser <~ ",") ~ (valueParser <~ ",") ~ (valueParser <~ ",") ~ (valueParser <~ ",") ~ ("or" ~> valueParser) ^^ {
        case (i1 ~ e1) ~ e2 ~ e3 ~ e4 ~ e5 ~ e6 =>
          pair(i1, EBOp(OOr, EBOp(OOr, EBOp(OOr, EBOp(OOr, EBOp(OEq, e1, e2), EBOp(OEq, e1, e3)), EBOp(OEq, e1, e4)), EBOp(OEq, e1, e5)), EBOp(OEq, e1, e6)))
      } | expr <~ ("is empty" | "is an empty list") ^^ {
        case i ~ e => pair(i, parseExpr(s"(= ${beautify(e)}.length 0)"))
      } | expr <~ "is neither a variabledeclaration nor a forbinding nor a bindingidentifier" ^^ {
        case i ~ e => pair(i, EUOp(ONot, EBOp(OOr, EBOp(OOr, EIsInstanceOf(e, "VariableDeclaration"), EIsInstanceOf(e, "ForBinding")), EIsInstanceOf(e, "BindingIdentifier"))))
      } | expr <~ "is a variabledeclaration , a forbinding , or a bindingidentifier" ^^ {
        case i ~ e => pair(i, EBOp(OOr, EBOp(OOr, EIsInstanceOf(e, "VariableDeclaration"), EIsInstanceOf(e, "ForBinding")), EIsInstanceOf(e, "BindingIdentifier")))
      } | "statement is statement10" ^^^ {
        pair(Nil, EIsInstanceOf(toERef("Statement"), "LabelledStatement"))
      } | expr <~ "is a data property" ^^ {
        case i ~ e => pair(i, EBOp(OEq, ETypeOf(e), EStr("DataProperty")))
      } | expr <~ "is an object" ^^ {
        case i ~ e =>
          val temp = getTemp
          pair(i :+ parseInst(s"""app $temp = (Type ${beautify(e)})"""), parseExpr(s"""(= $temp "Object")"""))
      } | (expr <~ "is not" ~ ("a" | "an")) ~ ty ^^ {
        case (i ~ e) ~ t => pair(i, EUOp(ONot, EBOp(OEq, ETypeOf(e), EStr(t.name))))
      } | (expr <~ "is") ~ (("a" | "an") ~> ty) ~ ("or" ~> ("a" | "an") ~> ty) ~ subCond ^^ {
        case (i0 ~ e) ~ t1 ~ t2 ~ f => concat(i0, f(EBOp(OOr, EBOp(OEq, ETypeOf(e), EStr(t1.name)), EBOp(OEq, ETypeOf(e), EStr(t2.name)))))
      } | (expr <~ "is" ~ ("a" | "an")) ~ ty ^^ {
        case (i ~ e) ~ t => pair(i, EBOp(OEq, ETypeOf(e), EStr(t.name)))
      } | ("either" ~> etcCond) ~ ("or" ~> etcCond) ^^ {
        case (i0 ~ c1) ~ (i1 ~ c2) => pair(i0 ++ i1, EBOp(OOr, c1, c2))
      } | name ~ ("is either" ~> expr) ~ ("or" ~> expr) ^^ {
        case x ~ (i0 ~ e1) ~ (i1 ~ e2) =>
          val e0 = parseExpr(x)
          pair(i0 ++ i1, EBOp(OOr, EBOp(OEq, e0, e1), EBOp(OEq, e0, e2)))
      } | expr <~ "is Boolean, String, Symbol, or Number" ^^ {
        case i ~ e => pair(i, EBOp(OOr, EBOp(OEq, e, EStr("Boolean")), EBOp(OOr, EBOp(OEq, e, EStr("String")), EBOp(OOr, EBOp(OEq, e, EStr("Symbol")), EBOp(OEq, e, EStr("Number")))))) // TODO : remove side effect
      } | (expr <~ "equals") ~ expr ^^ {
        case (i0 ~ x) ~ (i1 ~ y) => pair(i0 ++ i1, EBOp(OEq, x, y))
      } | (expr <~ "is") ~ rep1sep(valueParser, ",") ~ (", or" ~> valueParser) ^^ {
        case (i ~ e0) ~ list ~ e1 =>
          pair(i, (list :+ e1).map(e => EBOp(OEq, e0, e)).reduce((l, r) => EBOp(OOr, l, r)))
      } | "every field in" ~> id <~ "is absent" ^^ {
        case x => pair(Nil, parseExpr(s"""
          (&& (= absent $x.Value)
          (&& (= absent $x.Writable)
          (&& (= absent $x.Get)
          (&& (= absent $x.Set)
          (&& (= absent $x.Enumerable)
          (= absent $x.Configurable))))))"""))
      } | "type(" ~> name <~ ") is object and is either a built-in function object or has an [[ECMAScriptCode]] internal slot" ^^ {
        case x =>
          val temp = getTemp
          pair(List(parseInst(s"""app $temp = (Type $x)""")), parseExpr(s"""(&& (= $temp "Object") (|| (= $temp "BuiltinFunctionObject") (! (= $x.ECMAScriptCode absent))))"""))
      } | ("type(" ~> name <~ ") is") ~ nonTrivialTyName ~ subCond ^^ {
        case x ~ t ~ f =>
          val temp = getTemp
          concat(List(parseInst(s"""app $temp = (Type $x)""")), f(parseExpr(s"""(= $temp "$t")""")))
      } | ("type(" ~> name <~ ") is either") ~ rep(nonTrivialTyName <~ ",") ~ ("or" ~> nonTrivialTyName) ~ subCond ^^ {
        case x ~ ts ~ t ~ f =>
          val ty = getTemp
          val newTS = ts :+ t
          val e = parseExpr((ts :+ t).map(t => s"""(= $ty "$t")""").reduce((x, y) => s"(|| $x $y)"))
          concat(List(parseInst(s"app $ty = (Type $x)")), f(e))
      } | (expr <~ ("is the same as" | "is the same Number value as" | "is")) ~ expr ~ subCond ^^ {
        case (i0 ~ l) ~ (i1 ~ r) ~ f => concat(i0 ++ i1, f(EBOp(OEq, l, r)))
      } | (expr <~ "is") ~ expr ~ ("or" ~> expr) ~ subCond ^^ {
        case (i0 ~ e) ~ (i1 ~ l) ~ (i2 ~ r) ~ f => concat(i0 ++ i1 ++ i2, f(EBOp(OOr, EBOp(OEq, e, l), EBOp(OEq, e, r))))
      } | (expr <~ "does not contain") ~ expr ^^ {
        case (i0 ~ x) ~ (i1 ~ y) => pair(i0 ++ i1, EUOp(ONot, EContains(x, y)))
      } | containsExpr | (expr <~ "contains") ~ expr ^^ {
        case (i0 ~ x) ~ (i1 ~ y) => pair(i0 ++ i1, EContains(x, y))
      } | starCond
  )

  val ignoreCond: P[I[Expr]] = (
    "the order of evaluation needs to be reversed to preserve left to right evaluation" |
    name ~ "is added as a single item rather than spread" |
    name ~ "contains a formal parameter mapping for" ~ name |
    name ~ "is a Reference to an Environment Record binding" |
    "the base of" ~ ref ~ "is an Environment Record" |
    name ~ "must be" ~ rep(not(",") ~ text) |
    id ~ "does not currently have a property" ~ id |
    id <~ "is an accessor property" |
    ("isaccessordescriptor(" ~> id <~ ") and isaccessordescriptor(") ~ (id <~ ") are both") ~ expr
  ) ^^^ pair(Nil, EBool(true))

  lazy val nonTrivialTyName: P[String] = ("string" | "boolean" | "number" | "object" | "symbol") ^^ { ts => ts(0) }

  lazy val subCond: P[Expr => I[Expr]] =
    opt(",") ~> "or" ~> opt("if") ~> etcCond ^^ {
      case i ~ r =>
        val temp = getTempId
        (l: Expr) => pair(List(ILet(temp, l), IIf(ERef(RefId(temp)), emptyInst, ISeq(i :+ IAssign(RefId(temp), EBOp(OOr, ERef(RefId(temp)), r))))), ERef(RefId(temp)))
    } | opt(",") ~> "and" ~> opt("if") ~> etcCond ^^ {
      case i ~ r =>
        val temp = getTempId
        (l: Expr) => pair(List(ILet(temp, l), IIf(ERef(RefId(temp)), ISeq(i :+ IAssign(RefId(temp), EBOp(OAnd, ERef(RefId(temp)), r))), emptyInst)), ERef(RefId(temp)))
    } | guard(("," | in) ^^^ ((x: Expr) => pair(List[Inst](), x)))

  ////////////////////////////////////////////////////////////////////////////////
  // Types
  ////////////////////////////////////////////////////////////////////////////////
  lazy val ty: P[Ty] = (
    "AsyncGeneratorRequest" ^^^ Ty("AsyncGeneratorRequest") |||
    "PromiseCapability" ^^^ Ty("PromiseCapability") |||
    "PromiseReaction" ^^^ Ty("PromiseReaction") |||
    "arguments exotic object" ^^^ Ty("ArgumentsExoticObject") |||
    "array exotic object" ^^^ Ty("ArrayExoticObject") |||
    "bound function exotic object" ^^^ Ty("BoundFunctionExoticObject") |||
    "built-in function object" ^^^ Ty("BuiltinFunctionObject") |||
    "completion" ^^^ Ty("Completion") |||
    "declarative environment record" ^^^ Ty("DeclarativeEnvironmentRecord") |||
    "function environment record" ^^^ Ty("FunctionEnvironmentRecord") |||
    "global environment record" ^^^ Ty("GlobalEnvironmentRecord") |||
    "lexical environment" ^^^ Ty("LexicalEnvironment") |||
    "object environment record" ^^^ Ty("ObjectEnvironmentRecord") |||
    "object" ^^^ Ty("OrdinaryObject") |||
    "pendingjob" ^^^ Ty("PendingJob") |||
    "property descriptor" ^^^ Ty("PropertyDescriptor") |||
    "propertydescriptor" ^^^ Ty("PropertyDescriptor") |||
    "proxy exotic object" ^^^ Ty("ProxyExoticObject") |||
    "realm record" ^^^ Ty("RealmRecord") |||
    "record" ^^^ Ty("Record") |||
    "script record" ^^^ Ty("ScriptRecord") |||
    "string exotic object" ^^^ Ty("StringExoticObject") |||
    opt("ecmascript code") ~ "execution context" ^^^ Ty("ExecutionContext")
  )

  ////////////////////////////////////////////////////////////////////////////////
  // References
  ////////////////////////////////////////////////////////////////////////////////
  lazy val ref: P[I[Ref]] = opt(refPre) ~> opt("the") ~> (
    (("bound value for" | "value currently bound to") ~> id <~ "in") ~ id ^^ {
      case x ~ y => pair(Nil, parseRef(s"$y.SubMap[$x].BoundValue"))
    } | (ordinal <~ "element of") ~ (accessRef ||| ref) ^^ {
      case k ~ (i ~ r) => pair(i, RefProp(r, EINum(k)))
    } | (refBase <~ "'s own property whose key is") ~ id ^^ {
      case b ~ p => pair(Nil, RefProp(toRef(b, "SubMap"), toERef(p)))
    } | "second to top element" ~> "of" ~> ref ^^ {
      case i ~ r => pair(i, RefProp(r, EBOp(OSub, ERef(RefProp(r, EStr("length"))), EINum(2))))
    } | (fieldName <~ ("of" | "for")) ~ refBase ^^ {
      case f ~ b => pair(Nil, toRef(b, f))
    } | (refBase <~ "'s") ~ (fieldName ||| camelWord ^^ { EStr(_) }) <~ opt("value" | "attribute") ^^ {
      case b ~ x => pair(Nil, RefProp(RefId(Id(b)), x))
    } | (id <~ "flag of") ~ ref ^^ {
      case x ~ (i ~ r) if x == "withEnvironment" => pair(i, RefProp(r, EStr(x)))
    } | refBase ~ rep(field) ^^ {
      case x ~ es =>
        val i = (List[Inst]() /: es) { case (is, i ~ _) => is ++ i }
        pair(i, (es.map { case i ~ e => e }).foldLeft[Ref](RefId(Id(x))) {
          case (r, e) => RefProp(r, e)
        })
    }
  )
  val refBase: P[String] = opt("the") ~> (
    "running execution context" ^^^ context |||
    "current Realm Record" ^^^ realm |||
    "arguments object" ^^^ "args" |||
    "execution context stack" ^^^ executionStack |||
    ty ~ "for which the method was invoked" ^^^ "this" |||
    "this" ~ opt(nt | "this") ^^^ "this" |||
    "reference" ~> id |||
    intrinsicName |||
    symbolName |||
    camelWord |||
    opt(ordinal) ~ nt ^^ { case k ~ x => x + k.getOrElse("") } |||
    opt("reference" | nt) ~> id <~ opt("flag")
  )
  lazy val refPre: P[String] = opt("the") ~> (
    "hint" |||
    "list that is" |||
    "string value of" |||
    "value of" |||
    "parsed code that is"
  ) ^^^ ""
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
  ) ^^ { EStr(_) } ||| internalName

  ////////////////////////////////////////////////////////////////////////////////
  // Section Numbers
  ////////////////////////////////////////////////////////////////////////////////
  lazy val secno: P[List[Int]] =
    number ~ rep("." ~> number) ^^ {
      case n ~ list => n.toInt :: list.map(_.toInt)
    }

  // method pointed by sections
  lazy val section: P[String] = (
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
  lazy val etcStmt: P[Inst] = "change its bound value to" ~> id ^^ {
    case x => parseInst(s"envRec.SubMap[N].BoundValue = $x")
  } | ("Perform the following substeps in an implementation - dependent order , possibly interleaving parsing and error detection :" ~> stmt |
    ("Set the code evaluation state of" ~> id <~ "such that when evaluation is resumed for that execution context the following steps will be performed :") ~ stmt ^^ {
      case x ~ s => {
        parseInst(s"""$x.ResumeCont = () [=>] ${beautify(s)}""")
      }
    } | ("Set the code evaluation state of" ~> id <~ "such that when evaluation is resumed with a Completion") ~ (id <~ "the following steps will be performed :") ~ stmt ^^ {
      case x ~ y ~ s => {
        parseInst(s"""$x.ResumeCont = ($y) [=>] ${beautify(s)}""")
      }
    } | ("Set the code evaluation state of" ~> id <~ "such that when evaluation is resumed with a Completion") ~ (id <~ ", the following steps of the algorithm that invoked Await will be performed ," <~ rest) ^^ {
      case x ~ y => {
        parseInst(s"""{
          access $retcont = ($x "ReturnCont")
          $x.ResumeCont = ($y) [=>] return $y
          }""")
      }
    } | ("Resume the suspended evaluation of" ~> id <~ "using") ~ (expr <~ "as the result of the operation that suspended it . Let") ~ (id <~ "be the" <~ ("value" | "completion record") <~ "returned by the resumed computation .") ^^ {
      case cid ~ (i ~ e) ~ rid => {
        val tempId = getTemp
        val tempId2 = getTemp
        ISeq(i :+ parseInst(s"""withcont $tempId ($rid) = {
            $cid.ReturnCont = $tempId
            app $tempId2 = ($cid.ResumeCont ${beautify(e)})
            }"""))
      }
    } | ("Resume the suspended evaluation of" ~> id <~ "using") ~ (expr <~ "as the result of the operation that suspended it .") ^^ {
      case cid ~ (i ~ e) => {
        val tempId = getTemp
        val tempId2 = getTemp
        ISeq(i :+ parseInst(s"""withcont $tempId () = {
            $cid.ReturnCont = $tempId
            app $tempId2 = ($cid.ResumeCont ${beautify(e)})
            }"""))
      }
    } | ("Resume the suspended evaluation of" ~> id <~ ". Let") ~ (id <~ "be the value returned by the resumed computation .") ^^ {
      case cid ~ rid => {
        val tempId = getTemp
        val tempId2 = getTemp
        parseInst(s"""withcont $tempId ($rid) = {
            $cid.ReturnCont = $tempId
            app $tempId2 = ($cid.ResumeCont)
            }""")
      }
    } | ("Assert : If we return here , the" ~ opt("async") ~ " generator either threw") <~ rest ^^^ {
      parseInst(s"""{
        access $retcont = (genContext "ReturnCont")
       }""")
    } | "Assert : If we return here , the async function either threw an exception or performed an implicit or explicit return ; all awaiting is done" ^^^ {
      parseInst(s"""{
        access $retcont = (asyncContext "ReturnCont")
      }""")
    } | "push" ~> expr <~ ("onto" | "on to") ~ "the execution context stack" ~ rest ^^ {
      case i ~ e => ISeq(i ++ List(IAppend(e, toERef(executionStack)), parseInst(s"""
        $context = $executionStack[(- $executionStack.length 1i)]
      """)))
    } | "if this method was called with more than one argument , then in left to right order , starting with the second argument , append each argument as the last element of" ~> name ^^ {
      case x => parseInst(s"""{
        if (< 0i argumentsList.length) (pop argumentsList 0i) else {}
        $x = argumentsList
      }""")
    } | "in an implementation - dependent manner , obtain the ecmascript source texts" ~ rest ^^^ {
      val temp = getTemp
      parseInst(s"""app $temp = (EnqueueJob "ScriptJobs" ScriptEvaluationJob (new [ script, hostDefined ]))""")
    } | (in ~ "if IsStringPrefix(" ~> name <~ ",") ~ (name <~ rep(rest ~ next) ~ out) ^^ {
      case x ~ y => parseInst(s"return (< $y $x)")
    } | ("if the mathematical value of" ~> name <~ "is less than the mathematical value of") ~ name <~ rest ^^ {
      case x ~ y => parseInst(s"return (< $x $y)")
    } | ("let" ~> id <~ "be a new built-in function object that when called performs the action described by") ~ id <~ "." ~ rest ^^ {
      case x ~ y => parseInst(s"""{
        let $x = (new BuiltinFunctionObject("SubMap" -> (new SubMap())))
        $x.Code = $y
      }""") // TODO handle internalSlotsList
    } | "if the host requires use of an exotic object" ~ rest ^^^ {
      parseInst("let global = undefined")
    } | "if the host requires that the" ~ rest ^^^ {
      parseInst("let thisValue = undefined")
    } | "perform any necessary implementation - defined initialization of" <~ rest ^^^ {
      parseInst(s"""{
        app localEnv = (NewFunctionEnvironment F undefined)
        calleeContext["LexicalEnvironment"] = localEnv
        calleeContext["VariableEnvironment"] = localEnv
      }""")
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
      val temp = getTemp
      forEachMap(Id(temp), toERef("Desc"), parseInst(s"""O.SubMap[P][$temp] = Desc[$temp]"""))
    } | ("convert the property named" ~> id) ~ ("of object" ~> id <~ "from a data property to an accessor property" <~ rest) ^^^ {
      val temp = getTemp
      parseInst(s"""{
        let $temp = O.SubMap[P]
        O.SubMap[P] = (new AccessorProperty("Get" -> undefined, "Set" -> undefined, "Enumerable" -> $temp["Enumerable"], "Configurable" -> $temp["Configurable"]))
      }""")
    } | ("convert the property named" ~> id) ~ ("of object" ~> id <~ "from an accessor property to a data property" <~ rest) ^^^ {
      val temp = getTemp
      parseInst(s"""{
        let $temp = O.SubMap[P]
        O.SubMap[P] = (new DataProperty("Value" -> undefined, "Writable" -> false, "Enumerable" -> $temp["Enumerable"], "Configurable" -> $temp["Configurable"]))
      }""")

    } | "parse" ~ id ~ "using script as the goal symbol and analyse the parse result for any early Error conditions" ~ rest ^^^ {
      parseInst(s"""let body = script""")
    } | (("suspend" ~> name <~ "and remove it from the execution context stack") | ("pop" ~> name <~ "from the execution context stack" <~ rest)) ^^ {
      case x => {
        val idx = getTemp
        parseInst(s"""{
        $context = null
        if (= $executionStack[(- $executionStack.length 1i)] $x) {
          $idx = (- $executionStack.length 1i)
          (pop $executionStack $idx)
        } else {}
      }""")
      }
    } | "suspend the currently running execution context" ^^^ {
      parseInst(s"""$context = null""")
    } | "suspend" ~> name ^^ {
      case x => parseInst(s"""{
        $context = null
        $x = null
      }""")
    } | "remove" ~> id <~ "from the execution context stack and restore" <~ rest ^^ {
      case x => {
        val idx = getTemp
        parseInst(s"""{
        if (= $executionStack[(- $executionStack.length 1i)] $x) {
          $idx = (- $executionStack.length 1i)
          (pop $executionStack $idx)
        } else {}
        $context = $executionStack[(- $executionStack.length 1i)]
      }""")
      }
    } | "resume the context that is now on the top of the execution context stack as the running execution context" ^^^ {
      parseInst(s"""$context = $executionStack[(- $executionStack.length 1i)]""")
    } | "let" ~> name <~ "be a newly created ecmascript function object with the internal slots listed in table 27." ^^ {
      case x => parseInst(s"""{
        let $x = (new ECMAScriptFunctionObject("SubMap" -> (new SubMap())))
        delete $x.Call
        delete $x.Construct
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
        app $temp = (DefinePropertyOrThrow global name desc)
        if (= (typeof $temp) "Completion") {
          if (= $temp.Type CONST_normal) $temp = $temp.Value
          else return $temp
        } else {}
      }"""))
    } | "for each own property key" ~> id ~> "of" ~> id <~ "that is an array index" <~ rest ^^^ { // TODO: considering order of property key
      val temp1 = getTemp
      val tempId = getTempId
      ISeq(List(
        parseInst(s"""let $temp1 = (map-keys O["SubMap"])"""),
        forEachList(tempId, toERef(temp1), IAppend(toERef(tempId), toERef("keys"))),
        parseInst(s"""return keys""")
      ))
    } | "increase" ~> id <~ "by 1" ^^ {
      case x => IAssign(RefId(Id(x)), EBOp(OPlus, toERef(x), EINum(1)))
    } | "if" ~> (name <~ "is") ~ grammar ~ ("," ~ opt("then") ~> stmt) ^^ {
      case x ~ Grammar(y, ss) ~ s =>
        val pre = ss.map(s => parseInst(s"""access $s = ($x "$s")"""))
        IIf(parseExpr(s"(is-instance-of $x $y)"), ISeq(pre :+ s), ISeq(Nil))
    } | (("set" ~> name <~ "'s essential internal methods" <~ rest) | ("Set the remainder of" ~> id <~ "'s essential internal methods to the default ordinary object definitions specified in 9.1")) ^^ {
      case s => parseInst(s"""{
        if (= $s["HasProperty"] absent) $s["HasProperty"] = OrdinaryObjectDOTHasProperty else {}
        if (= $s["DefineOwnProperty"] absent) $s["DefineOwnProperty"] = OrdinaryObjectDOTDefineOwnProperty else {}
        if (= $s["Set"] absent) $s["Set"] = OrdinaryObjectDOTSet else {}
        if (= $s["SetPrototypeOf"] absent) $s["SetPrototypeOf"] = OrdinaryObjectDOTSetPrototypeOf else {}
        if (= $s["Get"] absent) $s["Get"] = OrdinaryObjectDOTGet else {}
        if (= $s["PreventExtensions"] absent) $s["PreventExtensions"] = OrdinaryObjectDOTPreventExtensions else {}
        if (= $s["Delete"] absent) $s["Delete"] = OrdinaryObjectDOTDelete else {}
        if (= $s["GetOwnProperty"] absent) $s["GetOwnProperty"] = OrdinaryObjectDOTGetOwnProperty else {}
        if (= $s["OwnPropertyKeys"] absent) $s["OwnPropertyKeys"] = OrdinaryObjectDOTOwnPropertyKeys else {}
        if (= $s["GetPrototypeOf"] absent) $s["GetPrototypeOf"] = OrdinaryObjectDOTGetPrototypeOf else {}
        if (= $s["IsExtensible"] absent) $s["IsExtensible"] = OrdinaryObjectDOTIsExtensible else {}
      }""")
    } | ("append the pair ( a two element list ) consisting of" ~> expr) ~ ("and" ~> expr) ~ ("to the end of" ~> expr) ^^ {
      case (i0 ~ x) ~ (i1 ~ y) ~ (i2 ~ z) => ISeq(i0 ++ i1 ++ i2 :+
        IAppend(EList(List(x, y)), z))
    } | ("add" ~> id <~ "at the back of the job queue named by") <~ id ^^ {
      case x => IAppend(toERef(x), toERef(jobQueue))
    } | ("remove the own property with name" ~> name <~ "from") ~ name ^^ {
      case p ~ o => parseInst(s"delete $o.SubMap[$p]")
    } | "create an own data property" ~ rest ^^^ {
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
    } | ("create an immutable binding in" ~> name <~ "for") ~ (name <~ "and record that it is uninitialized . if") ~ (name <~ "is" <~ rest) ^^ {
      case x ~ y ~ z => parseInst(s"""$x.SubMap[$y] = (new ImmutableBinding("initialized" -> false, "strict" -> $z))""")
    } | ("record that the binding for" ~> name <~ "in") ~ name <~ "has been initialized" ^^ {
      case x ~ y => parseInst(s"if (! (= $y.SubMap[$x] absent)) $y.SubMap[$x].initialized = true else {}")
    } | ("Let" ~> id <~ "be a newly created object with an internal slot for each name in") ~ id ^^ {
      case obj ~ list =>
        val temp = getTempId
        ISeq(List(
          ILet(Id(obj), EMap(Ty("OrdinaryObject"), List(EStr("SubMap") -> EMap(Ty("SubMap"), Nil)))),
          forEachList(temp, toERef(list), IAssign(toRef(obj, toERef(temp)), EUndef))
        ))
    } | "Let" ~> id <~ "be a new Realm Record" ^^ {
      case x => ILet(Id(x), toERef(realm))
    }) | ignoreStmt

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

  // comments
  lazy val comment: P[Inst] = (
    "assert:" |
    "note:" |
    "this may be" |
    "as defined" |
    "( if" |
    "this call will always return" ~ value |
    (opt("(") <~ ("see" | "it may be"))
  ) ~ rest ^^^ emptyInst

  // extra fields for newly created objects
  lazy val extraFields: P[List[(Expr, Expr)]] = (
    "a" ~> internalName <~ "internal slot" ^^ { case x => List(x -> EUndef) } |||
    "internal slots" ~> rep1sep(internalName, sep("and")) ^^ { case xs => xs.map(_ -> EUndef) } |||
    id <~ "as the binding object" ^^ { case x => List(EStr("BindingObject") -> toERef(x)) } |||
    ("the internal slots listed in table" ~ number | opt("initially has") ~> "no fields" | "no bindings") ^^^ Nil
  )

  // et cetera expressions
  lazy val etcExpr: P[I[Expr]] = (
    ("the result of performing the abstract operation named by" ~> expr) ~ ("using the elements of" ~> expr <~ "as its arguments .") ^^ {
      case (i0 ~ e0) ~ (i1 ~ e1) => {
        val temp = getTemp
        val applyInst = parseInst(s"""app $temp = (${beautify(e0)} ${beautify(e1)}[0i] ${beautify(e1)}[1i] ${beautify(e1)}[2i])""")
        pair(List(applyInst), toERef(temp))
      }
    } | "a non - empty Job Queue chosen in an implementation - defined" ~ rest ^^^ {
      val checkInst = parseInst(s"""if (= $jobQueue.length 0) {
        return (new Completion( "Type" -> CONST_normal, "Value" -> undefined, "Target" -> CONST_empty))
      } else {}""")
      pair(List(checkInst), toERef(jobQueue))
    } | "the PendingJob record at the front of" ~> id <~ rest ^^ {
      case x => pair(Nil, parseExpr(s"""(pop $x 0i)"""))
    } | "an implementation - dependent String source code representation of" ~ rest ^^^ {
      pair(Nil, EStr(""))
    } | "the source text matched by" ~> name ^^ {
      case x => pair(Nil, EGetSyntax(toERef(x)))
    } | "the String value consisting of the single code unit" ~> name ^^ {
      case x => pair(Nil, parseExpr(x))
    } | "the" ~ ("mathematical" | "number") ~ "value that is the same sign as" ~> name <~ "and whose magnitude is floor(abs(" ~ name ~ "))" ^^ {
      case x => pair(Nil, parseExpr(s"(convert $x num2int)"))
    } | "the" ~ code ~ "where" ~> name <~ "is" ~ code ^^ {
      case x =>
        val str = getTemp
        pair(
          List(parseInst(s"""{
            let $str = (get-syntax $x)
            $str = (- $str 1i)
          }""")), parseExpr(str)
        )
    } | "the String representation of this Number value using the radix specified by" ~> name <~ rest ^^ {
      case radixNumber =>
        var temp = getTemp
        pair(List(parseInst(s"""{
          if (= x NaN) {
            app $temp = (WrapCompletion "NaN")
            return $temp
          } else {}
          if (|| (= x 0i) (= x -0.0)) {
            app $temp = (WrapCompletion "0")
            return $temp
          } else {}
          if (< x 0.0) {
            x = (- x)
            if (= x Infinity) {
              app $temp = (WrapCompletion "-Infinity")
              return $temp
            } else {}
            let $temp = (+ "-" (convert x num2str $radixNumber))
          } else {
            if (= x Infinity) {
              app $temp = (WrapCompletion "Infinity")
              return $temp
            } else {}
            let $temp = (convert x num2str $radixNumber)
          }
        }""")), parseExpr(s"$temp"))
    } | "the String value whose elements are , in order , the elements in the List" ~> name <~ rest ^^ {
      case l =>
        val s = getTemp
        val x = getTemp
        val idx = getTemp
        val len = getTemp
        pair(List(parseInst(s"""{
          let $s = ""
          let $idx = 0i
          let $len = $l.length
          while (< $idx $len) {
            let $x = $l[$idx]
            $s = (+ $s $x)
            $idx = (+ $idx 1i)
          }
        }""")), parseExpr(s))
    } | "the grammar symbol" ~> name ^^ {
      case x => pair(Nil, EStr(x))
    } | "a String according to Table 35" ^^^ {
      val temp = getTemp
      pair(List(parseInst(s"app $temp = (GetTypeOf val)")), toERef(temp))
    } | "the parenthesizedexpression that is covered by coverparenthesizedexpressionandarrowparameterlist" ^^^ {
      pair(Nil, EParseSyntax(toERef("this"), EStr("ParenthesizedExpression"), Nil))
    } | "the length of" ~> name ^^ {
      case x => pair(Nil, parseExpr(s"""$x.length"""))
    } | "the" ~ opt("actual") ~ "number of" ~ ("actual arguments" | "arguments passed to this function" ~ opt("call")) ^^^ {
      pair(Nil, parseExpr(s"""argumentsList.length"""))
    } | "the List of arguments passed to this function" ^^^ {
      pair(Nil, parseExpr("argumentsList"))
    } | ("the numeric value of the code unit at index" ~> name <~ "within") ~ name ^^ {
      case x ~ y => pair(Nil, parseExpr(s"$y[$x]"))
    } | "the active function object" ^^^ {
      pair(Nil, parseExpr(s"""$context.Function"""))
    } | ("a zero - origined list containing the argument items in order" | ("the" ~ id ~ "that was passed to this function by" ~ rest)) ^^^ {
      pair(Nil, parseExpr(s"""argumentsList"""))
    } | "an iterator object ( 25 . 1 . 1 . 2 ) whose" <~ code <~ "method iterates" <~ rest ^^^ {
      val temp = getTemp
      val temp2 = getTemp
      pair(List(
        parseInst(s"""app $temp = (EnumerateObjectPropertiesHelper O (new []) (new []))"""),
        parseInst(s"""app $temp2 = (CreateListIteratorRecord $temp)""")
      ), toERef(temp2))
    } | "the ecmascript code that is the result of parsing" ~> id <~ ", interpreted as utf - 16 encoded unicode text" <~ rest ^^^ {
      pair(Nil, parseExpr(s"""(parse-syntax x "Script")""")) // TODO : throw syntax error
    } | ("the" ~> name <~ "that is covered by") ~ expr ^^ {
      case r ~ (i ~ e) => pair(i, EParseSyntax(e, EStr(r), Nil))
    } | "the string that is the only element of" ~> expr ^^ {
      case i ~ e =>
        val temp = getTemp
        pair(i :+ IAccess(Id(temp), e, EINum(0)), toERef(temp))
    } | ("the result of" ~> name <~ "minus the number of elements of") ~ name ^^ {
      case x ~ y => pair(Nil, parseExpr(s"(- $x $y.length)"))
    } | ("the larger of" ~> expr <~ "and") ~ expr ^^ {
      case (i0 ~ x) ~ (i1 ~ y) =>
        val a = beautify(x)
        val b = beautify(y)
        val temp = getTemp
        pair(i0 ++ i1 :+ parseInst(s"""{
          if (< $a $b) let $temp = $b
          else let $temp = $a
        }"""), parseExpr(temp))
    } | ("the result of applying" ~> id <~ "to") ~ (id <~ "and") ~ (id <~ "as if evaluating the expression" ~ id ~ id ~ id) ^^ {
      case op ~ l ~ r =>
        val res = getTempId
        val opERef = toERef(op)
        val list = List(
          ("*", OMul, "ToNumber", "ToNumber"),
          ("/", ODiv, "ToNumber", "ToNumber"),
          ("%", OMod, "ToNumber", "ToNumber"),
          ("-", OSub, "ToNumber", "ToNumber"),
          ("<<", OLShift, "ToInt32", "ToUint32"),
          (">>", OSRShift, "ToInt32", "ToUint32"),
          (">>>", OURShift, "ToUint32", "ToUint32"),
          ("&", OBAnd, "ToInt32", "ToInt32"),
          ("^", OBXOr, "ToInt32", "ToInt32"),
          ("|", OBOr, "ToInt32", "ToInt32"),
          ("**", OPow, "ToNumber", "ToNumber")
        )
        def getMap(x: Expr, names: List[String]): Map[String, List[Inst] ~ Expr] =
          (Map[String, List[Inst] ~ Expr]() /: names) {
            case (m, name) =>
              val temp = getTempId
              m + (name -> returnIfAbrupt(List(IApp(temp, toERef(name), List(x))), toERef(temp)))
          }
        val names = List("ToNumber", "ToInt32", "ToUint32", "ToPrimitive")
        val lmap = getMap(toERef(l), names)
        val rmap = getMap(toERef(r), names)
        val init: Inst = IExpr(ENotSupported("assign operator"))
        val genenralCase = (init /: list) {
          case (base, (name, op, left, right)) =>
            val li ~ le = lmap(left)
            var ri ~ re = rmap(right)
            IIf(
              EBOp(OEq, opERef, EStr(name)),
              ISeq(li ++ ri :+ IAssign(toRef(res), EBOp(op, le, re))),
              base
            )
        }
        val lprimI ~ lprim = lmap("ToPrimitive")
        val rprimI ~ rprim = rmap("ToPrimitive")
        val lpstrI ~ lpstr = getMap(lprim, List("ToString"))("ToString")
        val lpnumI ~ lpnum = getMap(lprim, List("ToNumber"))("ToNumber")
        var rpstrI ~ rpstr = getMap(rprim, List("ToString"))("ToString")
        var rpnumI ~ rpnum = getMap(rprim, List("ToNumber"))("ToNumber")
        val (ltemp, rtemp) = (getTempId, getTempId)
        val str = EStr("String")
        pair(List(IIf(EBOp(OEq, opERef, EStr("+")), ISeq(lprimI ++ rprimI ++ List(
          IApp(ltemp, toERef("Type"), List(lprim)),
          IApp(rtemp, toERef("Type"), List(rprim)),
          IIf(
            EBOp(OOr, EBOp(OEq, toERef(ltemp), str), EBOp(OEq, toERef(rtemp), str)),
            ISeq(lpstrI ++ rpstrI :+ IAssign(toRef(res), EBOp(OPlus, lpstr, rpstr))),
            ISeq(lpnumI ++ rpnumI :+ IAssign(toRef(res), EBOp(OPlus, lpnum, rpnum)))
          )
        )), genenralCase)), toERef(res))
    } | "the result of adding the value 1 to" ~> name <~ rest ^^ {
      case x => pair(Nil, parseExpr(s"(+ $x 1)"))
    } | "the result of subtracting the value 1 from" ~> name <~ rest ^^ {
      case x => pair(Nil, parseExpr(s"(- $x 1)"))
    } | ("the result of" ~> expr <~ "passing") ~ expr ~ ("and" ~> expr <~ "as the arguments") ^^ {
      case (i0 ~ f) ~ (i1 ~ x) ~ (i2 ~ y) =>
        val temp = getTempId
        pair((i0 ++ i1 ++ i2) :+ IApp(temp, f, List(x, y)), ERef(RefId(temp)))
    } | ("the sequence of code units consisting of the elements of" ~> name <~ "followed by the code units of") ~ name ~ ("followed by the elements of" ~> name) ^^ {
      case x ~ y ~ z => pair(Nil, parseExpr(s"(+ (+ $x $y) $z)"))
    } | ("the sequence of code units consisting of the code units of" ~> name <~ "followed by the elements of") ~ name ^^ {
      case x ~ y => pair(Nil, parseExpr(s"(+ $x $y)"))
    } | "the String value consisting of the code units of" ~> expr ^^ {
      case p => p
    } | ("the string-concatenation of" ~> name <~ ", the code unit 0x0020(SPACE) , and") ~ name ^^ {
      case x ~ y => pair(Nil, parseExpr(s"""(+ (+ $x " ") $y)"""))
    } | ("the string - concatenation of" ~> opt("the previous value of") ~> expr <~ "and") ~ expr ^^ {
      case (i0 ~ e1) ~ (i1 ~ e2) =>
        pair(i0 ++ i1, EBOp(OPlus, e1, e2))
    } | "-" ~> expr ^^ {
      case i ~ e => pair(i, EUOp(ONeg, e))
    } | "the number of" ~ (opt("code unit") ~ "elements" | "code units") ~ ("in" | "of") ~> ref ^^ {
      case i ~ r => pair(i, ERef(RefProp(r, EStr("length"))))
    } | ("the result of performing abstract relational comparison" ~> name <~ "<") ~ name ~ opt("with" ~ name ~ "equal to" ~> expr) ^^ {
      case x ~ y ~ Some(i ~ e) =>
        val temp = getTemp
        pair(i :+ parseInst(s"app $temp = (AbstractRelationalComparison $x $y ${beautify(e)})"), toERef(temp))
      case x ~ y ~ None =>
        val temp = getTemp
        pair(List(parseInst(s"app $temp = (AbstractRelationalComparison $x $y)")), toERef(temp))
    } | "the string - concatenation of" ~> repsep(expr, "," ~ opt("and")) ^^ {
      case es =>
        val init: I[Expr] = pair(Nil, EStr(""))
        val insts ~ e = (init /: es) { case (i0 ~ l, i1 ~ r) => pair(i0 ++ i1, EBOp(OPlus, l, r)) }
        (pair(insts, e): I[Expr])
    } | (("the result of performing abstract equality comparison" ~> id <~ "= =") ~ id) ^^ {
      case x1 ~ x2 =>
        val temp = getTempId
        pair(List(IApp(temp, toERef("AbstractEqualityComparison"), List(toERef(x1), toERef(x2)))), ERef(RefId(temp)))
    } | (("the result of performing strict equality comparison" ~> id <~ "= = =") ~ id) ^^ {
      case x1 ~ x2 =>
        val temp = getTempId
        pair(List(IApp(temp, toERef("StrictEqualityComparison"), List(toERef(x1), toERef(x2)))), ERef(RefId(temp)))
    } | ("the result of applying the multiplicativeoperator" <~ rest) ^^^ {
      val temp = getTemp
      pair(List(parseInst(s"app $temp = ( MulOperation (get-syntax MultiplicativeOperator) lnum rnum)")), toERef(temp))
    } | "the completion record that is the result of evaluating" ~> name <~ "in an implementation - defined manner that conforms to the specification of" ~ name ~ "." ~ name ~ "is the" ~ rest ^^ {
      case f =>
        val temp = getTemp
        pair(List(parseInst(s"app $temp = ($f.Code thisArgument argumentsList undefined $f)")), toERef(temp))
    } | "the completion record that is the result of evaluating" ~> name <~ "in an implementation - defined manner that conforms to the specification of" ~ name ~ ". the" ~ rest ^^ {
      case f =>
        val temp = getTemp
        pair(List(parseInst(s"app $temp = ($f.Code undefined argumentsList newTarget $f)")), toERef(temp))
    } | ("a value of type reference whose base value component is" ~> expr) ~
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

    } | "a new (possibly empty) List consisting of all of the argument values provided after" ~ name ~ "in order" ^^^ {
      pair(List(parseInst(s"if (< 0i argumentsList.length) (pop argumentsList 0i) else {}")), parseExpr("argumentsList"))
    } | "a List whose elements are , in left to right order , the arguments that were passed to this function invocation" ^^^ {
      pair(Nil, parseExpr("argumentsList"))
    } | "a List whose elements are, in left to right order, the portion of the actual argument list starting with the third argument" ~ rest ^^^ {
      pair(List(parseInst(s"""if (< 2i argumentsList.length) {
            (pop argumentsList 0i)
            (pop argumentsList 0i)
        } else { argumentsList = (new [])
        }""")), parseExpr("argumentsList"))
    } ||| ("the List of" ~> name <~ "items in") ~ (ref <~ ", in source text order") ^^ {
      case x ~ (i ~ r) => pair(i, parseExpr(s"(get-elems ${beautify(r)} $x)"))
    } ||| ("a List whose first element is" ~> expr <~ ", whose second elements is") ~ expr ~ (", and whose subsequent elements are the elements of" ~> expr <~ rest) ^^ {
      case x ~ y ~ z => getCopyList(z, List(y, x), true)
    } ||| "a List whose first element is" ~> name <~ "and whose subsequent elements are, in left to right order, the arguments that were passed to this function invocation" ^^ {
      case x => pair(List(parseInst(s"prepend $x -> argumentsList")), parseExpr("argumentsList"))
    } | "the string value whose code units are the sv of stringliteral" ^^^ {
      val temp = getTemp
      pair(List(parseInst(s"""access $temp = (StringLiteral "SV")""")), toERef(temp))
    } | (("the Number value represented by" ~> name) | ("the result of forming the value of the" ~> name)) <~ rest ^^ {
      case x =>
        val temp = getTemp
        pair(List(parseInst(s"""access $temp = ($x "MV")""")), toERef(temp))
    } | opt("the String value whose code units are the elements of") ~> "the TV of" ~> name <~ opt("as defined in 11.8.6") ^^ {
      case x =>
        val temp = getTemp
        pair(List(parseInst(s"""access $temp = ($x "TV")""")), toERef(temp))
    } | "the TRV of" ~> name <~ opt("as defined in 11.8.6") ^^ {
      case x =>
        val temp = getTemp
        pair(List(parseInst(s"""access $temp = ($x "TRV")""")), toERef(temp))
    } | ((
      "CoveredCallExpression of CoverCallExpressionAndAsyncArrowHead" ^^^ {
        parseExpr("""(parse-syntax CoverCallExpressionAndAsyncArrowHead "CallMemberExpression")""")
      } | "the steps of an" ~> name <~ "function as specified below" ^^ {
        case x => parseExpr(s"$x")
      } | "the result of parsing the source text" ~> code <~ rest ^^ {
        case s => parseExpr(s"""(parse-syntax "$s" "MethodDefinition" false false)""")
      } | "the result of applying bitwise complement to" ~> name <~ rest ^^ {
        case x => EUOp(OBNot, toERef(x))
      } | "the result of masking out all but the least significant 5 bits of" ~> name <~ rest ^^ {
        case x => parseExpr(s"(& $x 31i)")
      } | ("the result of left shifting" ~> name <~ "by") ~ (name <~ "bits" ~ rest) ^^ {
        case x ~ y => parseExpr(s"(<< $x $y)")
      } | ("the result of performing a sign-extending right shift of" ~> name <~ "by") ~ (name <~ "bits" ~ rest) ^^ {
        case x ~ y => parseExpr(s"(>> $x $y)")
      } | ("the result of performing a zero-filling right shift of" ~> name <~ "by") ~ (name <~ "bits" ~ rest) ^^ {
        case x ~ y => parseExpr(s"(>>> $x $y)")
      } | ("the result of applying the addition operation to" ~> id <~ "and") ~ id ^^ {
        case e1 ~ e2 => EBOp(OPlus, toERef(e1), toERef(e2))
      } | ("the result of applying the ** operator with" ~> id <~ "and") ~ id <~ rest ^^ {
        case e1 ~ e2 => EBOp(OPow, toERef(e1), toERef(e2))
      } | ("the result of applying the subtraction operation to" <~ rest) ^^ {
        case _ => EBOp(OSub, toERef("lnum"), toERef("rnum"))
      } | ("the result of negating" ~> id <~ rest) ^^ {
        case x => EUOp(ONeg, toERef(x))
      } | "the definition specified in 9.2.1" ^^^ {
        parseExpr("ECMAScriptFunctionObjectDOTCall")
      } | "the definition specified in 9.2.2" ^^^ {
        parseExpr("ECMAScriptFunctionObjectDOTConstruct")
      } | "the empty string" ^^ {
        case x => EStr("")
      } | opt("the") ~ value.filter(x => x == "this") ~ "value" ^^^ {
        parseExpr("this")
      } | "an instance of the production formalparameters0" ^^^ {
        parseExpr(s"""(parse-syntax "" "FormalParameters" false false)""")
      } | "a new object Environment Record containing" ~> id <~ "as the binding object" ^^ {
        case x => EMap(Ty("ObjectEnvironmentRecord"), List(
          EStr("SubMap") -> EMap(Ty("SubMap"), Nil),
          EStr("BindingObject") -> toERef(x)
        ))
      } | "a List whose elements are the arguments passed to this function" ^^^ {
        toERef("argumentsList")
      } | "the current Realm Record" ^^^ {
        toERef(realm)
      } | "a new unique Symbol value whose [[Description]] value is" ~> name ^^ {
        case x => parseExpr(s"(new '$x)")
      }
    ) ^^ { case e => pair(Nil, e) })
  )
}
