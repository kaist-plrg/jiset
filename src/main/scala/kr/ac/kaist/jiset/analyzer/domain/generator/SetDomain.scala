package kr.ac.kaist.jiset.analyzer.domain.generator

import kr.ac.kaist.jiset.analyzer.domain._

// set abstract domain
class SetDomain[V](total: Set[V]) extends AbsDomain[V] {
  // constructors
  def this(seq: V*) = this(seq.toSet)

  // top value
  object Top extends Elem

  // set value
  case class VSet(elem: Set[V]) extends Elem

  // bottom value
  val Bot = VSet(Set())

  // abstraction function
  def alpha(v: V): Elem = VSet(Set(v))

  // upper bound of size
  val upperBound = total.size

  // set abstract element
  sealed trait Elem extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (_, Top) => true
      case (VSet(l), VSet(r)) if l subsetOf r => true
      case _ => false
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Top, _) | (_, Top) => Top
      case (VSet(l), VSet(r)) =>
        val merged = l ++ r
        if (upperBound != 0 && merged.size > upperBound) Top
        else VSet(merged)
    }

    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Top, _) => that
      case (_, Top) => this
      case (VSet(l), VSet(r)) => VSet(l intersect r)
    }

    // concretization function
    lazy val gamma: concrete.Set[V] = this match {
      case Top => if (total.isEmpty) Infinite else Finite(total)
      case VSet(s) => Finite(s)
    }

    // conversion to flat domain
    lazy val getSingle: concrete.Flat[V] = this match {
      case Top => if (total.size == 1) One(total.head) else Many
      case VSet(set) => set.size match {
        case 0 => Zero
        case 1 => One(set.head)
        case _ => Many
      }
    }

    // map function
    def map(f: V => V): Elem = this match {
      case Top => Top
      case VSet(set) => VSet(set.map(f))
    }

    // foreach function
    def foreach(f: V => Unit): Unit = this match {
      case Top => ???
      case VSet(set) => set.foreach(f)
    }
  }
}
object SetDomain {
  def apply[V](seq: V*): SetDomain[V] = apply(seq.toSet)
  def apply[V](total: Set[V]) = new SetDomain[V](total)
  def apply[V](total: Set[V] = Set[V](), bound: Int = 0) =
    new SetDomain[V](total) { override val upperBound = bound }
}
