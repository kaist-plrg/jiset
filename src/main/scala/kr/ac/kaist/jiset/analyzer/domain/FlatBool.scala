package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.ir._

object FlatBool extends FlatDomain[Bool] {
  val topName = "bool"
  val totalOpt = None

  implicit class ElemOp(elem: Elem) {
    def unary_!(): Elem = elem match {
      case Bot => Bot
      case Top => Top
      case Base(Bool(b)) => Base(Bool(!b))
    }
  }
}
