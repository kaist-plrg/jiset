package kr.ac.kaist.jiset.analyzer.domain.value

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.ires.ir.Value

// value abstract domain
trait Domain extends AbsDomain[Value] {
  // abstract value element
  type Elem <: ElemTrait

  // abstract value element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    // check whether abstract values denotes only boolean values
    // and return the boolean values for branches.
    def boolset: Set[Boolean]
  }
}
