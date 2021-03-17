package kr.ac.kaist.jiset.analyzer.domain.combinator

import kr.ac.kaist.jiset.analyzer.domain._

// list abstract domain
class ListDomain[V, VD <: AbsDomain[V]](
  val AbsV: VD
) extends AbsDomain[List[V]] {
  type AbsV = AbsV.Elem

  // abstraction function
  def alpha(list: List[V]): Elem = ListElem(AbsV(list.toSet))

  // bottom value
  object Bot extends Elem

  // top value
  val Top = ListElem(AbsV.Top)

  // list element
  case class ListElem(v: AbsV) extends Elem

  // empty value
  lazy val Empty: Elem = ListElem(AbsV.Bot)

  // constructor
  def apply(value: AbsV): Elem = ListElem(value)

  // pair abstract element
  sealed trait Elem extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) => true
      case (_, Bot) => false
      case (ListElem(l), ListElem(r)) => l ⊑ r
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) => that
      case (_, Bot) => this
      case (ListElem(l), ListElem(r)) => ListElem(l ⊔ r)
    }

    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Bot) => Bot
      case (ListElem(l), ListElem(r)) => ListElem(l ⊓ r)
    }

    // concretization function
    def gamma: concrete.Set[List[V]] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[List[V]] = Many

    // get value
    def value: AbsV = this match {
      case Bot => AbsV.Bot
      case ListElem(v) => v
    }

    // get length
    def length: AbsNum = this match {
      case Bot => AbsNum.Bot
      case ListElem(v) =>
        if (v.isBottom) AbsNum(0)
        else AbsNum.Top
    }
  }
}
object ListDomain {
  def apply[V, VD <: EAbsDomain[V]](AbsV: VD) = new ListDomain[V, VD](AbsV)
}
