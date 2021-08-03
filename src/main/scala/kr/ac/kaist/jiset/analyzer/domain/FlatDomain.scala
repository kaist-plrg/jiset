package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.util.Useful._

// flat domain
class FlatDomain[T] extends Domain {
  object Bot extends Elem
  object Top extends Elem
  case class Base(elem: T) extends Elem

  // abstraction functions
  def apply(elems: T*): Elem = elems.size match {
    case 0 => Bot
    case 1 => Base(elems.head)
    case _ => Top
  }

  // elements
  sealed trait Elem extends ElemTrait {
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
    def getSingle: Flat[T] = this match {
      case Bot => FlatBot
      case Top => FlatTop
      case Base(elem) => FlatElem(elem)
    }

    // conversion to set of elements
    def toSet: Set[T] = this match {
      case Bot => Set()
      case Base(elem) => Set(elem)
      case Top =>
        warn("impossible to concretize the top value.")
        Set()
    }

    // conversion to list of elements
    def toList: List[T] = this match {
      case Bot => Nil
      case Base(elem) => List(elem)
      case Top =>
        warn("impossible to concretize the top value.")
        Nil
    }
  }
}
