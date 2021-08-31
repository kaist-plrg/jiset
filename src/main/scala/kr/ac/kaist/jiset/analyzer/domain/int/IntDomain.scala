package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.ir._

trait IntDomain extends Domain {
  // get intervals
  def getInterval(from: Long, to: Long): Elem

  // integer operators
  implicit def ElemOp(elem: Elem): IntOp
  trait IntOp {
    def plus(that: Elem): Elem
  }
}
