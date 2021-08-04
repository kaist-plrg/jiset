package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.{ CHECK_LOG_DIR }
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir.JsonProtocol._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._
import scala.Console.RED

object NativeHelper {
  import cfg.jsonProtocol._

  // dump abstract semantics to directory
  def dumpSem(sem: AbsSemantics, dirname: String): Unit = {
    import jsonProtocol._
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
  def loadSem(dirname: String): AbsSemantics = {
    import jsonProtocol._
    AbsSemantics(
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

  // path in CFG
  val CFG_PATH = s"$CHECK_LOG_DIR/cfg"
  mkdir(CFG_PATH)

  // dump CFG in DOT/PDF format
  def dumpCFG(
    cp: Option[ControlPoint] = None,
    pdf: Boolean = true,
    depth: Option[Int] = None,
    path: Option[Path] = None
  ): Unit = try {
    val dot = (new DotPrinter)(cp, depth, path).toString
    dumpFile(dot, s"$CFG_PATH.dot")
    if (pdf) {
      executeCmd(s"""unflatten -l 10 -o ${CFG_PATH}_trans.dot $CFG_PATH.dot""")
      executeCmd(s"""dot -Tpdf "${CFG_PATH}_trans.dot" -o "$CFG_PATH.pdf"""")
      println(s"Dumped CFG to $CFG_PATH.pdf")
    } else println(s"Dumped CFG to $CFG_PATH.dot")
  } catch {
    case _: Throwable => printlnColor(RED)(s"Cannot dump CFG")
  }

  // dump CFG in DOT/PDF format
  def dumpFunc(
    func: Function,
    pdf: Boolean = true
  ): Unit = try {
    val dot = (new DotPrinter)(func).toString
    dumpFile(dot, s"$CFG_PATH.dot")
    if (pdf) {
      executeCmd(s"""unflatten -l 10 -o ${CFG_PATH}_trans.dot $CFG_PATH.dot""")
      executeCmd(s"""dot -Tpdf "${CFG_PATH}_trans.dot" -o "$CFG_PATH.pdf"""")
      println(s"Dumped CFG to $CFG_PATH.pdf")
    } else println(s"Dumped CFG to $CFG_PATH.dot")
  } catch {
    case _: Throwable => printlnColor(RED)(s"Cannot dump CFG")
  }
}
