package kr.ac.kaist.jiset.analyzer.domain.refvalue

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.generator._

object BasicDomain extends refvalue.Domain {
  // abstraction function
  def alpha(refVal: RefValue): Elem = refVal match {
    case RefValueId(name) => Id(name)
    case RefValueProp(addr, name) => Prop(AbsValue(addr), AbsPure(name))
    case RefValueString(str, name) => Prop(AbsValue(str), AbsPure(name))
  }

  // bottom value
  object Bot extends Elem

  // top value
  object Top extends Elem

  // id reference values
  case class Id(name: String) extends Elem

  // property reference values
  case class Prop(base: AbsValue, prop: AbsPure) extends Elem

  trait Elem extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) | (_, Top) => true
      case (Id(lname), Id(rname)) => lname == rname
      case (Prop(lb, lp), Prop(rb, rp)) => lb ⊑ rb && lp ⊑ rp
      case _ => false
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Top) => that
      case (_, Bot) | (Top, _) => this
      case (Id(lname), Id(rname)) if lname == rname => this
      case (Prop(lb, lp), Prop(rb, rp)) => Prop(lb ⊔ rb, lp ⊔ rp)
      case _ => Top
    }

    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Top) => this
      case (_, Bot) | (Top, _) => that
      case (Id(lname), Id(rname)) if lname == rname => this
      case (Prop(lb, lp), Prop(rb, rp)) => Prop(lb ⊓ rb, lp ⊓ rp)
      case _ => Bot
    }

    // concretization function
    def gamma: concrete.Set[RefValue] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[RefValue] = Many
  }
}
