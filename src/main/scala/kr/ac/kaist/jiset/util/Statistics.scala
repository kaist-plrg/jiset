package kr.ac.kaist.jiset.util

import kr.ac.kaist.jiset.LINE_SEP

// box plot
class BoxPlot[T](seqs: Seq[T], implicit val num: Numeric[T]) {
  import Statistics._

  // sorted data
  private lazy val sorted = seqs.sorted
  
  def size: Int = seqs.size
  
  // quartile data
  lazy val min: T = sorted.head
  lazy val max: T = sorted.last
  lazy val (q1, q3): (Double, Double) = {
    if (size <= 1) (min, min)
    else {
      var (lh, uh) = sorted.splitAt(size / 2)
      if (size % 2 == 1)  uh = uh.tail
      (median(lh, true).get, median(uh, true).get)
    }
  }
  lazy val med: Double = median(sorted, true).get

  // average
  lazy val avg: Double = num.toDouble(seqs.sum(num)) / seqs.size
  
  // to csv summary string
  def csvSummary: String =
    f"$min%2.2f,$q1%2.2f,$med%2.2f,$q3%2.2f,$max%2.2f,$avg%2.2f,$size"

  // to summary string
  def summary: String = {
    val app = new Appender
    app >> f"$size%,d items (avg: $avg%2.2f)" >> LINE_SEP
    app >> f"-  min: $min%2.2f" >> LINE_SEP
    app >> f"-   Q1: $q1%2.2f" >> LINE_SEP
    app >> f"-  med: $med%2.2f" >> LINE_SEP
    app >> f"-   Q3: $q3%2.2f" >> LINE_SEP
    app >> f"-  max: $max%2.2f"
    app.toString
  }

  // implicit conversion
  implicit def toDouble(t: T): Double = num.toDouble(t)
}
object BoxPlot {
  def apply[T](seqs: Seq[T])(implicit num: Numeric[T]): BoxPlot[T] =
    new BoxPlot[T](seqs, num)
}

// statistics
object Statistics {
  // median
  def median[T](
    data: Seq[T],
    ordered: Boolean = false
  )(implicit num: Numeric[T]): Option[Double] = 
    if (data.isEmpty) None
    else {
      val sorted = if (ordered) data else data.sorted
      val size = sorted.size
      val idx = sorted.size / 2
      Some(
        if (size % 2 == 1) num.toDouble(data(idx))
        else {
          val s = num.plus(data(idx), data(idx-1))
          num.toDouble(s) / 2
        }
      )
    }
}
