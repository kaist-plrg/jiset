package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// flat domain
class FlatDomain[A](setName: String) extends Domain {
  object Bot extends Elem
  object Top extends Elem
  case class Base(elem: A) extends Elem

  // abstraction functions
  def apply(elems: A*): Elem = elems.size match {
    case 0 => Bot
    case 1 => Base(elems.head)
    case _ => Top
  }

  // appender
  implicit val app: App[Elem] = (app, elem) => elem match {
    case Bot => app >> "⊥"
    case Top => app >> setName
    case Base(v) => app >> v.toString
  }

  // elements
  sealed trait Elem extends Iterable[A] with ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case BasicOrder(bool) => bool
      case (Base(l), Base(r)) => l == r
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case BasicJoin(elem) => elem
      case (Base(l), Base(r)) => if (l == r) this else Top
    }

    // get single value
    def getSingle: Flat[A] = this match {
      case Bot => FlatBot
      case Top => FlatTop
      case Base(elem) => FlatElem(elem)
    }

    // iterators
    final def iterator: Iterator[A] = (this match {
      case Bot => None
      case Base(elem) => Option(elem)
      case Top =>
        warn("impossible to concretize the top value.")
        None
    }).iterator
  }
}
