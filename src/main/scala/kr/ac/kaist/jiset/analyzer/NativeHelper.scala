package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.JsonProtocol._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir.JsonProtocol._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.JvmUseful._

object NativeHelper {
  import cfg.jsonProtocol._

  // dump abstract semantics to directory
  def dumpSem(sem: AbsSemantics, dirname: String): Unit = {
    mkdir(dirname)
    val npMaps = sem.npMap.groupBy { case (np, _) => sem.funcOf(np) }
    val rpMaps = sem.rpMap.groupBy { case (rp, _) => sem.funcOf(rp) }
    for (func <- cfg.funcs) {
      val name = func.name
      val subname = s"$dirname/funcs/$name"
      val npMap = npMaps.getOrElse(func, Map())
      val rpMap = rpMaps.getOrElse(func, Map())
      mkdir(subname)
      dumpJson(npMap, s"$subname/npMap.json")
      dumpJson(rpMap, s"$subname/rpMap.json")
    }
    dumpJson(sem.thenBranches, s"$dirname/thenBranches.json")
    dumpJson(sem.elseBranches, s"$dirname/elseBranches.json")
    dumpJson(sem.retEdges, s"$dirname/retEdges.json")
    dumpJson(sem.unknownVars, s"$dirname/unknownVars.json")
    dumpJson(sem.assertions, s"$dirname/assertions.json")
  }

  // load abstract semantics from filename
  def loadSem(dirname: String): AbsSemantics = AbsSemantics(
    npMap = (for {
      file <- walkTree(s"$dirname/funcs")
      if file.getName == "npMap.json"
      pair <- readJson[Map[NodePoint[Node], AbsState]](file.toString)
    } yield pair).toMap,
    rpMap = (for {
      file <- walkTree(s"$dirname/funcs")
      if file.getName == "rpMap.json"
      pair <- readJson[Map[ReturnPoint, AbsType]](file.toString)
    } yield pair).toMap,
    thenBranches = readJson[Set[NodePoint[Branch]]](
      s"$dirname/thenBranches.json"
    ),
    elseBranches = readJson[Set[NodePoint[Branch]]](
      s"$dirname/elseBranches.json"
    ),
    retEdges = readJson[Map[ReturnPoint, Set[(NodePoint[Call], String)]]](
      s"$dirname/retEdges.json"
    ),
    unknownVars = readJson[Set[(ControlPoint, String)]](
      s"$dirname/unknownVars.json"
    ),
    assertions = readJson[Map[ControlPoint, (AbsType, Expr)]](
      s"$dirname/assertions.json"
    ),
  )
}
