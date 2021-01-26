package kr.ac.kaist.jiset.analyzer.domain.combinator

import kr.ac.kaist.jiset.analyzer.domain._

// option abstract domain
case class OptionDomain[V, VD <: EAbsDomain[V]](
    AbsV: VD
) extends AbsDomain[Option[V]] {
  type AbsV = AbsV.Elem

  // abstraction function
  def alpha(opt: Option[V]): Elem = opt match {
    case Some(v) => Elem(AbsV(v), AbsAbsent.Bot)
    case None => Elem(AbsV.Bot, AbsAbsent.Top)
  }

  // bottom value
  val Bot: Elem = Elem(AbsV.Bot, AbsAbsent.Bot)

  // top value
  val Top: Elem = Elem(AbsV.Top, AbsAbsent.Top)

  // constructor
  def apply(value: AbsV, absent: AbsAbsent): Elem = Elem(value, absent)

  // extractor
  def unapply(elem: Elem): Option[(AbsV, AbsAbsent)] = Some((elem.value, elem.absent))

  // pair abstract element
  case class Elem(value: AbsV, absent: AbsAbsent) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = this.value ⊑ that.value && this.absent ⊑ that.absent

    // join operator
    def ⊔(that: Elem): Elem = Elem(this.value ⊔ that.value, this.absent ⊔ that.absent)

    // meet operator
    def ⊓(that: Elem): Elem = Elem(this.value ⊓ that.value, this.absent ⊓ that.absent)

    // concretization function
    def gamma: concrete.Set[Option[V]] = value.gamma match {
      case Finite(v) => Finite(v.map(Some(_)) ++ (
        if (absent.isTop) List(None) else Nil
      ))
      case _ => Infinite
    }

    // conversion to flat domain
    def getSingle: concrete.Flat[Option[V]] = (value.getSingle, absent.getSingle) match {
      case (One(v), Zero) => One(Some(v))
      case (Zero, One(n)) => One(None)
      case (Zero, Zero) => Zero
      case _ => Many
    }

    // conversion to string
    override def toString: String = this match {
      case Bot => "⊥"
      case Top => "⊤"
      case _ => s"($value, $absent)"
    }

    // absent check
    def isAbsent: Boolean = absent.isTop
  }
}
