package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// simple domain
trait SimpleDomain[A] extends Domain {
  // target value
  protected val value: A

  // elements
  object Bot extends Elem
  object Top extends Elem

  // appender
  implicit val app: App[Elem] = (app, elem) => elem match {
    case Bot => app >> "⊥"
    case Top => app >> value.toString
  }

  // elements
  sealed trait Elem extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case BasicOrder(bool) => bool
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case BasicJoin(elem) => elem
    }

    // get single value
    def getSingle: Flat[value.type] = this match {
      case Bot => FlatBot
      case Top => FlatElem(value)
    }
  }
}
object SimpleDomain {
  // constructors
  def apply[A](value: A): SimpleDomain[A] = {
    val v = value
    new SimpleDomain[A] {
      protected val value: A = v
    }
  }
}
