package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.ir._

object FlatBigInt extends FlatDomain[BigINum] {
  val topName = "bigint"
  val totalOpt = None

  implicit class ElemOp(elem: Elem) {
    def plus(that: Elem): Elem = (elem, that) match {
      case (Bot, _) | (_, Bot) => Bot
      case (Base(BigINum(l)), Base(BigINum(r))) => Base(BigINum(l + r))
      case _ => Top
    }
  }
}
