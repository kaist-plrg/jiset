package kr.ac.kaist.jiset.util

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import kr.ac.kaist.jiset.LINE_SEP

case class ProgressBar[T](msg: String, seq: Iterable[T]) {
  val size = seq.size
  val MAX = 40
  val term = 1000 // 1 second
  def foreach(f: T => Unit): Unit = {
    var gcount = 0
    var prev = 0
    val start = System.currentTimeMillis
    def show: Future[Unit] = Future {
      val count = gcount
      val percent = count.toDouble / size * 100
      val len = count * MAX / size
      val progress = (BAR * len) + (" " * (MAX - len))
      val msg = f"[$progress] $percent%2.2f%% ($count/$size)"
      print("\b" * prev + msg)
      prev = msg.length
      if (count == size) {
        val end = System.currentTimeMillis
        val interval = end - start
        println(f" ($interval%,d ms)")
      } else { Thread.sleep(term); show }
    }
    println(msg + "...")
    val future = show
    seq.foreach(t => { f(t); gcount += 1 })
    Thread.sleep(term)
  }

  // progress bar character
  val BAR = "░"
}
