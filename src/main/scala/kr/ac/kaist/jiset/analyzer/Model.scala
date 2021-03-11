package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm.{ Algo, Head, SyntaxDirectedHead }
import kr.ac.kaist.jiset.util.Useful._
import scala.Console._
import scala.util.matching.Regex

// TODO more manual modelings
class Model(cfg: CFG) {
  // global variables and heaps
  lazy val global: (Map[String, AbsValue], Map[Addr, AbsObj]) = getGlobal

  // type map
  lazy val typeMap: Map[String, TyInfo] =
    typeInfos.map(info => info.name -> info).toMap

  // manual modeling of semantics
  type Meaning = (Int, AbsSemantics, ReturnPoint, AbsState) => AbsState
  lazy val manualSemantics: Map[String, Meaning] = meanings

  //////////////////////////////////////////////////////////////////////////////
  // Private Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // get global variables and heaps
  private def getGlobal = {
    val (env, heaps) = cfg.getGlobal
    val globalEnv = manualEnv ++ (for ((x, v) <- env) yield x -> AbsValue(v))
    var globalHeap = (for ((a, o) <- heaps) yield a -> AbsObj(o)).toMap

    // map structures
    for ((x, (p, m)) <- manualMaps) {
      val map: Map[String, AbsObj.MapD.AbsVOpt] = m.map {
        case (k, v) => k -> AbsObj.MapD.AbsVOpt(v, AbsAbsent.Bot)
      }
      val pair = NamedAddr(x) -> AbsObj.MapElem(p, AbsObj.MapD(map, AbsObj.MapD.AbsVOpt(None)))
      globalHeap += pair
    }

    // list structures
    for ((x, v) <- manualLists) {
      val pair = (NamedAddr(x) -> AbsObj.ListElem(AbsObj.ListD.ListElem(v)))
      globalHeap += pair
    }

    (globalEnv, globalHeap)
  }

  // meaning of not yet compiled lines
  private val ignore: Meaning = (_, _, _, st) => st
  private def meanings: Map[String, Meaning] = Map(
    "Create an immutable binding in id:{envRec} for id:{N} and record that it is uninitialized . If id:{S} is value:{true} , record that the newly created binding is a strict binding ." -> ignore,
    "Let id:{internalSlotsList} be the internal slots listed in link:{unhandled: table-internal-slots-of-ecmascript-function-objects} ." -> ((asite, _, _, st) => {
      val (addr, s0) = st.allocList(asite, List(AbsStr.Top))
      s0 + ("internalSlotsList" -> addr)
    }),
    "Let id:{ec} be the topmost execution context on the execution context stack whose ScriptOrModule component is not value:{null} ." -> ((_, _, _, st) => {
      st + ("ec" -> AbsValue(Ty("ExecutionContext")))
    }),
    "If no such execution context exists , return value:{null} . Otherwise , return id:{ec} ' s ScriptOrModule ." -> ((_, sem, ret, st) => {
      val v = st.lookup(sem, "GLOBAL_executionStack", "length").escaped
      var res = AbsValue.Bot
      (v =^= AbsINum(0)).toSet.foreach {
        case true => res ⊔= AbsNull.Top
        case false => res ⊔= st.lookup(sem, "ec", "ScriptOrModule")
      }
      sem.doReturn(ret -> (st.heap, res))
      AbsState.Bot
    }),
    "Let id:{idTextUnescaped} be the result of replacing any occurrences of code:{\\\\} nt:{UnicodeEscapeSequence} in id:{idText} with the code point represented by the nt:{UnicodeEscapeSequence} ." -> ((_, _, _, st) => {
      st + ("idTextUnescaped" -> AbsStr.Top)
    }),
  )

  // TODO more manual modelings
  private def typeInfos: List[TyInfo] = List(
    TyInfo(
      name = "ScriptRecord",
      "Realm" -> AbsValue(Ty("RealmRecord")) ⊔ AbsUndef.Top,
      "Environment" -> AbsValue(Ty("EnvironmentRecord")) ⊔ AbsUndef.Top,
      "ECMAScriptCode" -> AbsValue(ASTVal("AnyNode")),
      "HostDefined" -> emptyConst,
    ),
    TyInfo(
      name = "ModuleRecord",
      "Status" -> (AbsConst("unlinked", "linking", "linked", "evaluating", "evaluated"): AbsValue),
      "EvaluationError" -> AbsValue(Undef) ⊔ AbsComp.Top.abrupt,
      "DFSIndex" -> AbsValue(Undef) ⊔ AbsINum.Top,
      "DFSAncestorIndex" -> AbsValue(Undef) ⊔ AbsINum.Top,
      "RequestedModules" -> AbsValue(NamedAddr("StringList")),
    ),
    TyInfo(
      name = "ExecutionContext",
      "LexicalEnvironment" -> AbsValue(Ty("EnvironmentRecord")),
      "VariableEnvironment" -> AbsValue(Ty("EnvironmentRecord")),
      "Function" -> AbsValue(Ty("Object")) ⊔ AbsNull.Top,
      "Realm" -> AbsValue(Ty("RealmRecord")),
      "ScriptOrModule" -> AbsTy(Ty("ScriptRecord"), Ty("ModuleRecord")),
    ),
    TyInfo(
      name = "EnvironmentRecord",
      "HasThisBinding" -> getClos(""".*\.HasThisBinding""".r),
      "GetThisBinding" -> getClos(""".*\.GetThisBinding""".r),
      "ThisBindingStatus" -> getConsts("lexical", "initialized", "uninitialized"),
      "OuterEnv" -> AbsValue(Ty("EnvironmentRecord")) ⊔ AbsValue(Null),
      "GlobalThisValue" -> AbsValue(NamedAddr("Global")),
      "ThisValue" -> ESValue,
    ),
    TyInfo(
      name = "DeclarativeEnvironmentRecord",
      "CreateImmutableBinding" -> getClos("""DeclarativeEnvironmentRecord.CreateImmutableBinding""".r),
    ),
    TyInfo(
      name = "Object",
      "SubMap" -> AbsValue(Ty("SubMap")),
    ),
    TyInfo(
      name = "OrdinaryObject",
      parent = "Object",
      "GetPrototypeOf" -> getClos("""OrdinaryObject.GetPrototypeOf""".r),
      "SetPrototypeOf" -> getClos("""OrdinaryObject.SetPrototypeOf""".r),
      "IsExtensible" -> getClos("""OrdinaryObject.IsExtensible""".r),
      "PreventExtensions" -> getClos("""OrdinaryObject.PreventExtensions""".r),
      "GetOwnProperty" -> getClos("""OrdinaryObject.GetOwnProperty""".r),
      "DefineOwnProperty" -> getClos("""OrdinaryObject.DefineOwnProperty""".r),
      "HasProperty" -> getClos("""OrdinaryObject.HasProperty""".r),
      "Get" -> getClos("""OrdinaryObject.Get""".r),
      "Set" -> getClos("""OrdinaryObject.Set""".r),
      "Delete" -> getClos("""OrdinaryObject.Delete""".r),
      "OwnPropertyKeys" -> getClos("""OrdinaryObject.OwnPropertyKeys""".r),
      "Extensible" -> AbsBool.Top,
      "InitialName" -> AbsValue(Null, Absent) ⊔ AbsStr.Top
    ),
    TyInfo(
      name = "PropertyDescriptor",
      "Value" -> ESValue ⊔ AbsAbsent.Top,
      "Writable" -> AbsValue(true, false, Absent),
      "Get" -> AbsValue(Ty("Object")),
      "Set" -> AbsValue(Ty("Object")),
      "Enumerable" -> AbsBool.Top,
      "Configurable" -> AbsBool.Top,
    ),
  )

  // TODO more manual modelings
  private def manualEnv: Map[String, AbsValue] = Map(
    "GLOBAL_context" -> AbsValue(Ty("ExecutionContext")),
    "GLOBAL_executionStack" -> AbsValue(NamedAddr("ExecutionStack")),
    "REALM" -> AbsValue(Ty("RealmRecord")),
    "Object" -> AbsValue("Object"),
    "String" -> AbsValue("String"),
    "Symbol" -> AbsValue("Symbol"),
    "Undefined" -> AbsValue("Undefined"),
    "Null" -> AbsValue("Null"),
    "Boolean" -> AbsValue("Boolean"),
    "Number" -> AbsValue("Number"),
    "BigInt" -> AbsValue("BigInt"),
    "PRIMITIVE" -> AbsValue(NamedAddr("PRIMITIVE")),
  )

  // TODO more manual modelings
  private def manualMaps: Map[String, (Option[String], Map[String, AbsValue])] = Map(
    "Global" -> (Some("OrdinaryObject"), Map()),
    // primitive object for numeric values
    "PRIMITIVE" -> (None, Map(
      "Number" -> AbsValue(NamedAddr("PRIMITIVE.Number")),
      "BigInt" -> AbsValue(NamedAddr("PRIMITIVE.BigInt")),
    )),
    "PRIMITIVE.Number" -> (None, Map(
      "unit" -> AbsValue(Num(1)),
      "unaryMinus" -> getClo("Number::unaryMinus"),
      "bitwiseNOT" -> getClo("Number::bitwiseNOT"),
      "exponentiate" -> getClo("Number::exponentiate"),
      "multiply" -> getClo("Number::multiply"), // XXX
      "divide" -> getClo("Number::divide"), // XXX
      "remainder" -> getClo("Number::remainder"), // XXX
      "add" -> getClo("Number::add"), // XXX
      "subtract" -> getClo("Number::subtract"),
      "leftShift" -> getClo("Number::leftShift"),
      "signedRightShift" -> getClo("Number::signedRightShift"),
      "unsignedRightShift" -> getClo("Number::unsignedRightShift"),
      "lessThan" -> getClo("Number::lessThan"),
      "equal" -> getClo("Number::equal"),
      "sameValue" -> getClo("Number::sameValue"),
      "sameValueZero" -> getClo("Number::sameValueZero"),
      "bitwiseAND" -> getClo("Number::bitwiseAND"),
      "bitwiseXOR" -> getClo("Number::bitwiseXOR"),
      "bitwiseOR" -> getClo("Number::bitwiseOR"),
      "toString" -> getClo("Number::toString"),
    )),
    "PRIMITIVE.BigInt" -> (None, Map(
      "unit" -> AbsValue(BigInt(1)),
      "unaryMinus" -> getClo("BigInt::unaryMinus"),
      "bitwiseNOT" -> getClo("BigInt::bitwiseNOT"), // XXX
      "exponentiate" -> getClo("BigInt::exponentiate"),
      "multiply" -> getClo("BigInt::multiply"), // XXX
      "divide" -> getClo("BigInt::divide"),
      "remainder" -> getClo("BigInt::remainder"),
      "add" -> getClo("BigInt::add"), // XXX
      "subtract" -> getClo("BigInt::subtract"), // XXX
      "leftShift" -> getClo("BigInt::leftShift"),
      "signedRightShift" -> getClo("BigInt::signedRightShift"),
      "unsignedRightShift" -> getClo("BigInt::unsignedRightShift"),
      "lessThan" -> getClo("BigInt::lessThan"), // XXX
      "equal" -> getClo("BigInt::equal"), // XXX
      "sameValue" -> getClo("BigInt::sameValue"),
      "sameValueZero" -> getClo("BigInt::sameValueZero"),
      "bitwiseAND" -> getClo("BigInt::bitwiseAND"),
      "bitwiseXOR" -> getClo("BigInt::bitwiseXOR"),
      "bitwiseOR" -> getClo("BigInt::bitwiseOR"),
      "toString" -> getClo("BigInt::toString"),
    )),
    // built-in objects
    "%AsyncFunction%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("Function")),
      "Call" -> getClo("AsyncFunction"),
      "Construct" -> getClo("AsyncFunction"),
    )),
    "%AsyncFunction.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%"))
    )),
    "%Function%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("Function"),
      "Construct" -> getClo("Function"),
    )),
    "%Function.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
      "Call" -> AbsClo.Bot // TODO accepts any arguments and returns undefined when invoked. (See 19.2.3 Properties of the Function Prototype Object)
    )),
    "%AsyncGeneratorFunction%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function%")),
      "Call" -> getClo("AsyncGeneratorFunction"),
      "Construct" -> getClo("AsyncGeneratorFunction"),
    )),
    "%AsyncGeneratorFunction.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
    )),
    "%Object%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("Object"),
      "Construct" -> getClo("Object"),
    )),
    "%Object.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsNull.Top,
      "Extensible" -> AT,
    )),
    "%Promise%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("Promise"),
      "Construct" -> getClo("Promise"),
    )),
    "%Promise.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%Array%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("Array"),
      "Construct" -> getClo("Array"),
    )),
    "%Array.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%AggregateError%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Error%")),
      "Call" -> getClo("AggregateError"),
      "Construct" -> getClo("AggregateError"),
    )),
    "%AggregateError.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Error.prototype%")),
    )),
    "%ArrayBuffer%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("ArrayBuffer"),
      "Construct" -> getClo("ArrayBuffer"),
    )),
    "%ArrayBuffer.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%ArrayIteratorPrototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%IteratorPrototype%")),
    )),
    "%AsyncFromSyncIteratorPrototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%AsyncIteratorPrototype%")),
    )),
    "%AsyncIteratorPrototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%Atomics%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%BigInt%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("BigInt"),
      "Construct" -> getClo("BigInt"),
    )),
    "%BigInt.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%Boolean%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("Boolean"),
      "Construct" -> getClo("Boolean"),
    )),
    "%Boolean.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
      "BooleanData" -> AF,
    )),
    "%DataView%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("Function.prototype")),
      "Call" -> getClo("DataView"),
      "Construct" -> getClo("DataView"),
    )),
    "%DataView.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
  )

  // TODO more manual modelings
  private def manualLists: Map[String, AbsValue] = Map(
    "ExecutionStack" -> AbsValue(Ty("ExecutionContext")),
    "StringList" -> AbsStr.Top,
  )

  private def getClos(pattern: Regex): AbsValue = AbsValue(for {
    func <- cfg.funcs.toSet
    if pattern.matches(func.name)
  } yield (Clo(func.uid, Env()): Value))
  private val cloMap: Map[String, AbsValue] =
    (for (func <- cfg.funcs) yield func.name -> AbsValue(Clo(func.uid))).toMap
  private def getClo(name: String): AbsValue = cloMap.getOrElse(name, {
    alarm(s"unknown function name: $name")
    AbsValue.Bot
  })
  private def getConsts(names: String*): AbsValue =
    AbsValue(names.toSet.map[Value](Const(_)))
}
