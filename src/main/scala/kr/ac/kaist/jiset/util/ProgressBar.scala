package kr.ac.kaist.jiset.util

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import kr.ac.kaist.jiset.LINE_SEP

case class ProgressBar[T](msg: String, seq: Iterable[T]) {
  val size = seq.size
  val MAX = 40
  val term = 1000 // 1 second
  def foreach(f: T => Unit): Unit = {
    var count = 0
    var prev = 0
    val start = System.currentTimeMillis
    def show: Unit = Future {
      val percent = count.toDouble / size * 100
      val len = count * MAX / size
      val progress = (BAR * len) + (" " * (MAX - len))
      val msg = f"[$progress] $percent%2.2f%% ($count/$size)"
      print("\b" * prev + msg)
      prev = msg.length
      if (size != count) { Thread.sleep(term); show }
      else {
        val end = System.currentTimeMillis
        val interval = end - start
        println(f" ($interval%,d ms)")
      }
    }
    println(msg + "...")
    show
    seq.foreach(t => { f(t); count += 1 })
  }

  // progress bar character
  val BAR = "░"
}
