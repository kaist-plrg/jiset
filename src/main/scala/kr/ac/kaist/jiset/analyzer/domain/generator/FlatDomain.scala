package kr.ac.kaist.jiset.analyzer.domain.generator

import kr.ac.kaist.jiset.analyzer.domain._

// flat abstract domain
class FlatDomain[V](total: Set[V] = Set[V]()) extends AbsDomain[V] {
  // constructors
  def this(seq: V*) = this(seq.toSet)

  // top value
  object Top extends Elem

  // singleton value
  case class Single(elem: V) extends Elem

  // bottom value
  object Bot extends Elem

  // abstraction function
  def alpha(v: V): Elem = Single(v)

  // flat abstract element
  sealed trait Elem extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) | (_, Top) => true
      case (Single(l), Single(r)) if l == r => true
      case _ => false
    }

    // join operator
    def ⊔(that: Elem): Elem = {
      if (this ⊑ that) that
      else if (that ⊑ this) this
      else Top
    }

    // meet operator
    def ⊓(that: Elem): Elem = {
      if (this ⊑ that) this
      else if (that ⊑ this) that
      else Bot
    }

    // concretization function
    lazy val gamma: concrete.Set[V] = this match {
      case Top => if (total.isEmpty) Infinite else Finite(total)
      case Single(v) => Finite(v)
      case Bot => Finite()
    }

    // conversion to flat domain
    lazy val getSingle: concrete.Flat[V] = this match {
      case Top => if (total.size == 1) One(total.head) else Many
      case Single(v) => One(v)
      case Bot => Zero
    }
  }
}
object FlatDomain {
  def apply[V](seq: V*): FlatDomain[V] = apply(seq.toSet)
  def apply[V](total: Set[V]) = new FlatDomain[V](total)
}
