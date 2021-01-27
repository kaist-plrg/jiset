package kr.ac.kaist.jiset.analyzer.domain.value

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.ires.ir.Value

// value abstract domain
trait Domain extends AbsDomain[Value] {
  // abstract operators
  implicit class Ops(elem: Elem) {
    def boolset: Set[Boolean] = getBooleans(elem)
  }

  // check whether abstract values denotes only boolean values
  // and return the boolean values for branches.
  def getBooleans(elem: Elem): Set[Boolean]
}
