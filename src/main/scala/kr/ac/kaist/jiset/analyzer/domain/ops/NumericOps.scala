package kr.ac.kaist.jiset.analyzer.domain.ops

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.ires.ir._

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
  def neg(elem: Elem): Elem

  // addition (+)
  def add(left: Elem, right: Elem): Elem

  // substitution (-)
  def sub(left: Elem, right: Elem): Elem

  // multiplication (*)
  def mul(left: Elem, right: Elem): Elem

  // division (/)
  def div(left: Elem, right: Elem): Elem

  // exponential (**)
  def pow(left: Elem, right: Elem): Elem

  // modulo (%)
  def mod(left: Elem, right: Elem): Elem

  // unsigned modulo (%%)
  def umod(left: Elem, right: Elem): Elem

  // comparison (<)
  def lt(left: Elem, right: Elem): AbsBool
}
