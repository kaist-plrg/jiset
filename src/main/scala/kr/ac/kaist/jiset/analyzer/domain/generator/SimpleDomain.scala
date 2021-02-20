package kr.ac.kaist.jiset.analyzer.domain.generator

import kr.ac.kaist.jiset.analyzer.domain._

// simple abstract domain
class SimpleDomain[V](total: Set[V]) extends AbsDomain[V] {
  // constructors
  def this(seq: V*) = this(seq.toSet)

  // top value
  object Top extends Elem

  // bottom value
  object Bot extends Elem

  // abstraction function
  def alpha(v: V): Elem = Top

  // simple abstract element
  sealed trait Elem extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Top, Bot) => false
      case _ => true
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, Bot) => Bot
      case _ => Top
    }

    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Top, Top) => Top
      case _ => Bot
    }

    // concretization function
    lazy val gamma: concrete.Set[V] = this match {
      case Top => if (total.isEmpty) Infinite else Finite(total)
      case Bot => Finite()
    }

    // conversion to flat domain
    lazy val getSingle: concrete.Flat[V] = this match {
      case Top => if (total.size == 1) One(total.head) else Many
      case Bot => Zero
    }
  }
}
object SimpleDomain {
  def apply[V](seq: V*): SimpleDomain[V] = apply(seq.toSet)
  def apply[V](total: Set[V]): SimpleDomain[V] = new SimpleDomain(total)
}
