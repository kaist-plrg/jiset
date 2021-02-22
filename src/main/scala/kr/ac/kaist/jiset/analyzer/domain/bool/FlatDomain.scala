package kr.ac.kaist.jiset.analyzer.domain.bool

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

object FlatDomain extends generator.FlatDomain[Bool](true, false)
  with bool.Domain { self =>
  implicit class BoolOps(elem: Elem) {
    def &&(that: Elem): Elem = and(elem, that)
    def ||(that: Elem): Elem = or(elem, that)
    def ^(that: Elem): Elem = xor(elem, that)
    def unary_!(): Elem = not(elem)
    def toSet: Set[Boolean] = self.toSet(elem)
  }

  // logical conjunction (&&)
  def and(left: Elem, right: Elem): Elem = alpha(_ && _)(left, right)

  // logical disjunction (||)
  def or(left: Elem, right: Elem): Elem = alpha(_ || _)(left, right)

  // logical exclusive disjunction (^)
  def xor(left: Elem, right: Elem): Elem = alpha(_ ^ _)(left, right)

  // logical negation (!)
  def not(elem: Elem): Elem = alpha(!_)(elem)

  // conversion to boolean set
  def toSet(elem: Elem): Set[Boolean] = elem match {
    case Top => Set(true, false)
    case Single(Bool(b)) => Set(b)
    case Bot => Set()
  }
}
