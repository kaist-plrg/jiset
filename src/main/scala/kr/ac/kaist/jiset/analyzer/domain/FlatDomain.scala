package kr.ac.kaist.jiset.analyzer.domain

// flat domain
class FlatDomain[T] extends Domain {
  object Bot extends Elem
  object Top extends Elem
  case class Base(elem: T) extends Elem

  // abstraction function
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
  }
}
