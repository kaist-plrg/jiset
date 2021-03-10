package kr.ac.kaist.jiset

import scala.Console.RED
import kr.ac.kaist.jiset.CHECK_ALARM
import kr.ac.kaist.jiset.util.Useful._

package object analyzer {
  def alarm(msg: String): Unit = {
    Console.err.println(setColor(RED)(s"[Bug] $msg"))
    if (CHECK_ALARM) scala.io.StdIn.readLine() match {
      case "d" | "debug" => error("stop for debugging")
      case _ => println("PASS")
    }
  }
}
