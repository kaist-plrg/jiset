package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.ir._

object FlatInt extends FlatDomain[INum] with IntDomain {
  val topName = "int"
  val totalOpt = None

  implicit class ElemOp(elem: Elem) extends IntOp {
    def plus(that: Elem): Elem = aux(_ + _)(elem, that)
    def mul(that: Elem): Elem = aux(_ * _)(elem, that)
    private def aux(op: (Long, Long) => Long): (Elem, Elem) => Elem = {
      case (Bot, _) | (_, Bot) => Bot
      case (Base(INum(l)), Base(INum(r))) => Base(INum(op(l, r)))
      case _ => Top
    }
  }
}
