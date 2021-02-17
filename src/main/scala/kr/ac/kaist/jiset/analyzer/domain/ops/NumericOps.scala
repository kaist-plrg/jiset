package kr.ac.kaist.jiset.analyzer.domain.ops

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

// numeric operators
trait NumericOps { this: AbsDomain[_] =>
  // abstract operators
  implicit class NumericOps(elem: Elem) {
    def unary_-(): Elem = neg(elem)
    def +(that: Elem): Elem = add(elem, that)
    def -(that: Elem): Elem = sub(elem, that)
    def *(that: Elem): Elem = mul(elem, that)
    def /(that: Elem): Elem = div(elem, that)
    def **(that: Elem): Elem = pow(elem, that)
    def %(that: Elem): Elem = mod(elem, that)
    def %%(that: Elem): Elem = umod(elem, that)
    def <(that: Elem): AbsBool = lt(elem, that)
  }

  // negation (-)
  val neg: Elem => Elem

  // addition (+)
  val add: (Elem, Elem) => Elem

  // substitution (-)
  val sub: (Elem, Elem) => Elem

  // multiplication (*)
  val mul: (Elem, Elem) => Elem

  // division (/)
  val div: (Elem, Elem) => Elem

  // exponential (**)
  val pow: (Elem, Elem) => Elem

  // modulo (%)
  val mod: (Elem, Elem) => Elem

  // unsigned modulo (%%)
  val umod: (Elem, Elem) => Elem

  // comparison (<)
  val lt: (Elem, Elem) => AbsBool
}
