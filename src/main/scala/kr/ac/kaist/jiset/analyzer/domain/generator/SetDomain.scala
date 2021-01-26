package kr.ac.kaist.jiset.analyzer.domain.generator

import kr.ac.kaist.jiset.analyzer.domain._
import collection.immutable.{ Set => SSet }

// set abstract domain
class SetDomain[V](total: SSet[V] = SSet()) extends AbsDomain[V] {
  // top value
  object Top extends Elem

  // set value
  case class Set(elem: SSet[V]) extends Elem

  // bottom value
  val Bot = Set(SSet())

  // abstraction function
  def alpha(v: V): Elem = Set(SSet(v))

  // total size
  val totalSize = total.size

  // set abstract element
  sealed trait Elem extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (_, Top) => true
      case (Set(l), Set(r)) if l subsetOf r => true
      case _ => false
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Top, _) | (_, Top) => Top
      case (Set(l), Set(r)) =>
        val merged = l ++ r
        if (merged.size > totalSize) Top
        else Set(merged)
    }

    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Top, _) => that
      case (_, Top) => this
      case (Set(l), Set(r)) => Set(l intersect r)
    }

    // concretization function
    lazy val gamma: concrete.Set[V] = this match {
      case Top => if (total.isEmpty) Infinite else Finite(total)
      case Set(s) => Finite(s)
    }

    // conversion to flat domain
    lazy val getSingle: concrete.Flat[V] = this match {
      case Top => if (total.size == 1) One(total.head) else Many
      case Set(set) => set.size match {
        case 0 => Zero
        case 1 => One(set.head)
        case _ => Many
      }
    }

    // conversion to string
    override def toString: String = this match {
      case Top => "⊤"
      case Set(set) => "[" + set.mkString(", ") + "]"
    }
  }
}
object SetDomain {
  def apply[V](seq: V*): SetDomain[V] = apply(seq.toSet)
  def apply[V](total: Set[V]): SetDomain[V] = new SetDomain(total)
}
