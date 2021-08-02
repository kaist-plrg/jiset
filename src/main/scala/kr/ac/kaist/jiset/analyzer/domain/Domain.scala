package kr.ac.kaist.jiset.analyzer.domain

// domain
trait Domain {
  // top element
  val Top: Elem

  // bottom element
  val Bot: Elem

  // element
  type Elem <: ElemTrait

  // element traits
  trait ElemTrait { this: Elem =>
    // partial order
    def ⊑(that: Elem): Boolean

    // join operator
    def ⊔(that: Elem): Elem

    // not partial order
    def !⊑(that: Elem): Boolean = !(this ⊑ that)

    // bottom check
    def isBottom: Boolean = this == Bot

    // top check
    def isTop: Boolean = this == Top
  }

  // basic partial order
  object BasicOrder {
    def unapply(pair: (Elem, Elem)): Option[Boolean] = pair match {
      case (Bot, _) | (_, Top) => Some(true)
      case (Top, _) | (_, Bot) => Some(false)
      case (left, right) if left eq right => Some(true)
      case _ => None
    }
  }

  // basic join
  object BasicJoin {
    def unapply(pair: (Elem, Elem)): Option[Elem] = pair match {
      case (Top, _) | (_, Top) => Some(Top)
      case (Bot, elem) => Some(elem)
      case (elem, Bot) => Some(elem)
      case (left, right) if left eq right => Some(left)
      case _ => None
    }
  }
}
