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
  def getGlobal: (Map[String, AbsValue], Map[Addr, AbsObj]) = {
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

  // type map
  def typeMap: Map[String, TyInfo] =
    typeInfos.map(info => info.name -> info).toMap

  // manual modeling of semantics
  type Meaning = (Int, AbsSemantics, ReturnPoint, AbsState) => AbsState
  private val ignore: Meaning = (_, _, _, st) => st
  val manualSemantics: Map[String, Meaning] = Map(
    "Create an immutable binding in id:{envRec} for id:{N} and record that it is uninitialized . If id:{S} is value:{true} , record that the newly created binding is a strict binding ." -> ignore,
    "Let id:{internalSlotsList} be the internal slots listed in link:{unhandled: table-internal-slots-of-ecmascript-function-objects} ." -> ((asite, _, _, st) => {
      val (addr, s0) = st.allocList(asite, List(AbsStr.Top))
      s0 + ("internalSlotsList" -> addr)
    }),
    "Let id:{ec} be the topmost execution context on the execution context stack whose ScriptOrModule component is not value:{null} ." -> ((_, _, _, st) => {
      st + ("ec" -> AbsValue(Ty("ExecutionContext")))
    }),
    "If no such execution context exists , return value:{null} . Otherwise , return id:{ec} ' s ScriptOrModule ." -> ((_, sem, ret, st) => {
      val v = st(sem, "GLOBAL_executionStack", "length").escaped
      var res = AbsValue.Bot
      (v =^= AbsINum(0)).toSet.foreach {
        case true => res ⊔= AbsNull.Top
        case false => res ⊔= st(sem, "ec", "ScriptOrModule")
      }
      sem.doReturn(ret -> (st.heap, res))
      AbsState.Bot
    }),
  )

  // TODO more manual modelings
  private def typeInfos: List[TyInfo] = List(
    TyInfo(
      name = "ExecutionContext",
      "LexicalEnvironment" -> AbsValue(Ty("EnvironmentRecord")),
      "VariableEnvironment" -> AbsValue(Ty("EnvironmentRecord")),
      "Function" -> AbsValue(Ty("Object")) ⊔ AbsNull.Top,
      "Realm" -> AbsValue(Ty("Realm")),
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
    "REALM" -> AbsValue(Ty("Realm")),
    "Object" -> AbsValue("Object"),
    "String" -> AbsValue("String"),
    "Symbol" -> AbsValue("Symbol"),
  )
  // TODO more manual modelings
  private def manualMaps: Map[String, (Option[String], Map[String, AbsValue])] = Map(
    "Global" -> (Some("OrdinaryObject"), Map()),
    "%AsyncFunction.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("Function.prototype"))
    )),
  )
  // TODO more manual modelings
  private def manualLists: Map[String, AbsValue] = Map(
    "ExecutionStack" -> AbsValue(Ty("ExecutionContext")),
  )

  private def getClos(pattern: Regex): AbsValue = AbsValue(for {
    func <- cfg.funcs.toSet
    if pattern.matches(func.algo.head.printName)
  } yield (Clo(func.uid, Env()): Value))
  private def getConsts(names: String*): AbsValue =
    AbsValue(names.toSet.map[Value](Const(_)))
}
