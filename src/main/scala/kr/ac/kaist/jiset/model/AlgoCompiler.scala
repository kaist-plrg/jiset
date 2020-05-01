package kr.ac.kaist.jiset.model

import kr.ac.kaist.ires.ir.Parser._
import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.error.UnexpectedShift
import kr.ac.kaist.jiset.parser.TokenParsers
import kr.ac.kaist.jiset.util.Useful._
import scala.util.{ Try, Success, Failure }
import kr.ac.kaist.jiset.algorithm
import algorithm.{ AlgoKind, Algorithm, Token, Text, StaticSemantics }
import algorithm.{ Method, Grammar, AlgoCompilers, GeneralAlgoCompilerHelper }

case class AlgoCompiler(algoName: String, algo: Algorithm) extends AlgoCompilerHelper
trait AlgoCompilerHelper extends GeneralAlgoCompilerHelper {
  // etc statements
  override lazy val etcStmt: P[Inst] = "change its bound value to" ~> id ^^ {
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
          $retcont = (pop $retcont 0i)
          $x.ResumeCont = ($y) [=>] return $y
          }""")
      }
    } | ("Resume the suspended evaluation of" ~> id <~ "using") ~ (expr <~ "as the result of the operation that suspended it . Let") ~ (id <~ "be the" <~ ("value" | "completion record") <~ "returned by the resumed computation .") ^^ {
      case cid ~ (i ~ e) ~ rid => {
        val tempId = getTemp
        val tempId2 = getTemp
        ISeq(i :+ parseInst(s"""withcont $tempId ($rid) = {
            if (= $cid.ReturnCont absent) {
              $cid.ReturnCont = (new [])
            } else {}
            append $tempId -> $cid.ReturnCont
            app $tempId2 = ($cid.ResumeCont ${beautify(e)})
            }"""))
      }
    } | ("Resume the suspended evaluation of" ~> id <~ "using") ~ (expr <~ "as the result of the operation that suspended it .") ^^ {
      case cid ~ (i ~ e) => {
        val tempId = getTemp
        val tempId2 = getTemp
        ISeq(i :+ parseInst(s"""withcont $tempId () = {
            if (= $cid.ReturnCont absent) {
              $cid.ReturnCont = (new [])
            } else {}
            append $tempId -> $cid.ReturnCont
            app $tempId2 = ($cid.ResumeCont ${beautify(e)})
            }"""))
      }
    } | ("Resume the suspended evaluation of" ~> id <~ ". Let") ~ (id <~ "be the value returned by the resumed computation .") ^^ {
      case cid ~ rid => {
        val tempId = getTemp
        val tempId2 = getTemp
        parseInst(s"""withcont $tempId ($rid) = {
            if (= $cid.ReturnCont absent) {
              $cid.ReturnCont = (new [])
            } else {}
            append $tempId -> $cid.ReturnCont
            app $tempId2 = ($cid.ResumeCont)
            }""")
      }
    } | ("Assert : If we return here , the" ~ opt("async") ~ " generator either threw") <~ rest ^^^ {
      parseInst(s"""{
        access $retcont = (genContext "ReturnCont")
        $retcont = (pop $retcont 0i)
       }""")
    } | "Assert : If we return here , the async function either threw an exception or performed an implicit or explicit return ; all awaiting is done" ^^^ {
      parseInst(s"""{
        access $retcont = (asyncContext "ReturnCont")
        $retcont = (pop $retcont 0i)
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
        prototype = INTRINSIC_FunctionPrototype
        let $x = (new BuiltinFunctionObject("SubMap" -> (new SubMap())))
        delete $x.Construct
        $x.Code = $y.step
        if (= $y.name "")
          {}
        else
          $x.SubMap.name = (new DataProperty("Value" -> $y.name, "Writable" -> false, "Enumerable" -> false, "Configurable" -> true))
        $x.SubMap.length = (new DataProperty("Value" -> $y.length, "Writable" -> false, "Enumerable" -> false, "Configurable" -> true))
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
        if (= $executionStack[(- $executionStack.length 1i)] $x) {
          $idx = (- $executionStack.length 1i)
          (pop $executionStack $idx)
          if (= $executionStack.length 0i) {
            $context = null
          } else {
            $context = $executionStack[(- $executionStack.length 1i)]
          }
        } else {
          $context = null
        }
      }""")
      }
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
    } | "let" ~> name <~ "be a newly created ecmascript function object with the internal slots listed in table 27. All of those internal slots are initialized to" <~ value <~ "." ^^ {
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
    } | "let " ~ id ~ "," ~ id ~ ", and" ~ id ~ "be integers such that" ~ id ~ "≥ 1" ~ rest ^^^ {
      parseInst(s"""return (convert m num2str)""")
    } | "for each property of the global object" ~ rest ^^^ {
      val temp = getTemp
      forEachMap(Id("name"), parseExpr("GLOBAL"), parseInst(s"""{
        let desc = GLOBAL[name]
        app $temp = (DefinePropertyOrThrow global name desc)
        if (is-completion $temp) {
          if (= $temp.Type CONST_normal) $temp = $temp.Value
          else return $temp
        } else {}
      }"""))
    } | "for each own property key" ~> id ~> "of" ~> id <~ "that is an array index" <~ rest ^^^ {
      val temp1 = getTemp
      val temp2 = getTemp
      val temp3 = getTemp
      val temp4 = getTemp
      val temp5 = getTemp
      val temp6 = getTemp
      val temp7 = getTemp
      val temp8 = getTemp
      val temp9 = getTemp
      val tempId = getTemp
      ISeq(List(
        parseInst(s"""{
          let $temp1 = (new [])
          let $temp2 = (new [])
          let $temp3 = (new [])
          let $temp4 = (map-keys O["SubMap"])
        }"""),
        forEachList(Id(tempId), toERef(temp4),
          parseInst(s"""
            if (= (typeof $tempId) "Symbol")
              append $tempId -> $temp1
            else {
              app $temp5 = (CanonicalNumericIndexString $tempId)
              if (|| (= $temp5 undefined) (|| (< $temp5 0) (< 4294967295 $temp5)))
                append $tempId -> $temp2
              else
                append $temp5 -> $temp3
            }""")),
        parseInst(s"""
          while (< 0i $temp3["length"]) {
            let $temp6 = 0i
            let $temp7 = 0i
            while (< $temp7 $temp3["length"]) {
              if (< $temp3[$temp7] $temp3[$temp6])
                $temp6 = $temp7
              else {}
              $temp7 = (+ $temp7 1i)
            }
            let $temp8 = (pop $temp3 $temp6)
            app $temp9 = (ToString $temp8)
            append $temp9 -> keys
          }
          """),
        forEachList(Id(tempId), toERef(temp2),
          IAppend(toERef(Id(tempId)), toERef("keys"))),
        forEachList(Id(tempId), toERef(temp1),
          IAppend(toERef(Id(tempId)), toERef("keys"))),
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
    } | ("Remove the binding for" ~> id <~ "from") ~ id ^^ {
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
    } | ("Return the result of appending to" ~> id <~ "the elements of the") ~ (name <~ "of the second CaseClauses .") ^^ {
      case x ~ s => {
        val temp = getTemp
        val temp2 = getTemp
        val temp3 = getTemp
        parseInst(s"""{
          access $temp = (CaseClauses1 "$s")
          let $temp2 = 0i
          while (< $temp2 $temp["length"]) {
            let $temp3 = $temp[$temp2]
            append $temp3 -> $x
            $temp2 = (+ $temp2 1i)
          }
          return $x
        }""")
      }
    } | "If" ~ id ~ "contains a code unit that is not a radix -" ~ id ~ "digit" <~ rest ^^^ {
      IExpr(ENotSupported("StringOp"))
    } | "if there exists" ~ ("any" | "an") ~ "integer" ~ rest ^^^ {
      IExpr(ENotSupported("NumberOp"))
    } | (
      "For each own property key" ~ id ~ "of" ~ id ~ "that is a String but is not an array index , in ascending chronological order of property creation , do " ~ rest |
      "For each own property key" ~ id ~ "of" ~ id ~ "that is a Symbol , in ascending chronological order of property creation , do " ~ rest |
      "If - 6 <" ~ id ~ "≤ 0 , return the string - concatenation of : " ~ rest |
      "If 0 <" ~ id ~ "≤ 21 , return the string - concatenation of : " ~ rest |
      "If" ~ id ~ "≤" ~ id ~ "≤ 21 , return the string - concatenation of : " ~ rest |
      "Otherwise , if" ~ id ~ "= 1 , return the string - concatenation of : " ~ rest |
      "Return the string - concatenation of :" ~ stepList |
      "Let" ~ id ~ "be an implementation - defined Completion value ." |
      "Perform an implementation - defined debugging action ." |
      "suspend the currently running execution context" |
      "suspend" ~> name
    ) ^^^ {
        ISeq(List())
      } | (
        "Append all the entries of" ~ id ~ "to the end of" ~ id ~ "." |
        "Append in order the code unit elements of" ~ id ~ "to the end of" ~ id ~ "." |
        "Append the Record { [ [ Key ] ] :" ~ id ~ ", [ [ Symbol ] ] :" ~ id ~ "} to the GlobalSymbolRegistry List ." |
        "Create an immutable indirect binding in" ~ id ~ "for" ~ id ~ "that references" ~ id ~ "and" ~ id ~ "as its target binding and record that the binding is initialized ." |
        "Create own properties of" ~ id ~ "corresponding to the definitions in 26 . 3 ." |
        "For each Record { [ [ Key ] ] , [ [ Value ] ] }" ~ id ~ "that is an element of" ~ id ~ ", in original key insertion order , do " ~ rest |
        "For each element" ~ id ~ "of the GlobalSymbolRegistry List ( see 19 . 4 . 2 . 2 ) , do " ~ rest |
        "For each element" ~ id ~ "of the GlobalSymbolRegistry List , do " ~ rest |
        "For each element" ~ id ~ "of" ~ id ~ ", in ascending index order , do " ~ rest |
        "For each integer" ~ id ~ "starting with 0 such that" ~ id ~ "<" ~ id ~ ", in ascending order , do " ~ rest |
        "For each own property key" ~ id ~ "of" ~ id ~ "such that Type (" ~ id ~ ") is String and" ~ id ~ "is not an array index , in ascending chronological order of property creation , do " ~ rest |
        "For each own property key" ~ id ~ "of" ~ id ~ "such that Type (" ~ id ~ ") is String and" ~ id ~ "is not an integer index , in ascending chronological order of property creation , do " ~ rest |
        "For each own property key" ~ id ~ "of" ~ id ~ "such that Type (" ~ id ~ ") is Symbol , in ascending chronological order of property creation , do " ~ rest |
        "For each own property key" ~ id ~ "of" ~ id ~ "such that" ~ id ~ "is an array index and ToInteger (" ~ id ~ ") ≥" ~ id ~ ", in ascending numeric index order , do " ~ rest |
        "If BoundNames of" ~ id ~ "contains any duplicate elements , throw a" ~ value ~ "exception ." |
        "If IsCallable (" ~ id ~ ") is" ~ value ~ ", set" ~ id ~ "to the intrinsic function % ObjProto_toString % ." |
        "If IsDataDescriptor (" ~ id ~ ") is" ~ value ~ "and" ~ id ~ "has attribute values { [ [ Writable ] ] :" ~ value ~ ", [ [ Enumerable ] ] :" ~ value ~ "} , return" ~ value ~ "." |
        "If any element of the BoundNames of" ~ id ~ "also occurs in the LexicallyDeclaredNames of" ~ id ~ ", throw a" ~ value ~ "exception ." |
        "If any static semantics errors are detected for" ~ id ~ "or" ~ id ~ ", throw a" ~ value ~ "or a" ~ value ~ "exception ," ~ rest |
        "If neither" ~ id ~ "nor any prefix of" ~ id ~ "satisfies the syntax of a StrDecimalLiteral ( see 7 . 1 . 3 . 1 ) , return" ~ value ~ "." |
        "If only one argument was passed , return" ~ id ~ "." |
        "If the binding for" ~ id ~ "is an indirect binding , then " ~ rest |
        "If the first code unit of" ~ id ~ "is the code unit 0x002D ( HYPHEN - MINUS ) , return" ~ value ~ "." |
        "If the code unit at index" ~ id ~ "within" ~ id ~ "is not the code unit 0x0025 ( PERCENT SIGN ) , throw a" ~ value ~ "exception ." |
        "If the code units at index (" ~ id ~ "+ 1 ) and (" ~ id ~ "+ 2 ) within" ~ id ~ "do not represent hexadecimal digits , throw a" ~ value ~ "exception ." |
        "If the code units at index (" ~ id ~ "+ 1 ) and (" ~ id ~ "+ 2 ) within" ~ id ~ "do not represent hexadecimal digits , throw a" ~ value ~ "exception ." |
        "If the length of" ~ id ~ "is at least 2 and the first two code units of" ~ id ~ "are either" ~ code ~ "or" ~ code ~ ", then " ~ rest |
        "If the sequence of code units of" ~ id ~ "starting at" ~ id ~ "of length" ~ id ~ "is the same as the full code unit sequence of" ~ id ~ ", return" ~ value ~ "." |
        "If the sequence of code units of" ~ id ~ "starting at" ~ id ~ "of length" ~ id ~ "is the same as the full code unit sequence of" ~ id ~ ", return" ~ value ~ "." |
        "If the two most significant bits in" ~ id ~ "are not 10 , throw a" ~ value ~ "exception ." |
        "If" ~ id ~ "+ ( 3 × (" ~ id ~ "- 1 ) ) is greater than or equal to" ~ id ~ ", throw a" ~ value ~ "exception ." |
        "If" ~ id ~ "+ 2 is greater than or equal to" ~ id ~ ", throw a" ~ value ~ "exception ." |
        "If" ~ id ~ ". [ [ Site ] ] is the same Parse Node as" ~ id ~ ", then " ~ rest |
        "If" ~ id ~ "= 0 ℝ , then " ~ rest |
        "If" ~ id ~ "can be the string - concatenation of" ~ id ~ "and some other String" ~ id ~ ", return" ~ value ~ ". Otherwise , return" ~ value ~ "." |
        "If" ~ id ~ "contains any duplicate entries , throw a" ~ value ~ "exception ." |
        "If" ~ id ~ "does not contain a valid UTF - 8 encoding of a Unicode code point , throw a" ~ value ~ "exception ." |
        "If" ~ id ~ "does not have all of the internal slots of a Map Iterator Instance ( 23 . 1 . 5 . 3 ) , throw a" ~ value ~ "exception ." |
        "If" ~ id ~ "does not have all of the internal slots of a Set Iterator Instance ( 23 . 2 . 5 . 3 ) , throw a" ~ value ~ "exception ." |
        "If" ~ id ~ "is a Proxy exotic object and" ~ id ~ ". [ [ ProxyHandler ] ] is" ~ value ~ ", throw a" ~ value ~ "exception ." |
        "If" ~ id ~ "is a trailing surrogate or" ~ id ~ "+ 1 =" ~ id ~ ", then " ~ rest |
        "If" ~ id ~ "is a trailing surrogate ," ~ rest |
        "If" ~ id ~ "is not a leading surrogate or trailing surrogate , then " ~ rest |
        "If" ~ id ~ "is not a trailing surrogate ," ~ rest |
        "If" ~ id ~ "is not empty and the first code unit of" ~ id ~ "is the code unit 0x002B ( PLUS SIGN ) or the code unit 0x002D ( HYPHEN - MINUS ) , remove the first code unit from" ~ id ~ "." |
        "If" ~ id ~ "is not empty and the first code unit of" ~ id ~ "is the code unit 0x002D ( HYPHEN - MINUS ) , set" ~ id ~ "to - 1 ." |
        "If" ~ id ~ "is not greater than" ~ id ~ ", return" ~ id ~ "." |
        "If" ~ id ~ "is odd , return" ~ id ~ "+ 1 ." |
        "Let" ~ id ~ "," ~ id ~ ", and" ~ id ~ "be integers such that" ~ id ~ "≥ 0 ," ~ rest |
        "Let" ~ id ~ "and" ~ id ~ "be integers such that 10" ~ rest |
        "Let" ~ id ~ "be a substring of" ~ id ~ "consisting of the leftmost code unit that is not a" ~ nt ~ "and all code units to the right of that code unit . ( In other words , remove leading white space . )" ~ rest |
        "Let" ~ id ~ "be a newly created substring of" ~ id ~ "consisting of the first code unit that is not a " ~ nt ~ "and all code units following that code unit . ( In other words , remove leading white space . )" ~ rest |
        "Let" ~ id ~ "be a List consisting of all of the arguments passed to this function , starting with the second argument . If fewer than two arguments were passed , the List is empty ." |
        "Let" ~ id ~ "be a List containing in order the code points as defined in 6 . 1 . 4 of" ~ id ~ ", starting at the first element of" ~ id ~ "." |
        "Let" ~ id ~ "be a List containing the arguments passed to this function ." |
        "Let" ~ id ~ "be a List containing the arguments passed to this function ." |
        "Let" ~ id ~ "be a List of 8 - bit integers of size" ~ id ~ "." |
        "Let" ~ id ~ "be a List of length 1 that contains a nondeterministically chosen byte value ." |
        "Let" ~ id ~ "be a List where the elements are the result of toLowercase (" ~ id ~ ") , according to the Unicode Default Case Conversion algorithm ." |
        "Let" ~ id ~ "be a String containing one instance of each code unit valid in uriReserved and uriUnescaped plus" ~ code ~ "." |
        "Let" ~ id ~ "be a String containing one instance of each code unit valid in uriReserved plus" ~ code ~ "." |
        "Let" ~ id ~ "be a String containing one instance of each code unit valid in uriUnescaped ." |
        "Let" ~ id ~ "be a new Data Block value consisting of" ~ id ~ "bytes . If it is impossible to create such a Data Block , throw a" ~ value ~ "exception ." |
        "Let" ~ id ~ "be a new List containing the same values as the list" ~ id ~ "where the values are ordered as if an Array of the same values had been sorted using" ~ code ~ "using" ~ value ~ "as" ~ id ~ "." |
        "Let" ~ id ~ "be a new List which is a copy of" ~ id ~ "." |
        "Let" ~ id ~ "be a new Shared Data Block value consisting of" ~ id ~ "bytes . If it is impossible to create such a Shared Data Block , throw a" ~ value ~ "exception ." |
        "Let" ~ id ~ "be a new module Environment Record containing no bindings ." |
        "Let" ~ id ~ "be a newly created Integer - Indexed exotic object with an internal slot for each name in" ~ id ~ "." |
        "Let" ~ id ~ "be a newly created module namespace exotic object with the internal slots listed in Table 29 ." |
        "Let" ~ id ~ "be an integer for which the exact mathematical value" ~ rest |
        "Let" ~ id ~ "be an integer for which ℝ (" ~ id ~ ") ÷ 10 ℝ" ~ rest |
        "Let" ~ id ~ "be equivalent to a function that returns" ~ id ~ "." |
        "Let" ~ id ~ "be equivalent to a function that throws" ~ id ~ "." |
        "Let" ~ id ~ "be the 8 - bit value represented by the two hexadecimal digits at index (" ~ id ~ "+ 1 ) and (" ~ id ~ "+ 2 ) ." |
        "Let" ~ id ~ "be the Agent Record of the surrounding agent ." |
        "Let" ~ id ~ "be the Number value of the Element Size value specified in Table 59 for" ~ id ~ "." |
        "Let" ~ id ~ "be the List of argument values starting with the second argument ." |
        "Let" ~ id ~ "be the List of octets resulting by applying the UTF - 8 transformation to" ~ id ~ "." |
        "Let" ~ id ~ "be the Number value for" ~ id ~ "." |
        "Let" ~ id ~ "be the Record { [ [ Key ] ] , [ [ Value ] ] } that is the value of" ~ id ~ "[" ~ id ~ "] ." |
        "Let" ~ id ~ "be the String value derived from" ~ id ~ "by copying code unit elements from" ~ id ~ "to" ~ id ~ "while performing replacements as specified in Table 51 . These" ~ code ~ "replacements are done left - to - right , and , once such a replacement is performed , the new replacement text is not subject to further replacements ." |
        "Let" ~ id ~ "be the String value equal to the substring of" ~ id ~ "consisting of the code units at indices" ~ id ~ "( inclusive ) through" ~ id ~ "( exclusive ) ." |
        "Let" ~ id ~ "be the String value for the list - separator String appropriate for the host environment ' s current locale ( this is derived in an implementation - defined way ) ." |
        "Let" ~ id ~ "be the String value of the Element Type value in Table 59 for" ~ id ~ "." |
        "Let" ~ id ~ "be the String value" ~ id ~ "[" ~ id ~ "] ." |
        "Let" ~ id ~ "be the [ [ CandidateExecution ] ] field of the surrounding agent ' s Agent Record ." |
        "Let" ~ id ~ "be the [ [ EventList ] ] field of the element in" ~ id ~ ". [ [ EventsRecords ] ] whose [ [ AgentSignifier ] ] is AgentSignifier ( ) ." |
        "Let" ~ id ~ "be the code point whose numeric value is that of" ~ id ~ "." |
        "Let" ~ id ~ "be the code unit at index" ~ id ~ "+ 1 within" ~ id ~ "." |
        "Let" ~ id ~ "be the code unit at index" ~ id ~ "within" ~ id ~ "." |
        "Let" ~ id ~ "be the code unit whose value is" ~ id ~ "." |
        "Let" ~ id ~ "be the first code unit of" ~ id ~ ", and let" ~ id ~ "be the remaining" ~ id ~ "- 1 code units of" ~ id ~ "." |
        "Let" ~ id ~ "be the first code unit of" ~ id ~ ", and let" ~ id ~ "be the remaining" ~ id ~ "code units of" ~ id ~ "." |
        "Let" ~ id ~ "be the first" ~ id ~ "-" ~ id ~ "code units of" ~ id ~ ", and let" ~ id ~ "be the remaining" ~ id ~ "code units of" ~ id ~ "." |
        "Let" ~ id ~ "be the longest prefix of" ~ id ~ ", which might be" ~ id ~ "itself , that satisfies the syntax of a StrDecimalLiteral ." |
        "Let" ~ id ~ "be the module Environment Record for which the method was invoked ." |
        "Let" ~ id ~ "be the number of bytes in" ~ id ~ "." |
        "Let" ~ id ~ "be the number of elements in the List" ~ id ~ "." |
        "Let" ~ id ~ "be the prefix associated with" ~ id ~ "in Table 47 ." |
        "Let" ~ id ~ "be the result of parsing" ~ id ~ ", interpreted as UTF - 16 encoded Unicode text as described in 6 . 1 . 4 , using" ~ id ~ "as the goal symbol . Throw a" ~ value ~ "exception if the parse fails ." |
        "Let" ~ id ~ "be the smallest nonnegative integer such that (" ~ id ~ "< <" ~ id ~ ") & 0x80 is equal to 0 ." |
        "Let" ~ id ~ "be the string - concatenation of the first" ~ id ~ "code units of" ~ id ~ "," ~ id ~ ", and the trailing substring of" ~ id ~ "starting at index" ~ id ~ ". If" ~ id ~ "is 0 , the first element of the concatenation will be the empty String ." |
        "Let" ~ id ~ "be the string - concatenation of :" ~ stepList |
        "Let" ~ id ~ "be the substring of" ~ id ~ "from index" ~ id ~ "to index" ~ id ~ "inclusive ." |
        "Let" ~ id ~ "be the value obtained by applying the UTF - 8 transformation to" ~ id ~ ", that is , from a List of octets into a 21 - bit value ." |
        "Let" ~ id ~ "be the code point with the same numeric value as code unit" ~ id ~ "." |
        "Increment" ~ id ~ "and" ~ id ~ "each by 1 ." |
        "No action is required ." |
        "Otherwise , return" ~ value ~ "." |
        "Remove" ~ id ~ "from" ~ id ~ "." |
        "Replace the element of" ~ id ~ "whose value is" ~ id ~ "with an element whose value is" ~ const ~ "." |
        "Return a value of Number type , whose value is the numeric value of the code unit at index" ~ id ~ "within the String" ~ id ~ "." |
        "Return the Number value for" ~ id ~ "." |
        "Return the code point" ~ id ~ "." |
        "Return the code unit sequence consisting of" ~ id ~ "followed by" ~ id ~ "." |
        "Search" ~ id ~ "for the first occurrence of" ~ id ~ "and let" ~ id ~ "be the index within" ~ id ~ "of the first code unit of the matched substring and let" ~ id ~ "be" ~ id ~ ". If no occurrences of" ~ id ~ "were found , return" ~ id ~ "." |
        "Set all of the bytes of" ~ id ~ "to 0 ." |
        "Set" ~ id ~ "to the string - concatenation of :" ~ stepList |
        "Set" ~ id ~ "to the string - concatenation of the code unit 0x0030 ( DIGIT ZERO ) , the code unit 0x002E ( FULL STOP ) , - (" ~ id ~ "+ 1 ) occurrences of the code unit 0x0030 ( DIGIT ZERO ) , and the String" ~ id ~ "." |
        "Set" ~ id ~ "to the string - concatenation of the first" ~ id ~ "+ 1 code units of" ~ id ~ ", the code unit 0x002E ( FULL STOP ) , and the remaining" ~ id ~ "- (" ~ id ~ "+ 1 ) code units of" ~ id ~ "."
      ) ^^^ IExpr(ENotSupported("Etc")))

  // etc expressions
  override lazy val etcExpr: P[I[Expr]] = (
    (id <~ "' s intrinsic object named") ~ id ^^ {
      case _ ~ y => pair(List(), ERef(RefId(Id(y))))
    } | ("the number whose value is MV of" ~> nt) ^^ {
      case x =>
        {
          val temp = getTemp
          pair(List(parseInst(s"""access $temp = ($x "MV")""")), toERef(temp))
        }
    } | ("the String value of length 1 , containing one code unit from" ~> id) ~ (", " ~> ("namely" | "specifically") ~> "the code unit at index" ~> id) ^^ {
      case x ~ y => {
        pair(Nil, ERef(RefProp(RefId(Id(x)), ERef(RefId(Id(y))))))
      }
    } | ("the result of performing the abstract operation named by" ~> expr) ~ ("using the elements of" ~> expr <~ "as its arguments .") ^^ {
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
    } | "the" ~> ("source text" | "code") ~ ("matched by" | "matching") ~> refBase ^^ {
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
    } | "a String according to Table 35" ^^^ {
      val temp = getTemp
      pair(List(parseInst(s"app $temp = (GetTypeOf val)")), toERef(temp))
    } | "the parenthesizedexpression that is covered by coverparenthesizedexpressionandarrowparameterlist" ^^^ {
      pair(Nil, EParseSyntax(toERef("this"), EStr("ParenthesizedExpression"), Nil))
    } | "the" ~ opt("actual") ~ "number of" ~ ("actual arguments" | "arguments passed to this function" ~ opt("call")) ^^^ {
      pair(Nil, parseExpr(s"""argumentsList.length"""))
    } | "the List of arguments passed to this function" ^^^ {
      pair(Nil, parseExpr("argumentsList"))
    } | ("the numeric value of the code unit at index" ~> expr <~ "within") ~ (opt("the string") ~> name) ^^^ {
      pair(Nil, ENotSupported("StringOp")) // parseExpr(s"(convert $y[${beautify(x)}] str2cp)"))
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
      val tempP = getTemp
      val tempP2 = getTemp
      pair(List(parseInst(s"""{
        let $tempP = (parse-syntax x "Script")
        if (= $tempP absent) {
          app $tempP2 = (ThrowCompletion ${beautify(getErrorObj("SyntaxError"))})
          return $tempP2
        } else {}
      }""")), toERef(tempP))
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
          names.foldLeft(Map[String, List[Inst] ~ Expr]()) {
            case (m, name) =>
              val temp = getTempId
              m + (name -> returnIfAbrupt(List(IApp(temp, toERef(name), List(x))), toERef(temp)))
          }
        val names = List("ToNumber", "ToInt32", "ToUint32", "ToPrimitive")
        val lmap = getMap(toERef(l), names)
        val rmap = getMap(toERef(r), names)
        val init: Inst = IAssert(EStr("invalid assign operator"))
        val genenralCase = list.foldLeft(init) {
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
    } | "-" ~> expr ^^ {
      case i ~ e => pair(i, EUOp(ONeg, e))
    } | ("the result of performing abstract relational comparison" ~> name <~ "<") ~ name ~ opt("with" ~ name ~ "equal to" ~> expr) ^^ {
      case x ~ y ~ Some(i ~ e) =>
        val temp = getTemp
        pair(i :+ parseInst(s"app $temp = (AbstractRelationalComparison $x $y ${beautify(e)})"), toERef(temp))
      case x ~ y ~ None =>
        val temp = getTemp
        pair(List(parseInst(s"app $temp = (AbstractRelationalComparison $x $y)")), toERef(temp))
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
    } | "the string value whose code units are the sv of the stringliteral" ^^^ {
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
    } | "the String value consisting of the UTF16Encoding of each code point of" ~> name <~ "of RegularExpressionLiteral" ^^ {
      case x => {
        val temp = getTemp
        pair(List(parseInst(s"""access $temp = (RegularExpressionLiteral "$x")""")), toERef(temp))
      }
    } | "the TRV of" ~> name <~ opt("as defined in 11.8.6") ^^ {
      case x =>
        val temp = getTemp
        pair(List(parseInst(s"""access $temp = ($x "TRV")""")), toERef(temp))
    } | "the FunctionBody , ConciseBody , or AsyncConciseBody that most closely contains" ~> id ^^ {
      case x => {
        val temp = getTemp
        val temp2 = getTemp
        pair(List(parseInst(s"""{
        let $temp = true
        let $temp2 = $x
        while (&& (= $temp true) (! (= $temp2 absent))) {
          if (is-instance-of $temp2 FunctionBody) $temp = false else {}
          if (is-instance-of $temp2 ConciseBody) $temp = false else {}
          if (is-instance-of $temp2 AsyncConciseBody) $temp = false else {}
          if (= $temp true) access $temp2 = ($temp2 "parent") else {}
          }
        }""")), ERef(RefId(Id(temp2))))
      }
    } | ((
      "the steps of an" ~> name <~ "function as specified below" ^^ {
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
      } | opt("the") ~ "string value" ~ ("whose" | "that" | "containing" | "consisting of" | "consisting solely of") ~ rest ^^^ {
        ENotSupported("StringOp")
      } | "the code units of" ~ rep(normal.filter(_ != Text("."))) ^^^ {
        ENotSupported("StringOp")
      } | "the remaining" ~ rest ^^^ {
        ENotSupported("StringOp")
      } | "the" ~ ("largest" | "smallest") ~ "possible" ~ rest ^^^ {
        ENotSupported("NumberOp")
      } | "the mathematical integer value that is represented by" ~ rest ^^^ {
        ENotSupported("NumberOp")
      } | "integers such that" ~ rest ^^^ {
        ENotSupported("NumberOp")
      } | "the algorithm steps" ~ ("specified" | "defined") ~ "in" ~ not(algorithmName) ~ rest ^^^ {
        ENotSupported("Algorithms")
      }
    ) ^^ { case e => pair(Nil, e) })
  )

  // etc conditions
  override lazy val etcCond: P[I[Expr]] = (
    "this this is contained in strict mode code and StringValue of Identifier is" ~ code ~ "or" ~ code ^^^ {
      pair(Nil, parseExpr(s"""(|| (= (get-syntax IdentifierName) "eval") (= (get-syntax IdentifierName) "arguments"))"""))
    } | (id <~ "is the ReservedWord") ~ code ^^ {
      case x ~ y => pair(Nil, parseExpr(s"""(= (get-syntax $x) "$y")"""))
    } | "no arguments were passed to this function invocation" ^^^ {
      pair(Nil, parseExpr(s"(= argumentsList.length 0i)"))
    } | name <~ "is an Identifier and StringValue of" ~ name ~ "is the same value as the StringValue of IdentifierName" ^^ {
      case x => pair(Nil, parseExpr(s"(&& (is-instance-of $x Identifier) (= (get-syntax $x) (get-syntax IdentifierName)))"))
    } | name <~ "does not have all of the internal slots of an Array Iterator Instance (22.1.5.3)" ^^ {
      case x => pair(Nil, parseExpr(s"""
        (|| (= absent $x.IteratedObject)
        (|| (= absent $x.ArrayIteratorNextIndex)
        (= absent $x.ArrayIterationKind)))"""))
    } | name <~ "does not have all of the internal slots of a String Iterator Instance (21.1.5.3)" ^^ {
      case x => pair(Nil, parseExpr(s"""(|| (= $x.IteratedString absent) (= $x.StringIteratorNextIndex absent))"""))
    } | id <~ "is not contained within a FunctionBody , ConciseBody , or AsyncConciseBody" ^^ {
      case x => {
        val temp = getTemp
        val temp2 = getTemp
        pair(List(parseInst(s"""{
        let $temp = true
        let $temp2 = $x
        while (&& (= $temp true) (! (= $temp2 absent))) {
          if (is-instance-of $temp2 FunctionBody) $temp = false else {}
          if (is-instance-of $temp2 ConciseBody) $temp = false else {}
          if (is-instance-of $temp2 AsyncConciseBody) $temp = false else {}
          access $temp2 = ($temp2 "parent")
          }
        }""")), ERef(RefId(Id(temp))))
      }
    } | id ~ ("is the FunctionBody of" ~> ("a" | "an") ~> ("GeneratorBody" | "AsyncFunctionBody" | "AsyncGeneratorBody")) ^^ {
      case x ~ s => {
        val temp = getTemp
        val temp2 = getTemp
        pair(List(parseInst(s"""{
            let $temp2 = false
            if (is-instance-of $x FunctionBody) {
              access $temp = ($x "parent")
              if (&& (! (= $temp absent)) (is-instance-of $temp ${s(0)})) $temp2 = true else {}
            } else {}
          }""")), ERef(RefId(Id(temp2))))
      }
    } | expr <~ "is a normal completion" ^^ {
      case i ~ x => pair(i, parseExpr(s"""(&& (is-completion ${beautify(x)}) (= ${beautify(x)}.Type CONST_normal))"""))
    } | expr <~ "is not already suspended" ^^ {
      case i ~ e => pair(i, EBOp(OEq, e, ENull))
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
    } | (name <~ "does not have a binding for") ~ name ^^ {
      case x ~ y => pair(Nil, parseExpr(s"(= absent $x.SubMap[$y])"))
    } | ("the binding for" ~> name <~ "in") ~ (name <~ "cannot be deleted") ^^^ {
      pair(Nil, EBool(false))
    } | ("the binding for" ~> name <~ "in") ~ (name <~ "is a strict binding") ^^ {
      case x ~ y => pair(Nil, parseExpr(s"(&& (! (= absent $y.SubMap[$x].strict)) $y.SubMap[$x].strict)"))
    } | ("The binding for" ~> name <~ "in") ~ (name <~ "has not yet been initialized") ^^ {
      case x ~ y => pair(Nil, parseExpr(s"(&& (! (= absent $y.SubMap[$x].initialized)) (! $y.SubMap[$x].initialized))"))
    } | ("the binding for" ~> name <~ "in") ~ (name <~ "is a mutable binding") ^^ {
      case x ~ y => pair(Nil, parseExpr(s"""(= (typeof $y.SubMap[$x]) "MutableBinding")"""))
    } | (ref <~ "has a binding for the name that is the value of") ~ expr ^^ {
      case (i0 ~ r) ~ (i1 ~ p) => pair(i0 ++ i1, exists(RefProp(RefProp(r, EStr("SubMap")), p)))
    } | expr <~ "has any duplicate entries" ^^ {
      case i ~ e =>
        val temp = getTempId
        pair(i :+ IApp(temp, parseExpr("IsDuplicate"), List(e)), ERef(RefId(temp)))
    } | "statement is statement10" ^^^ {
      pair(Nil, EIsInstanceOf(toERef("Statement"), "LabelledStatement"))
    } | expr <~ "is an object" ^^ {
      case i ~ e =>
        val temp = getTemp
        pair(i :+ parseInst(s"""app $temp = (Type ${beautify(e)})"""), parseExpr(s"""(= $temp "Object")"""))
    } | ("either" ~> etcCond) ~ ("or" ~> etcCond) ^^ {
      case (i0 ~ c1) ~ (i1 ~ c2) => pair(i0 ++ i1, EBOp(OOr, c1, c2))
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
    } | (expr <~ ("is the same as" | "is the same Number value as" | "is")) ~ expr ~ subCond ^^ {
      case (i0 ~ l) ~ (i1 ~ r) ~ f => concat(i0 ++ i1, f(EBOp(OEq, l, r)))
    } | (id <~ ". [ [ Enumerable ] ] is present and the [ [ Enumerable ] ] fields of") ~ (id <~ "and") ~ (id <~ "are the Boolean negation of each other") ^^ {
      case z ~ x ~ y => pair(Nil, parseExpr(s"""
        (&& (! (= absent $z.Enumerable)) (|| (&& (= true $x.Enumerable) (= false $y.Enumerable)) (&& (= false $x.Enumerable) (= true $y.Enumerable))))"""))
    } | "an implementation - defined debugging facility is available and enabled" ^^^ {
      pair(Nil, EBool(false))
    } | "the most significant bit in" ~ id ~ "is" ~ number ^^^ {
      pair(Nil, ENotSupported("NumberOp"))
    } | id <~ "is not a leading surrogate" ^^^ {
      pair(Nil, ENotSupported("StringOp"))
    } | id <~ "is an integer index ≤" <~ id ^^^ {
      pair(Nil, ENotSupported("NumberOp"))
    } | starCond
  )

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

  override lazy val ignoreCond: P[I[Expr]] = (
    "the order of evaluation needs to be reversed to preserve left to right evaluation" |
    name ~ "is added as a single item rather than spread" |
    name ~ "contains a formal parameter mapping for" ~ name |
    name ~ "is a Reference to an Environment Record binding" |
    "the base of" ~ ref ~ "is an Environment Record" |
    name ~ "must be" ~ rep(not(",") ~ text) |
    id ~ "does not currently have a property" ~ id |
    ("isaccessordescriptor(" ~> id <~ ") and isaccessordescriptor(") ~ (id <~ ") are both") ~ expr
  ) ^^^ pair(Nil, EBool(true))
}
