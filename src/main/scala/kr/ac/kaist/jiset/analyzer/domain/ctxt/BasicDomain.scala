package kr.ac.kaist.jiset.analyzer.domain.ctxt

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

object BasicDomain extends ctxt.Domain {
  // abstraction function
  def alpha(ctxt: Ctxt): Elem =
    Elem(AbsEnv(ctxt.globals), AbsEnv(ctxt.locals), AbsValue(ctxt.retVal))

  // bottom value
  val Bot: Elem = Elem(AbsEnv.Bot, AbsEnv.Bot, AbsValue.Bot)

  // top value
  val Top: Elem = Elem(AbsEnv.Top, AbsEnv.Top, AbsValue.Top)

  // empty value
  val Empty: Elem = Elem(AbsEnv.Empty, AbsEnv.Empty, AbsAbsent.Top)

  case class Elem(
    globals: AbsEnv,
    locals: AbsEnv,
    retVal: AbsValue
  ) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (
      this.globals ⊑ that.globals &&
      this.locals ⊑ that.locals &&
      this.retVal ⊑ that.retVal
    )

    // join operator
    def ⊔(that: Elem): Elem = Elem(
      this.globals ⊔ that.globals,
      this.locals ⊔ that.locals,
      this.retVal ⊔ that.retVal
    )

    // meet operator
    def ⊓(that: Elem): Elem = Elem(
      this.globals ⊓ that.globals,
      this.locals ⊓ that.locals,
      this.retVal ⊓ that.retVal
    ).normalized

    // concretization function
    def gamma: concrete.Set[Ctxt] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[Ctxt] = Many
  }
}
