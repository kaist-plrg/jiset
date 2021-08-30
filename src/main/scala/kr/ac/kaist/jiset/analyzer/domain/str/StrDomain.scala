package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.ir._

trait StrDomain extends Domain {
  implicit def ElemOp(elem: Elem): StrOp
  trait StrOp {
    def plus(that: Elem): Elem
    def plusNum(that: AbsNum): Elem
  }
}
