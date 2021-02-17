package kr.ac.kaist.jiset.analyzer.domain.bool

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

// boolean abstract domain
trait Domain extends AbsDomain[Bool] {
  // abstract operators
  implicit class Ops(elem: Elem) {
    def &&(that: Elem): Elem = and(elem, that)
    def ||(that: Elem): Elem = or(elem, that)
    def ^(that: Elem): Elem = xor(elem, that)
    def unary_!(): Elem = not(elem)
  }

  // logical conjunction (&&)
  def and(left: Elem, right: Elem): Elem

  // logical disjunction (||)
  def or(left: Elem, right: Elem): Elem

  // logical exclusive disjunction (^)
  def xor(left: Elem, right: Elem): Elem

  // logical negation (!)
  def not(elem: Elem): Elem
}
