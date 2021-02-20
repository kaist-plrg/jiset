package kr.ac.kaist.jiset.analyzer.domain.state

import kr.ac.kaist.jiset.analyzer.State
import kr.ac.kaist.jiset.analyzer.domain._

object BasicDomain extends state.Domain {
  // abstraction function
  def alpha(st: State): Elem =
    Elem(AbsEnv(st.env), AbsHeap(st.heap), AbsValue(st.retVal))

  // bottom value
  val Bot: Elem = Elem(AbsEnv.Bot, AbsHeap.Bot, AbsValue.Bot)

  // top value
  val Top: Elem = Elem(AbsEnv.Top, AbsHeap.Top, AbsValue.Top)

  // empty value
  val Empty: Elem = Elem(AbsEnv.Empty, AbsHeap.Empty, AbsAbsent.Top)

  case class Elem(
    env: AbsEnv = AbsEnv.Bot,
    heap: AbsHeap = AbsHeap.Bot,
    retVal: AbsValue = AbsValue.Bot
  ) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (
      this.env ⊑ that.env &&
      this.heap ⊑ that.heap &&
      this.retVal ⊑ that.retVal
    )

    // join operator
    def ⊔(that: Elem): Elem = if (this eq that) this else Elem(
      this.env ⊔ that.env,
      this.heap ⊔ that.heap,
      this.retVal ⊔ that.retVal
    )

    // meet operator
    def ⊓(that: Elem): Elem = if (this eq that) this else Elem(
      this.env ⊓ that.env,
      this.heap ⊓ that.heap,
      this.retVal ⊓ that.retVal
    ).normalized

    // concretization function
    def gamma: concrete.Set[State] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[State] = Many

    // return value
    def doReturn(value: AbsValue): Elem = copy(retVal = value)

    // define variable
    def define(x: String, value: AbsValue): Elem = copy(env = env.define(x, value))
  }
}
