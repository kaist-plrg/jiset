package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.{ LINE_SEP, LOG, ANALYZE_LOG_DIR }
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Useful._
import scala.Console._
import java.io.PrintWriter

class Stat(sem: AbsSemantics) {
  var iter = 0
  private var counter: Map[ControlPoint, Int] = Map()

  // initalize
  mkdir(ANALYZE_LOG_DIR)
  private val nfSummary = getPrintWriter(s"$ANALYZE_LOG_DIR/summary")
  private val nfAlarms = getPrintWriter(s"$ANALYZE_LOG_DIR/alarms")

  // increase counter
  def inc[T <: ControlPoint](cp: T): T = {
    if (LOG) counter += cp -> (counter.getOrElse(cp, 0) + 1)
    cp
  }

  val worklist = sem.worklist

  logItems(nfSummary, "", "", "", "#Update")
  logItems(nfSummary, "#", "|WL|", "|CP|", "min", "max", "avg.", "median")

  // dump stats
  def dump(): Unit = {
    // dump summary
    // # of iter, worklist size, # of control points, min, max, avg, median
    logItems(nfSummary, iter, worklist.size, sem.size, min, max, f"$avg%.2f", median.toInt)

    // dump worklist
    val wapp = new Appender
    worklist.foreach(wapp >> _.toString >> LINE_SEP)
    dumpFile("worklist", wapp, s"$ANALYZE_LOG_DIR/worklist")

    // dump update
    val uapp = new Appender
    counter.foreach {
      case (cp, cnt) => uapp >> cnt >> "\t" >> cp.toString >> LINE_SEP
    }
    dumpFile("update", uapp, s"$ANALYZE_LOG_DIR/update")

    // dump result
    dumpFile("analysis result", sem.getString(CYAN), s"$ANALYZE_LOG_DIR/result.log")
  }

  // close
  def close(): Unit = nfSummary.close()

  // stats helpers
  private def logItems(nf: PrintWriter, items: Any*): Unit = {
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
