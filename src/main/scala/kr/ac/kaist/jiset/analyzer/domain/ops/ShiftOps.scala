package kr.ac.kaist.jiset.analyzer.domain.ops

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

// shift operators
trait ShiftOps { this: AbsDomain[_] =>
  // abstract operators
  implicit class ShiftOps(elem: Elem) {
    def <<(that: Elem): Elem = leftShift(elem, that)
    def >>(that: Elem): Elem = rightShift(elem, that)
    def >>>(that: Elem): Elem = unsignedRightShift(elem, that)
  }

  // left shift (<<)
  val leftShift: (Elem, Elem) => Elem

  // right shift (>>)
  val rightShift: (Elem, Elem) => Elem

  // unsigned right shift (>>>)
  val unsignedRightShift: (Elem, Elem) => Elem
}
