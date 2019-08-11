package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.algorithm
import algorithm.{ AlgoKind, Algorithm, Token, StaticSemantics, Method, Grammar }
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

trait AlgoCompilerHelper extends TokenParsers {
  val algoName: String
  val kind: AlgoKind

  // empty instruction
  lazy val emptyInst: Inst = ISeq(Nil)

  // result type
  type Result = Inst
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

  // execution context stack string
  val executionStack = "GLOBAL_executionStack"
  val context = "GLOBAL_context"
  val jobQueue = "GLOBAL_jobQueue"
  val retcont = "__ret__"

  ////////////////////////////////////////////////////////////////////////////////
  // Instructions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val stmt: P[Inst] = {
    etcStmt | (
      comment |||
      returnStmt |||
      letStmt |||
      innerStmt |||
      ifStmt |||
      callStmt |||
      setStmt |||
      recordStmt |||
      incrementStmt |||
      decrementStmt |||
      createStmt |||
      throwStmt |||
      whileStmt |||
      forEachStmt |||
      appendStmt |||
      insertStmt |||
      removeStmt |||
      starStmt
    )
  } <~ opt(".") ~ opt(comment)

  lazy val comment: P[Inst] = (
    "assert:" |
    "note:" |
    "this may be" |
    "as defined" |
    "( if" |
    "this call will always return" ~ value |
    (opt("(") <~ ("see" | "it may be"))
  ) ~ rest ^^^ emptyInst

  // etc statements
  lazy val etcStmt: P[Inst] = (
    "Perform the following substeps in an implementation - dependent order , possibly interleaving parsing and error detection :" ~> stmt |
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
    } | ("Once a generator enters the" | "Assert : If we return here , the async generator either threw") <~ rest ^^^ {
      parseInst(s"""{
        delete genContext.ResumeCont
        access $retcont = (genContext "ReturnCont")
        delete genContext.ReturnCont
       }""")
    } | "Assert : If we return here , the async function either threw an exception or performed an implicit or explicit return ; all awaiting is done" ^^^ {
      parseInst(s"""{
        delete asyncContext.ResumeCont
        access $retcont = (asyncContext "ReturnCont")
        delete asyncContext.ReturnCont
      }""")
    } | "push" ~> expr <~ ("onto" | "on to") ~ "the execution context stack" ~ rest ^^ {
      case i ~ e => ISeq(i ++ List(IAppend(e, toERef(executionStack)), parseInst(s"""
        $context = $executionStack[(- $executionStack.length 1i)]
      """)))
    } | "if this method was called with more than one argument , then in left to right order , starting with the second argument , append each argument as the last element of" ~> name ^^ {
      case x => parseInst(s"""{
        (pop argumentsList 0i)
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
    }
  ) | ignoreStmt

  // ignore statements
  lazy val ignoreStmt: P[Inst] = (
    "set fields of" |
    "need to defer setting the" |
    "create any implementation-defined" |
    "no further validation is required" |
    "if" ~ id ~ "is a List of errors," |
    "order the elements of" ~ id ~ "so they are in the same relative order as would" |
    "Perform any implementation or host environment defined processing of" |
    "Perform any implementation or host environment defined job initialization using"
  ) ~ rest ^^^ emptyInst

  // return statements
  lazy val returnStmt: P[Inst] = (
    "return" ~> expr ^^ {
      case ie => kind match {
        case StaticSemantics => getRet(ie)
        case Method if algoName == "OrdinaryGetOwnProperty" => getRet(ie)
        case _ => getRet(getWrapCompletion(ie))
      }
    } ||| "return" ^^^ {
      getRet(getNormalCompletion(EUndef))
    } ||| ("ReturnCont" ~> expr <~ "to") ~ expr ^^ {
      case (i ~ f) ~ ie => ISeq(i :+ getInst(getCall(f, List(ie))))
    } ||| "ReturnCont" ~> expr ^^ {
      case ie => getInst(getCall(retcont, List(ie)))
    } ||| "ReturnCont" ^^^ {
      getInst(getCall(retcont, List(getNormalCompletion(EUndef))))
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
  lazy val innerStmt: P[Inst] = in ~> stmts <~ out ^^ { ISeq(_) }

  // if-then-else statements
  lazy val ifStmt =
    "if" ~> (name <~ "is") ~ grammar ~ ("," ~ opt("then") ~> stmt) ^^ {
      case x ~ Grammar(y, ss) ~ s =>
        val pre = ss.map(s => parseInst(s"""access $s = ($x "$s")"""))
        IIf(parseExpr(s"(is-instance-of $x $y)"), ISeq(pre :+ s), ISeq(Nil))
    } | ("if" ~> cond <~ "," <~ opt("then")) ~ stmt ~ (
      opt("." | ";" | ",") ~> opt(next) ~> ("else" | "otherwise") ~> opt(
        "the order of evaluation needs to be reversed to preserve left to right evaluation" |
          name ~ "is added as a single item rather than spread" |
          name ~ "contains a formal parameter mapping for" ~ name |
          name ~ "is a Reference to an Environment Record binding" |
          "the base of" ~ ref ~ "is an Environment Record" |
          name ~ "must be" ~ rep(not(",") ~ text) |
          id ~ "does not currently have a property" ~ id |
          id <~ "is an accessor property" |
          ("isaccessordescriptor(" ~> id <~ ") and isaccessordescriptor(") ~ (id <~ ") are both") ~ expr |
          cond
      ) ~> opt(",") ~> stmt
    ) ^^ {
          case (i ~ c) ~ t ~ e => ISeq(i :+ IIf(c, t, e))
        } | ("if" ~> cond <~ "," <~ opt("then")) ~ stmt ^^ {
          case (i ~ c) ~ t => ISeq(i :+ IIf(c, t, emptyInst))
        }

  // call statements
  lazy val callStmt: P[Inst] = ("perform" | "call") ~> expr ^^ {
    case i ~ e => ISeq(i :+ IExpr(e))
  } | returnIfAbruptExpr ^^ {
    case i ~ e => ISeq(i :+ IExpr(e))
  }

  // set statements
  lazy val setStmt =
    (("set" ~> name <~ "'s essential internal methods" <~ rest) | ("Set the remainder of" ~> id <~ "'s essential internal methods to the default ordinary object definitions specified in 9.1")) ^^ {
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
    } | ("set" ~> ref) ~ ("to" ~> expr) ^^ {
      case (i0 ~ r) ~ (i1 ~ e) => ISeq(i0 ++ i1 :+ IAssign(r, e))
    } | ("set the bound value for" ~> expr <~ "in") ~ expr ~ ("to" ~> expr) ^^ {
      case (i0 ~ p) ~ (i1 ~ e) ~ (i2 ~ v) =>
        ISeq(i0 ++ i1 ++ i2 :+ parseInst(s"${beautify(e)}.SubMap[${beautify(p)}].BoundValue = ${beautify(v)}"))
    } | ("set" ~> ref) ~ ("as" ~ ("described" | "specified") ~ "in" ~> (
      "9.4.1.1" ^^^ parseExpr(getScalaName("BoundFunctionExoticObject.Call")) |
      "9.4.1.2" ^^^ parseExpr(getScalaName("BoundFunctionExoticObject.Construct")) |
      "9.4.2.1" ^^^ parseExpr(getScalaName("ArrayExoticObject.DefineOwnProperty")) |
      "9.4.3.1" ^^^ parseExpr(getScalaName("StringExoticObject.GetOwnProperty")) |
      "9.4.3.2" ^^^ parseExpr(getScalaName("StringExoticObject.DefineOwnProperty")) |
      "9.4.3.3" ^^^ parseExpr(getScalaName("StringExoticObject.OwnPropertyKeys")) |
      "9.4.4.1" ^^^ parseExpr(getScalaName("ArgumentsExoticObject.GetOwnProperty")) |
      "9.4.4.2" ^^^ parseExpr(getScalaName("ArgumentsExoticObject.DefineOwnProperty")) |
      "9.4.4.3" ^^^ parseExpr(getScalaName("ArgumentsExoticObject.Get")) |
      "9.4.4.4" ^^^ parseExpr(getScalaName("ArgumentsExoticObject.Set")) |
      "9.4.4.5" ^^^ parseExpr(getScalaName("ArgumentsExoticObject.Delete")) |
      "9.4.5.1" ^^^ parseExpr(getScalaName("IntegerIndexedExoticObject.GetOwnProperty")) |
      "9.4.5.2" ^^^ parseExpr(getScalaName("IntegerIndexedExoticObject.HasProperty")) |
      "9.4.5.3" ^^^ parseExpr(getScalaName("IntegerIndexedExoticObject.DefineOwnProperty")) |
      "9.4.5.4" ^^^ parseExpr(getScalaName("IntegerIndexedExoticObject.Get")) |
      "9.4.5.5" ^^^ parseExpr(getScalaName("IntegerIndexedExoticObject.Set")) |
      "9.4.5.6" ^^^ parseExpr(getScalaName("IntegerIndexedExoticObject.OwnPropertyKeys")) |
      "9.5.12" ^^^ parseExpr(getScalaName("ProxyExoticObject.Call")) |
      "9.5.13" ^^^ parseExpr(getScalaName("ProxyExoticObject.Construct"))
    )) ^^ {
        case (i ~ r) ~ e => ISeq(i :+ IAssign(r, e))
      }

  // record statements
  lazy val recordStmt =
    ("record that the binding for" ~> name <~ "in") ~ name <~ "has been initialized" ^^ {
      case x ~ y => parseInst(s"if (! (= $y.SubMap[$x] absent)) $y.SubMap[$x].initialized = true else {}")
    }

  // increment statements
  lazy val incrementStmt =
    ("increment" ~> ref <~ "by") ~ expr ^^ {
      case (i0 ~ x) ~ (i1 ~ y) => ISeq(i0 ++ i1 :+ IAssign(x, EBOp(OPlus, ERef(x), y)))
    }

  // decrement statements
  lazy val decrementStmt =
    (("decrement" | "decrease") ~> ref <~ "by") ~ expr ^^ {
      case (i0 ~ x) ~ (i1 ~ y) => ISeq(i0 ++ i1 :+ IAssign(x, EBOp(OSub, ERef(x), y)))
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
    ("for each" ~ opt(nt | "string" | "element" | "parse node") ~> id) ~ (("in order from" | "in" | "of" | "from") ~> expr <~ opt(",") ~ opt("(NOTE: this is another complete iteration of the second CaseClauses),") ~ opt("in list order," | "in original insertion order,") ~ "do") ~ stmt ^^ {
      case x ~ (i ~ e) ~ b => ISeq(i :+ forEachList(Id(x), e, b))
    } | ("for each" ~> id) ~ ("in" ~> expr <~ ", in reverse list order , do") ~ stmt ^^ {
      case x ~ (i ~ e) ~ b => ISeq(i :+ forEachList(Id(x), e, b, true))
    } | ("for each" ~ opt("Record { [ [ Key ] ] , [ [ Value ] ] }") ~> id <~ "that is an element of") ~ (expr <~ ", do") ~ stmt ^^ {
      case x ~ (i ~ e) ~ b => ISeq(i :+ forEachList(Id(x), e, b))
    }

  // append statements
  lazy val appendStmt: P[Inst] = (
    ("append" ~> expr) ~ ("to" ~ opt("the end of") ~> expr) ^^ {
      case (i0 ~ x) ~ (i1 ~ y) => ISeq(i0 ++ i1 :+ IAppend(x, y))
    } | ("append the pair ( a two element list ) consisting of" ~> expr) ~ ("and" ~> expr) ~ ("to the end of" ~> expr) ^^ {
      case (i0 ~ x) ~ (i1 ~ y) ~ (i2 ~ z) => ISeq(i0 ++ i1 ++ i2 :+
        IAppend(EList(List(x, y)), z))
    } | ("add" ~> id <~ "at the back of the job queue named by") <~ id ^^ {
      case x => IAppend(toERef(x), toERef(jobQueue))
    } | (("append" | "add") ~> expr) ~ ("as" ~ ("an" | "the last") ~ "element of" ~ opt("the list") ~> opt("that is") ~> expr) ^^ {
      case (i0 ~ x) ~ (i1 ~ y) => ISeq(i0 ++ i1 :+ IAppend(x, y))
    } | ("append each item in" ~> expr <~ "to the end of") ~ expr ^^ {
      case (i0 ~ l1) ~ (i1 ~ l2) =>
        val tempId = getTempId
        ISeq(i0 ++ i1 :+ forEachList(tempId, l1, IAppend(toERef(tempId), l2)))
    } | ("append to" ~> expr <~ opt("the elements of")) ~ expr ^^ {
      case (i0 ~ l1) ~ (i1 ~ l2) =>
        val tempId = getTempId
        ISeq(i0 ++ i1 :+ forEachList(tempId, l2, IAppend(toERef(tempId), l1)))
    }
  )

  // append statements
  lazy val insertStmt: P[Inst] = (
    ("insert" ~> expr <~ "as the first element of") ~ expr ^^ {
      case (i0 ~ x) ~ (i1 ~ y) =>
        ISeq(i0 ++ i1 :+ IPrepend(x, y))
    }
  )

  // remove statements
  lazy val removeStmt: P[Inst] = (
    ("remove the own property with name" ~> name <~ "from") ~ name ^^ {
      case p ~ o => parseInst(s"delete $o.SubMap[$p]")
    } | ("remove the first element from" ~> name <~ "and let") ~ (name <~ "be the value of" ~ ("that" | "the") ~ "element") ^^ {
      case l ~ x => parseInst(s"let $x = (pop $l 0i)")
    }
  )

  ////////////////////////////////////////////////////////////////////////////////
  // Expressions
  ////////////////////////////////////////////////////////////////////////////////

  lazy val expr: P[I[Expr]] = (
    arithExpr |
    etcExpr |
    completionExpr |
    listExpr |
    newExpr |
    valueExpr ^^ { pair(Nil, _) } |
    curExpr ^^ { pair(Nil, _) } |
    algoExpr ^^ { pair(Nil, _) } |
    containsExpr |
    returnIfAbruptExpr |
    callExpr |
    typeExpr ^^ { pair(Nil, _) } |
    accessExpr |
    refExpr |
    starExpr
  )

  lazy val arithExpr: P[I[Expr]] = expr ~ bop ~ expr ^^ {
    case (i0 ~ l) ~ b ~ (i1 ~ r) => pair(i0 ++ i1, EBOp(b, l, r))
  } | "(" ~> expr <~ ")"
  lazy val bop: Parser[BOp] = (
    "×" ^^^ OMul |
    "/" ^^^ ODiv |
    "+" ^^^ OPlus |
    ("-" | "minus") ^^^ OSub |
    "modulo" ^^^ OMod |
    "&" ^^^ OBAnd |
    "^" ^^^ OBXOr |
    "|" ^^^ OBOr
  )

  // ReturnIfAbrupt
  lazy val returnIfAbruptExpr: P[I[Expr]] =
    (opt("the result of" ~ opt("performing")) ~>
      "?" ~> expr | "ReturnIfAbrupt(" ~> expr <~ ")") ^^ {
        case i ~ e => returnIfAbrupt(i, e, true)
      } | opt("the result of" ~ opt("performing")) ~>
      "!" ~> expr ^^ {
        case i ~ e => returnIfAbrupt(i, e, false)
      }

  // value expressions
  lazy val valueExpr: P[Expr] = (
    "2" ~> sup ^^ {
      case s =>
        val k = s.toInt
        if (k < 0 | k > 62) throw UnexpectedShift(k)
        EINum(1L << k)
    } |
    opt("the numeric value") ~ "zero" ^^^ { ENum(0.0) } |
    opt("the value" | ("the" ~ opt("single - element") ~ "string")) ~> (value | code) ^^ {
      case "null" => ENull
      case "true" => EBool(true)
      case "false" => EBool(false)
      case "NaN" => ENum(Double.NaN)
      case "+0" => EINum(0L)
      case "-0" => ENum(-0.0)
      case "+∞" => ENum(Double.PositiveInfinity)
      case "-∞" => ENum(Double.NegativeInfinity)
      case "undefined" => EUndef
      case s if s.startsWith("\"") && s.endsWith("\"") => EStr(s.slice(1, s.length - 1))
      case err if err.endsWith("Error") => parseExpr(s"""(new OrdinaryObject(
      "Prototype" -> INTRINSIC_${err}Prototype,
      "ErrorData" -> "${err}",
      "SubMap" -> (new SubMap())
    ))""")
      case s if Try(s.toDouble).isSuccess => ENum(s.toDouble)
      case s => ENotYetImpl(s)
    } | const ^^ {
      case "[empty]" => parseExpr("absent")
      case const => parseExpr("CONST_" + const.replaceAll("-", ""))
    } | (number <~ ".") ~ number ^^ {
      case x ~ y => ENum(s"$x.$y".toDouble)
    } | "a newly created" ~> value <~ "object" ^^ {
      case err if err.endsWith("Error") => parseExpr(s"""(new OrdinaryObject(
        "Prototype" -> INTRINSIC_${err}Prototype,
        "ErrorData" -> undefined,
        "SubMap" -> (new SubMap())
      ))""")
    } | opt("the numeric value") ~> number ^^ {
      case s => EINum(java.lang.Long.decode(s))
    } | internalName
  )

  // completion expressions
  lazy val completionExpr: P[I[Expr]] = "normalcompletion(" ~> expr <~ ")" ^^ {
    case i ~ e => pair(i, EMap(Ty("Completion"), List(
      EStr("Type") -> parseExpr("CONST_normal"),
      EStr("Value") -> e,
      EStr("Target") -> parseExpr("CONST_empty")
    )))
  }

  // call expressions
  lazy val callExpr: P[I[Expr]] = (
    "completion(" ~> expr <~ ")" ^^ {
      case i ~ e => pair(i, e)
    } | ("min(" ~> expr <~ ",") ~ (expr <~ ")") ^^ {
      case (i0 ~ l) ~ (i1 ~ r) =>
        val x = getTemp
        val a = beautify(l)
        val b = beautify(r)
        pair(i0 ++ i1 :+ parseInst(s"""{
          if (< $a $b) $x = $a
          else $x = $b
        }"""), parseExpr(x))
    } | ref ~ ("(" ~> repsep(expr, ",") <~ ")") ^^ {
      case (i0 ~ RefId(Id(x))) ~ list =>
        val temp = getTempId
        val i = (i0 /: list) { case (is, i ~ _) => is ++ i }
        val e = IApp(temp, parseExpr(x), list.map { case i ~ e => e })
        pair(i :+ e, toERef(temp))
      case (i0 ~ (r @ RefProp(b, _))) ~ list =>
        val temp = getTempId
        val i = (i0 /: list) { case (is, i ~ _) => is ++ i }
        val e = IApp(temp, ERef(r), ERef(b) :: list.map { case i ~ e => e })
        pair(i :+ e, toERef(temp))
    } | ("the result of the comparison" ~> expr <~ "==") ~ expr ^^ {
      case (i0 ~ x) ~ (i1 ~ y) =>
        val temp = getTemp
        pair(
          i0 ++ i1 :+ parseInst(s"app $temp = (AbstractEqualityComparison ${beautify(x)} ${beautify(y)})"),
          toERef(temp)
        )
    } | (opt("the result of" ~ opt("performing")) ~>
      (name <~ ("for" | "of")) ~
      (refWithOrdinal <~ ("using" | "with" | "passing") ~ opt("arguments" | "argument")) ~
      repsep(expr <~ opt("as the optional" ~ name ~ "argument"), ", and" | "," | "and") <~
      opt("as" ~ opt("the") ~ ("arguments" | "argument"))) ^^ {
        case f ~ x ~ list =>
          val temp = getTempId
          val temp2 = getTempId
          val i = (List[Inst]() /: list) { case (is, i ~ _) => is ++ i }
          val r = IAccess(temp, ERef(x), EStr(f))
          val e = IApp(temp2, toERef(temp), list.map { case i ~ e => e })
          pair(i ++ List(r, e), toERef(temp2))
      }
  )

  // new expressions
  lazy val newExpr: P[I[Expr]] =
    "a new empty list" ^^^ {
      pair(Nil, EList(Nil))
    } | "a" ~> opt("new") ~> " list containing" ~> expr ^^ {
      case i ~ e => pair(i, EList(List(e)))
    } | ("a new" ~> ty <~ "containing") ~ (expr <~ "as the binding object") ^^ {
      case t ~ (i ~ e) => pair(i, EMap(t, List(
        EStr("SubMap") -> EMap(Ty("SubMap"), Nil),
        EStr("BindingObject") -> e
      )))
    } | "a new realm record" ^^^ {
      pair(Nil, toERef("REALM"))
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
      } | opt("the") ~> ty ~ ("{" ~> repsep((expr <~ ":") ~ expr, ",") <~ "}") ^^ {
        case t ~ list =>
          val i = (List[Inst]() /: list) { case (is, _ ~ (i ~ e)) => is ++ i }
          pair(i, EMap(t, list.map { case (_ ~ x) ~ (_ ~ e) => (x, e) }))
      }

  // list expressions
  lazy val listExpr: P[I[Expr]] =
    "«" ~> repsep(expr, ",") <~ "»" ^^ {
      case list =>
        val i = (List[Inst]() /: list) { case (is, (i ~ _)) => is ++ i }
        pair(i, EList(list.map { case _ ~ e => e }))
    } | "a List whose first element is" ~> name <~ "and whose subsequent elements are, in left to right order, the arguments that were passed to this function invocation" ^^ {
      case x => pair(List(parseInst(s"prepend $x -> argumentsList")), parseExpr("argumentsList"))
    } | ("the List of" ~> name <~ "items in") ~ (refWithOrdinal <~ ", in source text order") ^^ {
      case x ~ r => pair(Nil, parseExpr(s"(get-elems ${beautify(r)} $x)"))
    } | (
      ("a new list containing the same values as the list" ~> name <~ "in the same order followed by the same values as the list") ~ (name <~ "in the same order") |
      ("a copy of" ~> name <~ "with all the elements of") ~ (name <~ "appended")
    ) ^^ {
        case x ~ y =>
          val elem = getTemp
          val newList = getTemp
          pair(List(
            parseInst(s"let $newList = (copy-obj $x)"),
            forEachList(Id(elem), parseExpr(y), parseInst(s"append $elem -> $newList"))
          ), parseExpr(newList))
      } | ("a" ~ ("copy" | "new list") ~ "of" ~ opt("the List") ~> name) ~ opt("with" ~> expr <~ "appended") ^^ {
        case x ~ None => pair(Nil, ECopy(toERef(x)))
        case x ~ Some(i ~ y) =>
          val e = beautify(y)
          val newList = getTemp
          pair(i :+ parseInst(s"""{
            let $newList = (copy-obj $x)
            append $e -> $newList
          }"""), parseExpr(newList))
      } | ("a List whose first element is" ~> name <~ ", whose second elements is") ~ name ~ (", and whose subsequent elements are the elements of" ~> name <~ rest) ^^ {
        case x ~ y ~ z =>
          val newList = getTemp
          pair(List(parseInst(s"""{
            let $newList = (copy-obj $z)
            append $y -> $newList
            append $x -> $newList
          }""")), parseExpr(newList))
      } | (
        ("a List containing the elements, in order, of" ~> name <~ "followed by") ~ name |
        ("a List containing" ~> name <~ "followed by the elements , in order , of") ~ name
      ) ^^ {
          case x ~ y => pair(Nil, parseExpr(s"(new [$x, $y])"))
        } | "a List containing" ~ ("only" | ("the" ~ ("one" | "single") ~ "element" ~ opt("," | "which is"))) ~> name ^^ {
          case x => pair(Nil, parseExpr(s"(new [$x])"))
        } | "a new (possibly empty) List consisting of all of the argument values provided after" ~ name ~ "in order" ^^^ {
          pair(List(parseInst(s"(pop argumentsList 0i)")), parseExpr("argumentsList"))
        } | "a List whose elements are , in left to right order , the arguments that were passed to this function invocation" ^^^ {
          pair(Nil, parseExpr("argumentsList"))
        } | "a List whose elements are, in left to right order, the portion of the actual argument list starting with the third argument" ~ rest ^^^ {
          pair(List(parseInst(s"""{
            (pop argumentsList 0i)
            (pop argumentsList 0i)
          }""")), parseExpr("argumentsList"))
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

  // contains expressions
  lazy val containsExpr =
    (opt("the result of") ~> word <~ literal("contains").filter(_ == List("Contains"))) ~ name ^^ {
      case x ~ y => {
        val temp = getTemp
        val temp2 = getTemp
        pair(List(parseInst(s"""{
          access $temp = ($x "Contains")
          app $temp2 = ($temp $y)
        }""")), toERef(temp2))
      }
    } | (id <~ literal("contains").filter(_ == List("Contains"))) ~ name ^^ { // TODO: define? Contains for ScriptBody0 (appears at PerformEval)
      case x ~ y => if (y == "ScriptBody")
        pair(Nil, parseExpr("true"))
      else
        pair(Nil, parseExpr("false"))
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
  lazy val etcExpr: P[I[Expr]] =
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
    } | ((
      "the algorithm steps specified in" ~> secno ~> "for the" ~> name <~ "function" ^^ {
        case x => toERef(x)
      } | "a new unique Symbol value whose [[Description]] value is" ~> name ^^ {
        case x => parseExpr(s"(new '$x)")
      } | "the algorithm steps defined in ListIterator" ~ rest ^^^ {
        parseExpr("ListIteratornext")
      } | "the algorithm steps defined in GetCapabilitiesExecutor Functions" ^^^ {
        toERef("GLOBALDOTGetCapabilitiesExecutorFunctions")
      } | "the algorithm steps defined in Promise Resolve Functions" ^^^ {
        toERef("GLOBALDOTPromiseResolveFunctions")
      } | "the algorithm steps defined in Promise Reject Functions" ^^^ {
        toERef("GLOBALDOTPromiseRejectFunctions")
      } | "the algorithm steps defined in Await Fulfilled Functions" ^^^ {
        toERef("GLOBALDOTAwaitFulfilledFunctions")
      } | "the algorithm steps defined in Await Rejected Functions" ^^^ {
        toERef("GLOBALDOTAwaitRejectedFunctions")
      } | "CoveredCallExpression of CoverCallExpressionAndAsyncArrowHead" ^^^ {
        parseExpr("""(parse-syntax CoverCallExpressionAndAsyncArrowHead "CallMemberExpression")""")
      } | "the steps of an" ~> name <~ "function as specified below" ^^ {
        case x => parseExpr(s"$x")
      } | "the result of parsing the source text" ~> code <~ rest ^^ {
        case s => parseExpr(s"""(parse-syntax "$s" "MethodDefinition" false false)""")
      } | opt("the String value whose code units are the elements of") ~> "the TV of" ~> name <~ opt("as defined in 11.8.6") ^^ {
        case x => EParseString(toERef(x), x match {
          case "NoSubstitutionTemplate" => PTVNoSubs
          case "TemplateHead" => PTVHead
          case "TemplateMiddle" => PTVMiddle
          case "TemplateTail" => PTVTail
        })
      } | "the TRV of" ~> name <~ opt("as defined in 11.8.6") ^^ {
        case x => EParseString(toERef(x), x match {
          case "NoSubstitutionTemplate" => PTRVNoSubs
          case "TemplateHead" => PTRVHead
          case "TemplateMiddle" => PTRVMiddle
          case "TemplateTail" => PTRVTail
        })
      } | (("the Number value represented by" ~> name) | ("the result of forming the value of the" ~> name)) <~ rest ^^ {
        case x => EParseString(toERef(x), PNum)
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
      } | "the token" ~> code ^^ {
        case x => EStr(x)
      } | "the empty string" ^^ {
        case x => EStr("")
      } | ("the stringvalue of stringliteral" | "the string value whose code units are the sv of stringliteral") ^^^ {
        parseExpr(s"(parse-string StringLiteral string)")
      } | opt("the") ~ value.filter(x => x == "this") ~ "value" ^^^ {
        parseExpr("this")
      } | "an instance of the production formalparameters0" ^^^ {
        parseExpr(s"""(parse-syntax "" "FormalParameters" false false)""")
      }
    ) ^^ { case e => pair(Nil, e) })

  lazy val accessExpr: P[I[Expr]] = opt("the") ~> "stringvalue of identifiername" ^^^ {
    pair(Nil, toERef("IdentifierName"))
  } | "EvaluateBody of" ~> ref ^^ {
    case i ~ r =>
      val temp = getTemp
      pair(i :+ IAccess(Id(temp), ERef(r), EStr("EvaluateBody")), toERef(temp))
  } | ("the result of evaluating" ~> name <~ "of") ~ name ^^ {
    case x ~ y =>
      val temp = getTemp
      val temp2 = getTemp
      pair(List(IAccess(Id(temp), toERef(y), EStr(x)), IAccess(Id(temp2), toERef(temp), EStr("Evaluation"))), toERef(temp2))
  } | "the result of evaluating" ~> refWithOrdinal ^^ {
    case x =>
      val temp = getTemp
      pair(List(IAccess(Id(temp), ERef(x), EStr("Evaluation"))), toERef(temp))
  } | ("the result of" ~ opt("performing") ~> name <~ "of") ~ name ^^ {
    case x ~ y =>
      val temp = getTemp
      pair(List(IAccess(Id(temp), toERef(y), EStr(x))), toERef(temp))
  } | "IsFunctionDefinition of" ~> id ^^ {
    case x =>
      val temp = getTemp
      pair(List(IAccess(Id(temp), toERef(x), EStr("IsFunctionDefinition"))), toERef(temp))
  } | "the sole element of" ~> expr ^^ {
    case i ~ e =>
      val temp = getTemp
      pair(i :+ IAccess(Id(temp), e, EINum(0)), toERef(temp))
  } | (opt("the") ~> name.filter(x => x.charAt(0).isUpper) <~ "of") ~ refWithOrdinal ^^ {
    case x ~ y =>
      val temp = getTemp
      pair(List(IAccess(Id(temp), ERef(y), EStr(x))), toERef(temp))
  }

  // reference expressions
  lazy val refExpr: P[I[Expr]] = ref ^^ {
    case i ~ r => pair(i, ERef(r))
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Conditions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val cond: P[I[Expr]] = (
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
      } | (name <~ "is") ~ (valueExpr <~ "and") ~ (name <~ "is") ~ valueExpr ^^ {
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
      } | (ref <~ "is" ~ ("not present" | "absent") <~ ", or is either") ~ (valueExpr <~ "or") ~ valueExpr ~ subCond ^^ {
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
      } | (ref <~ "is absent or has the value") ~ valueExpr ^^ {
        case (i ~ x) ~ v =>
          val l = beautify(x)
          val r = beautify(v)
          pair(i, parseExpr(s"(|| (= $l absent) (= $l $r))"))
      } | (ref <~ "has the value") ~ valueExpr ^^ {
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
        case x ~ y => pair(Nil, parseExpr(s"(= absent $x.SubMap.$y)"))
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
      } | (expr <~ "is") ~ (valueExpr <~ ",") ~ (valueExpr <~ ",") ~ (valueExpr <~ ",") ~ (valueExpr <~ ",") ~ ("or" ~> valueExpr) ^^ {
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
      } | ("either" ~> cond) ~ ("or" ~> cond) ^^ {
        case (i0 ~ c1) ~ (i1 ~ c2) => pair(i0 ++ i1, EBOp(OOr, c1, c2))
      } | name ~ ("is either" ~> expr) ~ ("or" ~> expr) ^^ {
        case x ~ (i0 ~ e1) ~ (i1 ~ e2) =>
          val e0 = parseExpr(x)
          pair(i0 ++ i1, EBOp(OOr, EBOp(OEq, e0, e1), EBOp(OEq, e0, e2)))
      } | expr <~ "is Boolean, String, Symbol, or Number" ^^ {
        case i ~ e => pair(i, EBOp(OOr, EBOp(OEq, e, EStr("Boolean")), EBOp(OOr, EBOp(OEq, e, EStr("String")), EBOp(OOr, EBOp(OEq, e, EStr("Symbol")), EBOp(OEq, e, EStr("Number")))))) // TODO : remove side effect
      } | (expr <~ "equals") ~ expr ^^ {
        case (i0 ~ x) ~ (i1 ~ y) => pair(i0 ++ i1, EBOp(OEq, x, y))
      } | (expr <~ "is") ~ rep1sep(valueExpr, ",") ~ (", or" ~> valueExpr) ^^ {
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

  lazy val nonTrivialTyName: P[String] = ("string" | "boolean" | "number" | "object" | "symbol") ^^ { ts => ts(0) }

  lazy val subCond: P[Expr => I[Expr]] =
    opt(",") ~> "or" ~> opt("if") ~> cond ^^ {
      case i ~ r =>
        val temp = getTempId
        (l: Expr) => pair(List(ILet(temp, l), IIf(ERef(RefId(temp)), emptyInst, ISeq(i :+ IAssign(RefId(temp), EBOp(OOr, ERef(RefId(temp)), r))))), ERef(RefId(temp)))
    } | opt(",") ~> "and" ~> opt("if") ~> cond ^^ {
      case i ~ r =>
        val temp = getTempId
        (l: Expr) => pair(List(ILet(temp, l), IIf(ERef(RefId(temp)), ISeq(i :+ IAssign(RefId(temp), EBOp(OAnd, ERef(RefId(temp)), r))), emptyInst)), ERef(RefId(temp)))
    } | guard(("," | in) ^^^ ((x: Expr) => pair(List[Inst](), x)))

  ////////////////////////////////////////////////////////////////////////////////
  // Types
  ////////////////////////////////////////////////////////////////////////////////
  lazy val ty: Parser[Ty] =
    "realm record" ^^^ Ty("RealmRecord") |
      "record" ^^^ Ty("Record") |
      "built-in function object" ^^^ Ty("BuiltinFunctionObject") |
      "bound function exotic object" ^^^ Ty("BoundFunctionExoticObject") |
      "arguments exotic object" ^^^ Ty("ArgumentsExoticObject") |
      "proxy exotic object" ^^^ Ty("ProxyExoticObject") |
      "string exotic object" ^^^ Ty("StringExoticObject") |
      "propertydescriptor" ^^^ Ty("PropertyDescriptor") |
      "pendingjob" ^^^ Ty("PendingJob") |
      "PromiseCapability" ^^^ Ty("PromiseCapability") |
      "PromiseReaction" ^^^ Ty("PromiseReaction") |
      "AsyncGeneratorRequest" ^^^ Ty("AsyncGeneratorRequest") |
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
  lazy val ref: P[I[Ref]] = (
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
    } | "this" ~ name ^^^ {
      parseRef("this")
    }
  ) ^^ {
      case r => pair(Nil, r)
    } | "the list that is" ~> ref ^^ {
      case p => p
    } | "the string value of" ~> ref ^^ {
      case p => p
    } | ("the first element of" ~> ref) ^^ {
      case i ~ r => pair(i, RefProp(r, EINum(0)))
    } | ("the second element of" ~> ref) ^^ {
      case i ~ r => pair(i, RefProp(r, EINum(1)))
    } | ("the value of") ~> ref ^^ {
      case i ~ r => pair(i, r)
    } | "the outer lexical environment reference of" ~> ref ^^ {
      case i ~ r => pair(i, RefProp(r, EStr("Outer")))
    } | "the parsed code that is" ~> ref ^^ {
      case i ~ r => pair(i, r)
    } | "the EnvironmentRecord component of" ~> ref ^^ {
      case i ~ r => pair(i, RefProp(r, EStr("EnvironmentRecord")))
    } | (name <~ "'s own property whose key is") ~ ref ^^ {
      case r ~ (i ~ p) => pair(i, RefProp(RefProp(RefId(Id(r)), EStr("SubMap")), ERef(p)))
    } | "the second to top element" ~> "of" ~> ref ^^ {
      case i ~ r => pair(i, RefProp(r, EBOp(OSub, ERef(RefProp(r, EStr("length"))), EINum(2))))
    } | (opt("the") ~> (name ^^ { case x => EStr(x) } | internalName) <~ opt("fields" | "component") ~ "of") ~ ref ^^ {
      case x ~ (i ~ y) => pair(i, RefProp(y, x))
    } | (name <~ "'s") ~ ((name ^^ { case x => EStr(x) }) | internalName) <~ opt("value" | "attribute") ^^ {
      case b ~ x => pair(Nil, RefProp(RefId(Id(b)), x))
    } | ("the" ~> id <~ "flag of") ~ ref ^^ {
      case x ~ (i ~ r) if x == "withEnvironment" => pair(i, RefProp(r, EStr(x)))
    } | "the" ~> name <~ "flag" ^^ {
      case x => pair(Nil, RefId(Id(x)))
    } | ordinal ~ nt ^^ {
      case k ~ x => pair(Nil, RefId(Id(x + k)))
    } | name ~ rep(field) ^^ {
      case x ~ es =>
        val i = (List[Inst]() /: es) { case (is, i ~ _) => is ++ i }
        pair(i, (es.map { case i ~ e => e }).foldLeft[Ref](RefId(Id(x))) {
          case (r, e) => RefProp(r, e)
        })
    }
  lazy val refWithOrdinal: P[Ref] =
    ordinal ~ nt ^^ {
      case k ~ x => RefId(Id(x + k))
    } | name ^^ {
      case x => RefId(Id(x))
    }
  lazy val ordinal: P[String] = (
    "the first" ^^^ "0" |
    "the second" ^^^ "1" |
    "the third" ^^^ "2"
  )

  ////////////////////////////////////////////////////////////////////////////////
  // Fields
  ////////////////////////////////////////////////////////////////////////////////
  lazy val field: P[I[Expr]] =
    "." ~> name ^^ {
      case x => pair(Nil, EStr(x))
    } | "." ~> internalName ^^ {
      case x => pair(Nil, x)
    } | "[" ~> expr <~ "]" ^^ {
      case i ~ e => pair(i, e)
    }

  ////////////////////////////////////////////////////////////////////////////////
  // Section Numbers
  ////////////////////////////////////////////////////////////////////////////////
  lazy val secno: P[List[Int]] =
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
    ("this" ~ nt | "this this" | "this") ^^^ "this" |
    "the arguments object" ^^^ "args" |
    opt("the intrinsic object") ~> ("%" ~> word <~ "%" | "[[%" ~> word <~ "%]]") ^^ { case x => s"INTRINSIC_$x" } |
    ("@@" ~> word | "[[@@" ~> word <~ "]]") ^^ { case x => s"SYMBOL_$x" } |
    "forin / ofheadevaluation" ^^^ { "ForInOfHeadEvaluation" } |
    "forin / ofbodyevaluation" ^^^ { "ForInOfBodyEvaluation" } |
    "the" ~> (word | id | nt) |
    nt ~> id |
    nt |
    word |
    id
  )

  lazy val internalName: Parser[Expr] =
    "[[" ~> word <~ "]]" ^^ { case x => EStr(x) }

  ////////////////////////////////////////////////////////////////////////////////
  // Helpers
  ////////////////////////////////////////////////////////////////////////////////
  // get temporal identifiers
  private var idCount: Int = 0
  private val TEMP_PRE: String = "__x"
  private val TEMP_POST: String = "__"
  private def getTemp: String = {
    val i = idCount
    idCount += 1
    s"$TEMP_PRE$i$TEMP_POST"
  }
  private def getTempId: Id = Id(getTemp)

  // several checks
  protected def checkEq(l: Expr, r: Expr): Expr = EBOp(OEq, l, r)
  protected def checkNot(e: Expr): Expr = EUOp(ONot, e)
  protected def checkNEq(l: Expr, r: Expr): Expr = checkNot(checkEq(l, r))
  protected def exists(expr: Expr): Expr = EUOp(ONot, EBOp(OEq, expr, EAbsent))
  protected def exists(ref: Ref): Expr = exists(ERef(ref))

  // for-each instrutions for lists
  protected def forEachList(id: Id, expr: Expr, body: Inst, reversed: Boolean = false): Inst = {
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
  protected def forEachMap(id: Id, expr: Expr, body: Inst, reversed: Boolean = false): Inst = {
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
  protected def handleParams(l: List[String]): (List[Id], Option[Id]) = {
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
  protected def returnIfAbrupt(
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
  protected def normalizeTempIds(inst: Inst): Inst = (new Walker {
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
  protected def flatten(inst: Inst): Inst = inst match {
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
  protected val pair = `~`
  protected def concat(a: List[Inst], b: I[Expr]): I[Expr] = b match {
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
    "," ||| "," ~ s ||| s
  ) ^^^ ""
}
