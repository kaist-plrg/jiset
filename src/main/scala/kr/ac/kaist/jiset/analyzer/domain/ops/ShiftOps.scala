package kr.ac.kaist.jiset.analyzer.domain.ops

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

// shift operators
trait ShiftOps { this: AbsDomain[_] =>
  // left shift (<<)
  val leftShift: (Elem, Elem) => Elem

  // right shift (>>)
  val rightShift: (Elem, Elem) => Elem

  // unsigned right shift (>>>)
  val unsignedRightShift: (Elem, Elem) => Elem
}

// shift operators helper
trait ShiftOpsHelper {
  type Domain <: AbsDomain[_] with ShiftOps
  val Domain: Domain
  val elem: Domain.Elem

  def <<(that: Domain.Elem): Domain.Elem = Domain.leftShift(elem, that)
  def >>(that: Domain.Elem): Domain.Elem = Domain.rightShift(elem, that)
  def >>>(that: Domain.Elem): Domain.Elem = Domain.unsignedRightShift(elem, that)
}
