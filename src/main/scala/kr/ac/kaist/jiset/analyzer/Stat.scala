package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.{ LINE_SEP, LOG, ANALYZE_LOG_DIR }
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Useful._
import scala.Console._
import java.io.PrintWriter

class Stat(sem: AbsSemantics) {
  // iteration
  var iter = 0
  private var counter: Map[ControlPoint, Int] = Map()

  // initalize
  mkdir(ANALYZE_LOG_DIR)
  private val nf = getPrintWriter(s"$ANALYZE_LOG_DIR/summary.tsv")

  // time
  private val startTime = System.currentTimeMillis
  def time: Long = System.currentTimeMillis - startTime

  // increase counter
  def inc[T <: ControlPoint](cp: T): T = {
    if (LOG) counter += cp -> (counter.getOrElse(cp, 0) + 1)
    cp
  }

  // Abbreviation
  log("WL", "The number of control points in worklist")
  log("CP", "The number of analyzed control points")
  log("AU", "The avarage number of updates for control points")
  log("RP", "The number of analyzed return points")
  log("AF", "The number of analyzed functions")
  log("TF", "The number of total functions")
  log()

  // header
  log("#", "time (ms)", "WL", "CP", "AU", "RP", "AF", "TF")

  // dump stats
  def dump(): Unit = {
    val worklist = sem.worklist

    val (numFunc, numAlgo, numRp) = sem.numOfFuncAlgoRp

    // dump summary
    log(f"$iter%,3d", f"$time%,3d", worklist.size, sem.size, f"$avg%.2f", numRp, numFunc, numAlgo)

    // dump worklist
    val wapp = new Appender
    worklist.foreach(wapp >> _.toString >> LINE_SEP)
    dumpFile(wapp, s"$ANALYZE_LOG_DIR/worklist")

    // dump update
    val uapp = new Appender
    counter.foreach {
      case (cp, cnt) =>
        val func = sem.funcOf(cp)
        uapp >> cnt >> "\t" >> s"$cp @ [${func.uid}] ${func.name}" >> LINE_SEP
    }
    dumpFile(uapp, s"$ANALYZE_LOG_DIR/update")

    // dump result
    dumpFile(sem.getString(CYAN), s"$ANALYZE_LOG_DIR/result.log")
  }

  // close
  def close(): Unit = nf.close()

  // stats helpers
  private def log(items: Any*): Unit = {
    nf.println(items.map(_.toString).mkString("\t"))
    nf.flush()
  }
  private def min: Int = if (counter.isEmpty) -1 else counter.values.min
  private def max: Int = if (counter.isEmpty) -1 else counter.values.max
  private def avg: Double =
    if (counter.isEmpty) -1
    else counter.values.sum / counter.size.toDouble
  private def median: Double =
    if (counter.isEmpty) -1
    else {
      val size = counter.size
      val values = counter.values.toList.sorted
      if (size % 2 == 1) values(size / 2)
      else (values(size / 2) + values(size / 2 - 1)) / 2.toDouble
    }
}
