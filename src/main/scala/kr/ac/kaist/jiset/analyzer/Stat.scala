package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.AbsSemantics._
import kr.ac.kaist.jiset.{ LINE_SEP, LOG, ANALYZE_LOG_DIR }
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Useful._
import scala.Console._
import java.io.PrintWriter

object Stat {
  // iteration
  var iter = 0
  private var counter: Map[ControlPoint, Int] = Map()

  // initalize
  mkdir(ANALYZE_LOG_DIR)
  private val nf = getPrintWriter(s"$ANALYZE_LOG_DIR/summary.tsv")

  // time
  var parseTime = 0L
  var cfgTime = 0L
  var checkerTime = 0L
  var analysisStartTime = 0L
  def analysisTime: Long = System.currentTimeMillis - analysisStartTime

  // checker time
  def doCheck[T](f: => T): T = {
    val (t, res) = time(f)
    checkerTime += t
    res
  }

  // increase counter
  def inc[T <: ControlPoint](cp: T): T = {
    if (LOG) counter += cp -> (counter.getOrElse(cp, 0) + 1)
    cp
  }

  // Abbreviation
  log("#", "The number of iterations")
  log("T", "The duration time (ms)")
  log("WL", "The number of control points in worklist")
  log("CP", "The number of analyzed control points")
  log("AU", "The avarage number of updates for control points")
  log("RP", "The number of analyzed return points")
  log("AF", "The number of analyzed functions")
  log("TF", "The number of total functions")
  log("ER", "The number of detected errors")
  log("WA", "The number of detected warnings")
  log()

  // header
  log("#", "T", "WL", "CP", "AU", "RP", "AF", "TF", "ER", "WA")

  // dump stats
  def dump(): Unit = {
    val (numFunc, numAlgo, numRp) = numOfFuncAlgoRp
    val analTime = analysisTime

    // dump summary
    log(
      f"$iter%,3d", f"$analTime%,3d", worklist.size, AbsSemantics.size,
      f"$avg%.2f", numRp, numFunc, numAlgo, numError, numWarning
    )

    // dump worklist
    val wapp = new Appender
    worklist.foreach(wapp >> _.toString >> LINE_SEP)
    dumpFile(wapp, s"$ANALYZE_LOG_DIR/worklist")

    // dump update
    // val uapp = new Appender
    // counter.foreach {
    //   case (cp, cnt) =>
    //     val func = funcOf(cp)
    //     uapp >> cnt >> "\t" >> s"$cp @ [${func.uid}] ${func.name}" >> LINE_SEP
    // }
    // dumpFile(uapp, s"$ANALYZE_LOG_DIR/update")

    // dump result
    // dumpFile(getString(CYAN), s"$ANALYZE_LOG_DIR/result.log")

    // dump stat for evaluation
    // # iter, parse, cfg, checker, analyze, full, all, node, return, all
    val evalItems = List(
      iter,
      parseTime,
      cfgTime,
      checkerTime,
      analTime,
      AbsSemantics.rpMap.keySet.map(_.func).toSet.filter(_.algo.isComplete).size,
      numFunc,
      npMap.size,
      numRp,
      npMap.size + numRp
    )
    dumpFile(evalItems.map(_.toString).mkString("\t"), s"$ANALYZE_LOG_DIR/stat_summary")
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
