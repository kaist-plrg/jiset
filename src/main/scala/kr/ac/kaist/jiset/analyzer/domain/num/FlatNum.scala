package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.ir._

object FlatNum extends FlatDomain[Num] {
  val topName = "num"
  val totalOpt = None

  // get intervals
  def getInterval(from: Double, to: Double): Elem =
    if (from == to) Base(Num(from)) else Top

  implicit class ElemOp(elem: Elem) {
    def plus(that: Elem): Elem = aux(_ + _)(elem, that)
    def plusInt(that: AbsInt): Elem = auxInt(_ + _)(elem, that.getSingle)
    def mul(that: Elem): Elem = aux(_ * _)(elem, that)
    def mulInt(that: AbsInt): Elem = auxInt(_ * _)(elem, that.getSingle)
    private def aux(op: (Double, Double) => Double): (Elem, Elem) => Elem = {
      case (Bot, _) | (_, Bot) => Bot
      case (Base(Num(l)), Base(Num(r))) => Base(Num(op(l, r)))
      case _ => Top
    }
    private def auxInt(op: (Double, Long) => Double): (Elem, Flat[INum]) => Elem = {
      case (Bot, _) | (_, FlatBot) => Bot
      case (Base(Num(l)), FlatElem(INum(r))) => Base(Num(op(l, r)))
      case _ => Top
    }
  }
}
