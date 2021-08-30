package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.ir._

object FlatInt extends FlatDomain[INum] {
  val topName = "int"
  val totalOpt = None

  // get intervals
  def getInterval(from: Int, to: Int): Elem =
    if (from == to) Base(INum(from)) else Top

  implicit class ElemOp(elem: Elem) {
    def plus(that: Elem): Elem = (elem, that) match {
      case (Bot, _) | (_, Bot) => Bot
      case (Base(INum(l)), Base(INum(r))) => Base(INum(l + r))
      case _ => Top
    }
  }
}
