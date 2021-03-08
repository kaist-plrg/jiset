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
    var globalHeap: Map[Addr, AbsObj] = (for ((x, (p, m)) <- manualHeaps) yield {
      val map: Map[String, AbsObj.MapD.AbsVOpt] = m.map {
        case (k, v) => k -> AbsObj.MapD.AbsVOpt(v, AbsAbsent.Bot)
      }
      NamedAddr(x) -> AbsObj.MapElem(p, AbsObj.MapD(map, AbsObj.MapD.AbsVOpt(None)))
    }).toMap
    globalHeap ++= (for ((a, o) <- heaps) yield a -> AbsObj(o))
    (globalEnv, globalHeap)
  }

  // type map
  def typeMap: Map[String, TyInfo] =
    typeInfos.map(info => info.name -> info).toMap

  // TODO more manual modelings
  private def typeInfos: List[TyInfo] = List(
    TyInfo(
      name = "ExecutionContext",
      "LexicalEnvironment" -> AbsValue(Ty("EnvironmentRecord")),
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
    ),
    TyInfo(
      name = "OrdinaryObject",
      parent = "Object"
    ),
  )
  // TODO more manual modelings
  private def manualEnv: Map[String, AbsValue] = Map(
    "GLOBAL_context" -> AbsValue(Ty("ExecutionContext")),
    "Object" -> AbsValue("Object"),
  )
  // TODO more manual modelings
  private def manualHeaps: Map[String, (Option[String], Map[String, AbsValue])] = Map(
    "Global" -> (Some("OrdinaryObject"), Map()),
    "AsyncFunction.prototype" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("Function.prototype"))
    )),
  )
  private val ESValue: AbsValue = {
    val prim = AbsPrim.Top.copy(absent = AbsAbsent.Bot)
    AbsPure(ty = AbsTy("Object"), prim = prim)
  }
  private def getClos(pattern: Regex): AbsValue = AbsValue(for {
    func <- cfg.funcs.toSet
    if pattern.matches(func.algo.head.printName)
  } yield (Clo(func.uid, Env()): Value))
  private def getConsts(names: String*): AbsValue =
    AbsValue(names.toSet.map[Value](Const(_)))

}
