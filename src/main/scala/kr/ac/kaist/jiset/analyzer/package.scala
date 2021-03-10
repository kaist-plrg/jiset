package kr.ac.kaist.jiset

import scala.Console.RED
import kr.ac.kaist.jiset.CHECK_ALARM
import kr.ac.kaist.jiset.util.Useful._

package object analyzer {
  val nfAlarms = getPrintWriter(s"$ANALYZE_LOG_DIR/alarms")
  def alarm(msg: String): Unit = {
    val errMsg = setColor(RED)(s"[Bug] $msg")
    Console.err.println(errMsg)
    if (LOG) nfAlarms.println(errMsg)
    if (CHECK_ALARM) scala.io.StdIn.readLine() match {
      case "d" | "debug" => error("stop for debugging")
      case _ => println("PASS")
    }
  }
}
