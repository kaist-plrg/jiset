package kr.ac.kaist.jiset.util

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import kr.ac.kaist.jiset.LINE_SEP

// progress bar
case class ProgressBar[T](msg: String, seq: Iterable[T]) {
  // summary
  val summary = new Summary

  // postfix for summary
  def postfix =
    if (summary.total == 0) ""
    else s" - ${summary.simpleString}"

  // size
  val size = seq.size

  // bar length
  val BAR_LEN = 40

  // interval
  val term = 1000 // 1 second

  // foreach function
  def foreach(f: T => Unit): Unit = {
    var gcount = 0
    var prev = 0
    val start = System.currentTimeMillis
    def show: Future[Unit] = Future {
      val count = gcount
      val percent = count.toDouble / size * 100
      val len = count * BAR_LEN / size
      val progress = (BAR * len) + (" " * (BAR_LEN - len))
      val msg = f"[$progress] $percent%2.2f%% ($count%,d/$size%,d)$postfix"
      print("\b" * prev + msg)
      prev = msg.length
      if (count == size) {
        val end = System.currentTimeMillis
        summary.timeMillis = end - start
        println(f" (${summary.timeMillis}%,d ms)")
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
