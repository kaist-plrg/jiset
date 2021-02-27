package kr.ac.kaist.jiset

package object analyzer {
  def alarm(msg: String): Unit =
    Console.err.println(s"[Bug] $msg")
}
