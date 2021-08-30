package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.ir._

object FlatNum extends FlatDomain[Num] {
  val topName = "num"
  val totalOpt = None

  // get intervals
  def getInterval(from: Int, to: Int): Elem =
    if (from == to) Base(Num(from)) else Top

  implicit class ElemOp(elem: Elem) {
  }
}
