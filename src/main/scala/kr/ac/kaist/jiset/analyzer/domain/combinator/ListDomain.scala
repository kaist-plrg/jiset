package kr.ac.kaist.jiset.analyzer.domain.combinator

import kr.ac.kaist.jiset.analyzer.domain._

// list abstract domain
class ListDomain[V, VD <: AbsDomain[V]](
  val AbsV: VD
) extends AbsDomain[List[V]] {
  val AbsVOpt = OptionDomain[V, AbsV.type](AbsV)
  type AbsV = AbsV.Elem
  type AbsVOpt = AbsVOpt.Elem

  // abstraction function
  def alpha(list: List[V]): Elem = Fixed(list.map(AbsV(_)).toVector)

  // bottom value
  object Bot extends Elem

  // top value
  object Top extends Elem

  // fixed length
  case class Fixed(vector: Vector[AbsV]) extends Elem

  // unfixed length
  case class Unfixed(value: AbsV) extends Elem

  // empty value
  lazy val Empty: Elem = Fixed(Vector())

  // constructor
  def apply(list: List[AbsV]): Elem = Fixed(list.toVector)
  def apply(value: AbsV): Elem = Unfixed(value)

  // pair abstract element
  sealed trait Elem extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) | (_, Top) => true
      case (Top, _) | (_, Bot) => false
      case (Fixed(vector), Unfixed(value)) => vector.forall(_ ⊑ value)
      case (Fixed(lv), Fixed(rv)) =>
        lv.length == rv.length && (lv zip rv).forall { case (l, r) => l ⊑ r }
      case (Unfixed(l), Unfixed(r)) => l ⊑ r
      case _ => false
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Top) => that
      case (Top, _) | (_, Bot) => this
      case (Fixed(lv), Fixed(rv)) if lv.length == rv.length =>
        Fixed(for ((l, r) <- lv zip rv) yield l ⊔ r)
      case _ => Unfixed(this.getValue ⊔ that.getValue)
    }

    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Top) => this
      case (Top, _) | (_, Bot) => that
      case (Fixed(lv), Fixed(rv)) if lv.length == rv.length =>
        Fixed(for ((l, r) <- lv zip rv) yield l ⊓ r)
      case _ => Unfixed(this.getValue ⊓ that.getValue)
    }

    // concretization function
    def gamma: concrete.Set[List[V]] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[List[V]] = Many

    // lookup
    def apply(n: Int): AbsVOpt = this match {
      case Bot => AbsVOpt.Bot
      case Top => AbsVOpt.Top
      case Fixed(vector) =>
        if (0 <= n && n < vector.length) AbsVOpt(vector(n))
        else AbsVOpt.Absent
      case Unfixed(value) => AbsVOpt(value, AbsAbsent.Top)
    }

    // get value
    def getValue: AbsV = this match {
      case Bot => AbsV.Bot
      case Top => AbsV.Top
      case Fixed(vector) => vector.foldLeft(AbsV.Bot)(_ ⊔ _)
      case Unfixed(value) => value
    }
  }
}
object ListDomain {
  def apply[V, VD <: EAbsDomain[V]](AbsV: VD) = new ListDomain[V, VD](AbsV)
}
