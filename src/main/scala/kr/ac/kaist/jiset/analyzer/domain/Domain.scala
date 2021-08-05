package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// domain
trait Domain {
  // top element
  val Top: Elem

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

    // top check
    def isTop: Boolean = this == Top

    // conversion to string
    override def toString: String = stringify(this)
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
