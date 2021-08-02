package kr.ac.kaist.jiset.analyzer.domain

// set domain
class SetDomain[T] extends Domain {
  lazy val Bot = Base(Set())
  object Top extends Elem
  case class Base(set: Set[T]) extends Elem

  // abstraction function
  def apply(elems: T*): Elem = Base(elems.toSet)

  // elements
  sealed trait Elem extends ElemTrait {
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
    def getSingle: Flat[T] = this match {
      case Base(set) => set.size match {
        case 0 => FlatBot
        case 1 => FlatElem(set.head)
        case _ => FlatTop
      }
      case _ => FlatTop
    }
  }
}
