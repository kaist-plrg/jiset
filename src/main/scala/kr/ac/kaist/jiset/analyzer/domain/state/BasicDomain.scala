package kr.ac.kaist.jiset.analyzer.domain.state

import kr.ac.kaist.jiset.analyzer.State
import kr.ac.kaist.jiset.analyzer.domain._

object BasicDomain extends state.Domain {
  // abstraction function
  def alpha(st: State): Elem = Elem(AbsCtxt(st.ctxt), AbsHeap(st.heap))

  // bottom value
  val Bot: Elem = Elem(AbsCtxt.Bot, AbsHeap.Bot)

  // top value
  val Top: Elem = Elem(AbsCtxt.Top, AbsHeap.Top)

  // empty value
  val Empty: Elem = Elem(AbsCtxt.Empty, AbsHeap.Empty)

  case class Elem(ctxt: AbsCtxt, heap: AbsHeap) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (
      this.ctxt ⊑ that.ctxt &&
      this.heap ⊑ that.heap
    )

    // join operator
    def ⊔(that: Elem): Elem = Elem(
      this.ctxt ⊔ that.ctxt,
      this.heap ⊔ that.heap
    )

    // meet operator
    def ⊓(that: Elem): Elem = Elem(
      this.ctxt ⊓ that.ctxt,
      this.heap ⊓ that.heap
    ).normalized

    // concretization function
    def gamma: concrete.Set[State] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[State] = Many
  }
}
