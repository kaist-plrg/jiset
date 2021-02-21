package kr.ac.kaist.jiset.analyzer.domain.state

import kr.ac.kaist.jiset.analyzer.State
import kr.ac.kaist.jiset.analyzer.domain._

object BasicDomain extends state.Domain {
  // abstraction function
  def alpha(st: State): Elem = Elem(AbsEnv(st.env), AbsHeap(st.heap))

  // bottom value
  val Bot: Elem = Elem(AbsEnv.Bot, AbsHeap.Bot)

  // top value
  val Top: Elem = Elem(AbsEnv.Top, AbsHeap.Top)

  // empty value
  val Empty: Elem = Elem(AbsEnv.Empty, AbsHeap.Empty)

  case class Elem(
    env: AbsEnv = AbsEnv.Bot,
    heap: AbsHeap = AbsHeap.Bot
  ) extends ElemTrait {
    // bottom check
    override def isBottom: Boolean = (this eq Bot) || (this == Bot)

    // partial order
    def ⊑(that: Elem): Boolean = (
      (this eq that) ||
      this.isBottom ||
      !that.isBottom && (
        this.env ⊑ that.env &&
        this.heap ⊑ that.heap
      )
    )

    // join operator
    def ⊔(that: Elem): Elem = if (this eq that) this else Elem(
      this.env ⊔ that.env,
      this.heap ⊔ that.heap
    )

    // meet operator
    def ⊓(that: Elem): Elem = if (this eq that) this else Elem(
      this.env ⊓ that.env,
      this.heap ⊓ that.heap
    ).normalized

    // concretization function
    def gamma: concrete.Set[State] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[State] = Many

    // define variable
    def +(pair: (String, AbsValue)): Elem = copy(env = env + pair)
  }
}
