package kr.ac.kaist.jiset.util

import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset._

object Stat {
  // touch counter for algorithms
  private var _algoNames: Map[String, Int] = Map()
  def algoNames: Map[String, Int] = _algoNames
  def touchAlgo(name: String): Unit =
    _algoNames += name -> (_algoNames.getOrElse(name, 0) + 1)

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

  // dump to a directory
  def dumpTo(dirname: String): Unit = if (iterSum > 0) {
    mkdir(dirname)
    dumpFile(algoNames.map {
      case (name, k) => s"$k - $name"
    }.mkString(LINE_SEP), s"$dirname/touched-algos")
    dumpFile(iterMap.map {
      case (fname, iter) => s"$fname: $iter"
    }.mkString(LINE_SEP), s"$dirname/iters")
    dumpFile(
      f"""- iteration:
      |  - min   : ${iterMap.toList.map(_._2).reduce(_ min _)}
      |  - max   : ${iterMap.toList.map(_._2).reduce(_ max _)}
      |  - total : $iterSum
      |  - avg.  : $iterAvg%.2f
      |- algorithms:
      |  - # algo : ${algoNames.size}
      |  - min    : ${algoNames.toList.map(_._2).reduce(_ min _)}
      |  - max    : ${algoNames.toList.map(_._2).reduce(_ max _)}""".stripMargin,
      s"$dirname/summary"
    )
  }
}
