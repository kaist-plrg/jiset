package kr.ac.kaist.jiset

import scala.Console.RED
import kr.ac.kaist.jiset.util.Useful._

package object analyzer {
  def alarm(msg: String): Unit =
    Console.err.println(setColor(RED)(s"[Bug] $msg"))
}
