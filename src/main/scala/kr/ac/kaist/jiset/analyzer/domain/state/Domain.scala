package kr.ac.kaist.jiset.analyzer.domain.state

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.ires.ir.State

// state abstract domain
trait Domain extends AbsDomain[State] {
  // abstract state element
  type Elem <: ElemTrait

  // abstract state element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
  }
}
