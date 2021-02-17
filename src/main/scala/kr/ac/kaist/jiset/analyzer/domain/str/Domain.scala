package kr.ac.kaist.jiset.analyzer.domain.str

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

// string abstract domain
trait Domain extends AbsDomain[Str] {
  // abstract operators
  implicit class Ops(elem: Elem) {
    def +(that: Elem): Elem = add(elem, that)
    def -(num: AbsINum): Elem = sub(elem, num)
    def *(num: AbsINum): Elem = mul(elem, num)
    def <(that: Elem): AbsBool = lt(elem, that)
  }

  // string addition (+)
  def add(left: Elem, right: Elem): Elem

  // drop right (-)
  def sub(left: Elem, right: AbsINum): Elem

  // string multiplication (*)
  def mul(left: Elem, right: AbsINum): Elem

  // string comparison (<)
  def lt(left: Elem, right: Elem): AbsBool
}
