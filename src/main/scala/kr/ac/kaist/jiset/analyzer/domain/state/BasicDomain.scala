package kr.ac.kaist.jiset.analyzer.domain.state

import kr.ac.kaist.jiset.ir.State
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

    // update references
    def update(globals: Map[String, AbsValue], refv: AbsRefValue, v: AbsValue): Elem = ???

    // update references
    def delete(globals: Map[String, AbsValue], refv: AbsRefValue): Elem = ???

    // lookup reference values
    def apply(globals: Map[String, AbsValue], refv: AbsRefValue): AbsValue =
      refv.toValue(this, globals)

    // allocate a new symbol
    def allocSymbol(desc: String): (Elem, AbsValue) = ???

    // allocate a new map
    def allocMap(props: Map[String, AbsValue]): (Elem, AbsValue) = ???

    // allocate a new list
    def allocList(vs: List[AbsValue]): (Elem, AbsValue) = ???

    // append an element to a list
    def append(v: AbsValue, addr: AbsAddr): Elem = ???

    // prepend an element to a list
    def prepend(v: AbsValue, addr: AbsAddr): Elem = ???

    // copy an object
    def copyOf(v: AbsValue): (Elem, AbsValue) = ???

    // get keys of an object
    def keysOf(v: AbsValue): (Elem, AbsValue) = ???

    // pop a value from a list
    def pop(list: AbsValue, idx: AbsValue): (Elem, AbsValue) = ???

    // get type of values
    def typeOf(v: AbsValue): AbsValue = ???

    // check whether lists contains elements
    def contains(list: AbsValue, v: AbsValue): AbsValue = ???
  }
}
