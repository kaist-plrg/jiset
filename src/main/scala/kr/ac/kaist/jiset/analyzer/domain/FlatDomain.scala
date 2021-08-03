package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.util.Useful._

// flat domain
class FlatDomain[A] extends Domain {
  object Bot extends Elem
  object Top extends Elem
  case class Base(elem: A) extends Elem

  // abstraction functions
  def apply(elems: A*): Elem = elems.size match {
    case 0 => Bot
    case 1 => Base(elems.head)
    case _ => Top
  }

  // elements
  sealed trait Elem extends ElemTrait with Iterable[A] {
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
