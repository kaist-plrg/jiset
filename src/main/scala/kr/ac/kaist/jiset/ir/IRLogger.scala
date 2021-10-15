package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.cfg
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.checker
import kr.ac.kaist.jiset.checker._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.Appender._

object IRLogger {
  import js.checkerJsonProtocol._
  val cfgStringifer = CFGElem.getStringifier(true, false, false)

  // iteration counter
  private var _iterSum: Long = 0L
  private var _iterMap: Map[String, Int] = Map()
  def iterAvg: Double = iterSum.toDouble / iterMap.size
  def iterSum: Long = _iterSum
  def iterMap: Map[String, Int] = _iterMap
  def recordIter(fnameOpt: Option[String], iter: Int): Unit = {
    fnameOpt.map(fname => {
      _iterMap += fname -> iter
      _iterSum += iter
    })
  }
  def iterMapString: String = (for {
    (fname, iter) <- iterMap
  } yield s"$fname: $iter").mkString(LINE_SEP)
  def dumpIterMap(dirname: String): Unit =
    dumpFile(iterMapString, s"$dirname/iters")

  // callstack depth
  private var _depthMap: Map[String, Int] = Map()
  def depthSum: Int = _depthMap.toList.map(_._2).sum
  def depthAvg: Double = depthSum.toDouble / _depthMap.size
  def recordCallDepth(fnameOpt: Option[String], depth: Int): Unit = {
    fnameOpt.map(fname => {
      _depthMap += fname -> depth
    })
  }
  def dumpDepthMap(dirname: String): Unit = {
    val depthMapStr = (for {
      (fname, depth) <- _depthMap
    } yield s"$fname: $depth").mkString(LINE_SEP)
    dumpFile(depthMapStr, s"$dirname/call-depths")
  }

  // summary
  def summaryString: String = (
    f"""- iteration:
       |  - min   : ${iterMap.toList.map(_._2).min}
       |  - max   : ${iterMap.toList.map(_._2).max}
       |  - total : $iterSum
       |  - avg.  : $iterAvg%.2f
       |- callstack depth:
       |  - min   : ${_depthMap.toList.map(_._2).min}
       |  - max   : ${_depthMap.toList.map(_._2).max}
       |  - avg.  : $depthAvg%.2f
       """.stripMargin
  )

  val visitDir = s"$VERSION_DIR/visited-nodes"

  def dumpVisitStat(dirname: String): Unit = {
    val vr = VisitRecorder(visitDir)
    mkdir(s"$dirname/visit-recorder")
    vr.dumpCsv(s"$dirname/visit-recorder")
  }

  def dumpSummary(dirname: String): Unit =
    dumpFile(summaryString, s"$dirname/summary")

  // dump to a directory
  def dumpTo(dirname: String): Unit = if (iterSum > 0) {
    mkdir(dirname)
    dumpIterMap(dirname)
    dumpDepthMap(dirname)
    dumpVisitStat(dirname)
    dumpSummary(dirname)
  }
}
