package kr.ac.kaist.jiset.analyzer.domain.refvalue

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.generator._

object BasicDomain extends refvalue.Domain {
  // abstraction function
  def alpha(refVal: RefValue): Elem = refVal match {
    case RefValueId(id) => Elem(id = StrFlat(id))
    case RefValueProp(addr, name) => Elem(prop = (AbsAddr(addr), StrFlat(name)))
    case RefValueString(str, name) => Elem(string = (AbsStr(str), StrFlat(name)))
  }

  // bottom value
  val Bot: Elem = Elem()

  // top value
  val Top: Elem = Elem(
    StrFlat.Top,
    (AbsAddr.Top, StrFlat.Top),
    (AbsStr.Top, StrFlat.Top)
  )

  case class Elem(
    id: StrFlat = StrFlat.Bot,
    prop: (AbsAddr, StrFlat) = (AbsAddr.Bot, StrFlat.Bot),
    string: (AbsStr, StrFlat) = (AbsStr.Bot, StrFlat.Bot)
  ) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (
      this.id ⊑ that.id &&
      (this.prop._1 ⊑ that.prop._1 && this.prop._2 ⊑ that.prop._2) &&
      (this.string._1 ⊑ that.string._1 && this.string._2 ⊑ that.string._2)
    )

    // join operator
    def ⊔(that: Elem): Elem = Elem(
      this.id ⊔ that.id,
      (this.prop._1 ⊔ that.prop._1, this.prop._2 ⊔ that.prop._2),
      (this.string._1 ⊔ that.string._1, this.string._2 ⊔ that.string._2)
    )

    // meet operator
    def ⊓(that: Elem): Elem = Elem(
      this.id ⊓ that.id,
      (this.prop._1 ⊓ that.prop._1, this.prop._2 ⊓ that.prop._2),
      (this.string._1 ⊓ that.string._1, this.string._2 ⊓ that.string._2)
    )

    // concretization function
    def gamma: concrete.Set[RefValue] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[RefValue] = Many
  }
}
