package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.util.Useful._

// set domain
class SetDomain[A] extends Domain {
  lazy val Bot = Base(Set())
  object Top extends Elem
  case class Base(set: Set[A]) extends Elem

  // abstraction functions
  def apply(elems: A*): Elem = Base(elems.toSet)

  // elements
  sealed trait Elem extends ElemTrait with Iterable[A] {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (_, Top) => true
      case (Top, _) => false
      case (Base(lset), Base(rset)) => lset subsetOf rset
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Top, _) | (_, Top) => Top
      case (Base(lset), Base(rset)) => Base(lset ++ rset)
    }

    // get single value
    def getSingle: Flat[A] = this match {
      case Base(set) => set.size match {
        case 0 => FlatBot
        case 1 => FlatElem(set.head)
        case _ => FlatTop
      }
      case _ => FlatTop
    }

    // iterators
    final def iterator: Iterator[A] = (this match {
      case Base(set) => set
      case Top =>
        warn("impossible to concretize the top value.")
        Set()
    }).iterator
  }
}
