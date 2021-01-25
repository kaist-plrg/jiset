package kr.ac.kaist.jiset.analyzer.domain

// domain
trait Domain {
  // element
  type Elem <: ElemTrait

  // top element
  val Top: Elem

  // bottom element
  val Bot: Elem

  // element traits
  protected trait ElemTrait { this: Elem =>
    // bottom check
    def isBottom: Boolean = this == Bot

    // top check
    def isTop: Boolean = this == Top

    // partial order
    def ⊑(that: Elem): Boolean

    // not partial order
    def !⊑(that: Elem): Boolean = !(this ⊑ that)

    // join operator
    def ⊔(that: Elem): Elem

    // meet operator
    def ⊓(that: Elem): Elem
  }
}
