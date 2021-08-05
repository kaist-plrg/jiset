package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// domain
trait Domain {
  // bottom element
  val Bot: Elem

  // element
  type Elem <: ElemTrait

  // appender
  implicit val app: App[Elem]

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

    // conversion to string
    override def toString: String = stringify(this)
  }

  // basic partial order
  object BasicOrder {
    def unapply(pair: (Elem, Elem)): Option[Boolean] = pair match {
      case (Bot, _) => Some(true)
      case (_, Bot) => Some(false)
      case (left, right) if left eq right => Some(true)
      case _ => None
    }
  }

  // basic join
  object BasicJoin {
    def unapply(pair: (Elem, Elem)): Option[Elem] = pair match {
      case (Bot, elem) => Some(elem)
      case (elem, Bot) => Some(elem)
      case (left, right) if left eq right => Some(left)
      case _ => None
    }
  }
}
