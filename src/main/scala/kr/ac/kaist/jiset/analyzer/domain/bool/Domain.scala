package kr.ac.kaist.jiset.analyzer.domain.bool

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

// boolean abstract domain
trait Domain extends AbsDomain[Bool] {
  // logical conjunction (&&)
  def and(left: Elem, right: Elem): Elem

  // logical disjunction (||)
  def or(left: Elem, right: Elem): Elem

  // logical exclusive disjunction (^)
  def xor(left: Elem, right: Elem): Elem

  // logical negation (!)
  def not(elem: Elem): Elem
}
