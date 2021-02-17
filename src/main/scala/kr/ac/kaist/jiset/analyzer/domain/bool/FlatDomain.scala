package kr.ac.kaist.jiset.analyzer.domain.bool

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

object FlatDomain extends generator.FlatDomain[Bool](true, false) with bool.Domain {
  // logical conjunction (&&)
  def and(left: Elem, right: Elem): Elem = alpha(_ && _)(left, right)

  // logical disjunction (||)
  def or(left: Elem, right: Elem): Elem = alpha(_ || _)(left, right)

  // logical exclusive disjunction (^)
  def xor(left: Elem, right: Elem): Elem = alpha(_ ^ _)(left, right)

  // logical negation (!)
  def not(elem: Elem): Elem = alpha(!_)(elem)
}
