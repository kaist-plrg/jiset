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
    } | (nt <~ camelWord.filter(_ == "Contains")) ~ (nt ^^ { EStr(_) } | id ^^ { toERef(_) }) ^^^ {
      pair(Nil, ENotSupported("Contains"))
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
    hexValue |||
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

  // hex values
  lazy val hexValue: P[Expr] =
    opt("the") ~> opt("code unit") ~> hex <~ "(" ~ rep(normal.filter(_ != Text(")"))) ~ ")" ^^ {
      case x => EStr(Character.toChars(x).mkString)
    } ||| hex ^^ { EINum(_) }
  lazy val hex: P[Int] = text.filter(_ startsWith "0x") ^^ {
    case s => Integer.parseInt(s.substring(2), 16)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Conditions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val cond: P[I[Expr]] = _cond <~ guard("," | in)
  lazy val _cond: P[I[Expr]] = (
    expr ~ condBOp ~ expr ^^ {
      case (i0 ~ x) ~ ((b, n, r)) ~ (i1 ~ y) => pair(i0 ++ i1, calc(n, r, b, x, y))
    } ||| _cond ~ (condOp <~ opt("if")) ~ _cond ^^ {
      case (i ~ l) ~ ((op, _)) ~ (i1 ~ r) if isEmptyInsts(i1) => pair(i, EBOp(op, l, r))
      case (i0 ~ l) ~ ((op, f)) ~ (i1 ~ r) =>
        val temp = getTempId
        val (t, e) = f(ISeq(i1 :+ IAssign(toRef(temp), r)))
        pair(i0 ++ List(ILet(temp, l), IIf(toERef(temp), t, e)), toERef(temp))
    } ||| expr ~ rep1sep(rhs, sep("or")) ^^ {
      case (i ~ r) ~ fs => pair(i, fs.map(_(r)).reduce(EBOp(OOr, _, _)))
    } ||| expr <~ "is strict mode code" ^^^ {
      pair(Nil, EBool(true)) // TODO : support strict mode code
    } | (id <~ "does not have" ~ ("a" | "an")) ~ internalName <~ ("field" | "internal slot") ^^ {
      case x ~ y => pair(Nil, EBOp(OEq, toERef(x, y), EAbsent))
    } ||| containsExpr
  )
  lazy val condBOp: P[(BOp, Boolean, Boolean)] = (
    ("=" | "equals") ^^^ (OEq, false, false) |||
    ("≠" | "is different from") ^^^ (OEq, true, false) |||
    ("<" | "is less than") ^^^ (OLt, false, false) |||
    "≥" ^^^ (OLt, true, false) |||
    (">" | "is greater than") ^^^ (OLt, false, true) |||
    "≤" ^^^ (OLt, true, true)
  )
  lazy val rhs: P[Expr => Expr] = (
    equalRhs |||
    notEqualRhs
  )

  lazy val equalRhs: P[Expr => Expr] = {
    "is" ~ opt("present and its value is") |||
      "has the value"
  } ~ opt("either") ~> rep1sep({
    (valueParser ||| (id | camelWord) ^^ { toERef(_) }) ^^ {
      case r => (l: Expr) => EBOp(OEq, l, r)
    } ||| ("absent" | "not present" | "not supplied") ^^^ {
      (l: Expr) => EBOp(OEq, l, EAbsent)
    } ||| ("not absent" | "present") ^^^ {
      (l: Expr) => EUOp(ONot, EBOp(OEq, l, EAbsent))
    }
  } <~ guard("," | "or" | "and" | in), sep("or")) ^^ {
    case fs => (l: Expr) => fs.map(_(l)).reduce(EBOp(OOr, _, _))
  }

  lazy val notEqualRhs: P[Expr => Expr] = {
    "is" ~ ("not" ~ opt("one of") | "neither")
  } ~> rep1sep({
    (valueParser ||| (id | camelWord) ^^ { toERef(_) }) ^^ {
      case r => (l: Expr) => (EBOp(OEq, l, r): Expr)
    }
  } <~ guard("," | "or" | "and" | "nor" | in), sep("nor" | "or")) ^^ {
    case fs => (l: Expr) => EUOp(ONot, fs.map(_(l)).reduce(EBOp(OOr, _, _)))
  }

  lazy val condOp: P[(BOp, Inst => (Inst, Inst))] = opt(",") ~> (
    "or" ^^^ { (OOr, (x: Inst) => (emptyInst, x)) } |||
    "and" ^^^ { (OAnd, (x: Inst) => (x, emptyInst)) }
  )

  ////////////////////////////////////////////////////////////////////////////////
  // Types
  ////////////////////////////////////////////////////////////////////////////////
  lazy val ty: P[Ty] = (
    "AsyncGeneratorRequest" ^^^ Ty("AsyncGeneratorRequest") |||
    "PromiseCapability" ^^^ Ty("PromiseCapability") |||
    "PromiseReaction" ^^^ Ty("PromiseReaction") |||
    "arguments exotic object" ^^^ Ty("ArgumentsExoticObject") |||
    "WriteSharedMemory" ^^^ Ty("WriteSharedMemory") |||
    "ReadSharedMemory" ^^^ Ty("ReadSharedMemory") |||
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
    "chosen value record" ^^^ Ty("ChosenValueRecord") |||
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


  lazy val etcStmt: P[Inst] = ignoreStmt

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

  lazy val subCond: P[Expr => I[Expr]] =
    guard(("," | in) ^^^ ((x: Expr) => pair(List[Inst](), x)))

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
}
