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
  val not: Elem => Elem

  // bit-wise and (&)
  val and: (Elem, Elem) => Elem

  // bit-wise or (|)
  val or: (Elem, Elem) => Elem

  // bit-wise xor (^)
  val xor: (Elem, Elem) => Elem
}
