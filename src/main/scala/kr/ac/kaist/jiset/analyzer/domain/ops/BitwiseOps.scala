package kr.ac.kaist.jiset.analyzer.domain.ops

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

// bit-wise operators
trait BitwiseOps { this: AbsDomain[_] =>
  // abstract operators
  implicit class BitwiseOps(elem: Elem) {
    def unary_~(): Elem = not(elem)
    def &(that: Elem): Elem = and(elem, that)
    def |(that: Elem): Elem = or(elem, that)
    def ^(that: Elem): Elem = xor(elem, that)
  }

  // bit-wise negation (~)
  def not(elem: Elem): Elem

  // bit-wise and (&)
  def and(left: Elem, right: Elem): Elem

  // bit-wise or (|)
  def or(left: Elem, right: Elem): Elem

  // bit-wise xor (^)
  def xor(left: Elem, right: Elem): Elem
}
