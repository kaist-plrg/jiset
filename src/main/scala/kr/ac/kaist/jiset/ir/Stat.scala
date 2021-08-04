package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.checker._
import kr.ac.kaist.jiset.util.JvmUseful._

object Stat {
  // touch counter for algorithms
  private var _algoNames: Map[String, Int] = Map()
  def algoNames: Map[String, Int] = _algoNames
  def touchAlgo(name: String): Unit =
    _algoNames += name -> (_algoNames.getOrElse(name, 0) + 1)
  def algoNamesString: String = (for {
    (name, k) <- algoNames
  } yield s"$k - $name").mkString(LINE_SEP)
  def dumpAlgoNames(dirname: String): Unit =
    dumpFile(algoNamesString, s"$dirname/touched-algos")

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

  // visit map
  val visitRecorder: VisitRecorder = VisitRecorder()

  //dump to a directory
  def dumpVisitMap(dirname: String): Unit =
    dumpFile(visitRecorder.toString, s"$dirname/visited-nodes")

  // summary
  def summaryString: String = (
    f"""- iteration:
       |  - min   : ${iterMap.toList.map(_._2).reduce(_ min _)}
       |  - max   : ${iterMap.toList.map(_._2).reduce(_ max _)}
       |  - total : $iterSum
       |  - avg.  : $iterAvg%.2f
       |- algorithms:
       |  - # algo : ${algoNames.size}
       |  - min    : ${algoNames.toList.map(_._2).reduce(_ min _)}
       |  - max    : ${algoNames.toList.map(_._2).reduce(_ max _)}""".stripMargin
  )

  def dumpSummary(dirname: String): Unit =
    dumpFile(summaryString, s"$dirname/summary")

  // dump to a directory
  def dumpTo(dirname: String): Unit = if (iterSum > 0) {
    mkdir(dirname)
    dumpAlgoNames(dirname)
    dumpIterMap(dirname)
    dumpVisitMap(dirname)
    dumpSummary(dirname)
  }
}
