package kr.ac.kaist.jiset.analyzer.domain.ops

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

// bit-wise operators
trait BitwiseOps { this: AbsDomain[_] =>
  // bit-wise negation (~)
  val not: Elem => Elem

  // bit-wise and (&)
  val and: (Elem, Elem) => Elem

  // bit-wise or (|)
  val or: (Elem, Elem) => Elem

  // bit-wise xor (^)
  val xor: (Elem, Elem) => Elem
}

// bit-wise operators helper
trait BitwiseOpsHelper {
  type Domain <: AbsDomain[_] with BitwiseOps
  val Domain: Domain
  val elem: Domain.Elem

  def unary_~(): Domain.Elem = Domain.not(elem)
  def &(that: Domain.Elem): Domain.Elem = Domain.and(elem, that)
  def |(that: Domain.Elem): Domain.Elem = Domain.or(elem, that)
  def ^(that: Domain.Elem): Domain.Elem = Domain.xor(elem, that)
}
