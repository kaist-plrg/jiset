package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.ir._

object FlatNum extends FlatDomain[Num] {
  val topName = "num"
  val totalOpt = None

  implicit class ElemOp(elem: Elem) {
    def plus(that: Elem): Elem = (elem, that) match {
      case (Bot, _) | (_, Bot) => Bot
      case (Base(Num(l)), Base(Num(r))) => Base(Num(l + r))
      case _ => Top
    }
    def plusInt(that: AbsInt): Elem = (elem, that.getSingle) match {
      case (Bot, _) | (_, FlatBot) => Bot
      case (Base(Num(l)), FlatElem(INum(r))) => Base(Num(l + r))
      case _ => Top
    }
  }
}
