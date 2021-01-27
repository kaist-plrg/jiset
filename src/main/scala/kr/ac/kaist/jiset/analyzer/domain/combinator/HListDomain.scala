package kr.ac.kaist.jiset.analyzer.domain.combinator

import shapeless._
import shapeless.ops.hlist._
import kr.ac.kaist.jiset.analyzer.domain._

// HList domain
trait HListDomain[L <: HList] extends AbsDomain[L] {
  // prepend another abstract domain
  def ::[H](AbsH: AbsDomain[H]): HConsDomain[H, AbsH.type, L, this.type] =
    HConsDomain(AbsH, this)

  // result of conversion from elements to HList
  type ElemHList <: HList

  // conversion from elements to HList
  def elemToHList(elem: Elem): ElemHList

  // implicit class for `at` function for elements
  implicit class AtHelper(elem: Elem) {
    def at(n: Nat)(implicit at: At[ElemHList, n.N]): at.Out = at(elemToHList(elem))
  }
}

// HNil Domain
object HNilDomain
  extends generator.SimpleDomain(Set[HNil](HNil))
  with HListDomain[HNil] {
  // result of conversion from elements to HList
  type ElemHList = HNil

  // conversion from elements to HList
  def elemToHList(elem: Elem) = HNil
}

// HCons Domain
class HConsDomain[H, HD <: EAbsDomain[H], T <: HList, TD <: HListDomain[T] with Singleton](
    val AbsH: HD,
    val AbsT: TD
) extends HListDomain[H :: T] {
  type AbsH = AbsH.Elem
  type AbsT = AbsT.Elem

  // result of conversion from elements to HList
  type ElemHList = AbsH :: AbsT.ElemHList

  // conversion from elements to HList
  def elemToHList(elem: Elem) = elem.head :: AbsT.elemToHList(elem.tail)

  // abstraction function
  def alpha(list: H :: T): Elem = {
    val h :: t = list
    Elem(AbsH(h), AbsT(t))
  }

  // bottom value
  val Bot: Elem = Elem(AbsH.Bot, AbsT.Bot)

  // top value
  val Top: Elem = Elem(AbsH.Top, AbsT.Top)

  // normalization
  private def norm(elem: Elem): Elem = if (elem.isBottom) Bot else elem

  // abstract element
  case class Elem(head: AbsH, tail: AbsT) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean =
      this.head ⊑ that.head && this.tail ⊑ that.tail

    // join operator
    def ⊔(that: Elem): Elem =
      Elem(this.head ⊔ that.head, this.tail ⊔ that.tail)

    // meet operator
    def ⊓(that: Elem): Elem =
      norm(Elem(this.head ⊓ that.head, this.tail ⊓ that.tail))

    // concretization function
    def gamma: concrete.Set[H :: T] = (head.gamma, tail.gamma) match {
      case (Finite(hs), Finite(ts)) => Finite(for {
        h <- hs
        t <- ts
      } yield h :: t)
      case _ => Infinite
    }

    // conversion to flat domain
    def getSingle: concrete.Flat[H :: T] = (head.getSingle, tail.getSingle) match {
      case _ if isBottom => Zero
      case (One(h), One(t)) => One(h :: t)
      case _ => Many
    }

    // conversion to string
    override def toString: String = {
      if (isBottom) "⊥"
      else if (isTop) "⊤"
      else s"$head :: $tail"
    }

    // bottom check
    override def isBottom: Boolean = head.isBottom || tail.isBottom
  }
}
object HConsDomain {
  def apply[H, HD <: EAbsDomain[H], T <: HList, TD <: HListDomain[T] with Singleton](
    AbsH: HD,
    AbsT: TD
  ) = new HConsDomain[H, HD, T, TD](AbsH, AbsT)
}
