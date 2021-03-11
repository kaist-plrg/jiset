package kr.ac.kaist.jiset

import scala.Console.RED
import kr.ac.kaist.jiset.CHECK_ALARM
import kr.ac.kaist.jiset.util.Useful._

package object analyzer {
  // initialize
  mkdir(ANALYZE_LOG_DIR)
  val nfAlarms = getPrintWriter(s"$ANALYZE_LOG_DIR/alarms")

  // transfer
  var transfer: AbsTransfer = null

  // alarm
  var alarmCP: ControlPoint = null
  var alarmCPStr: String = ""

  private var alarmMap: Map[ControlPoint, Set[String]] = Map()
  def alarm(msg: String): Unit = if (!TEST_MODE) {
    val set = alarmMap.getOrElse(alarmCP, Set())
    if (!(set contains msg)) {
      alarmMap += alarmCP -> (set + msg)
      val errMsg = s"[Bug] $msg @ $alarmCPStr"
      Console.err.println(setColor(RED)(errMsg))
      if (LOG) nfAlarms.println(errMsg)
      if (CHECK_ALARM) transfer.read(alarmCP)
    }
  }
}
