package kr.ac.kaist.jiset.analyzer.domain.ops

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.ires.ir._

// shift operators
trait ShiftOps { this: AbsDomain[_] =>
  // abstract operators
  implicit class ShiftOps(elem: Elem) {
    def <<(that: Elem): Elem = leftShift(elem, that)
    def >>(that: Elem): Elem = rightShift(elem, that)
    def >>>(that: Elem): Elem = unsignedRightShift(elem, that)
  }

  // left shift (<<)
  def leftShift(left: Elem, right: Elem): Elem

  // right shift (>>)
  def rightShift(left: Elem, right: Elem): Elem

  // unsigned right shift (>>>)
  def unsignedRightShift(left: Elem, right: Elem): Elem
}
