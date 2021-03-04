package kr.ac.kaist.jiset.analyzer.domain.combinator

import shapeless._
import shapeless.ops.hlist._
import kr.ac.kaist.jiset.analyzer.domain._

// Product domain
abstract class ProductDomain[P <: Product, H <: HList, HD <: HListDomain[H]](
  val gen: Generic[P] { type Repr = H }
) extends AbsDomain[P] {
  val HElem: HD
  import HElem._

  type HElem = HElem.Elem

  // abstraction function
  def alpha(prod: P): Elem = Elem(HElem.alpha(gen.to(prod)))

  // bottom value
  lazy val Bot: Elem = Elem(HElem.Bot)

  // top value
  lazy val Top: Elem = Elem(HElem.Top)

  // implicit class for `at` function for elements
  implicit class AtHelper(elem: Elem) {
    def at(n: Nat)(implicit at: At[HElem.ElemHList, n.N]): at.Out =
      at(HElem.elemToHList(elem.helem))
  }

  // constructor
  def apply(helem: HElem): Elem = Elem(helem)

  // extractor
  def unapply(elem: Elem): Option[HElem] = Some(elem.helem)

  // abstract element
  case class Elem(helem: HElem) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = this.helem ⊑ that.helem

    // join operator
    def ⊔(that: Elem): Elem = Elem(this.helem ⊔ that.helem)

    // meet operator
    def ⊓(that: Elem): Elem = Elem(this.helem ⊓ that.helem)

    // concretization function
    def gamma: concrete.Set[P] = helem.gamma.map(gen.from)

    // conversion to flat domain
    def getSingle: concrete.Flat[P] = helem.getSingle.map(gen.from)
  }
}
object ProductDomain {
  def apply[P <: Product, H <: HList, HD <: HListDomain[H]](
    gen: Generic[P] { type Repr = H }
  )(HElem0: HD) =
    new ProductDomain[P, H, HD](gen) { val HElem = HElem0 }
}
