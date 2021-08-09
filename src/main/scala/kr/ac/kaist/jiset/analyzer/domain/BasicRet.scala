package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// basic abstract return values and states
object BasicRet extends Domain {
  lazy val Bot = Elem(
    value = AbsValue.Bot,
    state = AbsState.Bot,
  )

  // constructors
  def apply(
    value: AbsValue = AbsValue.Bot,
    state: AbsState = AbsState.Bot
  ): Elem = Elem(value, state)

  // extractors
  def unapply(elem: Elem) = Some((
    elem.value,
    elem.state,
  ))

  // appender
  implicit val app: App[Elem] = (app, elem) => {
    app >> elem.value >> " @ " >> elem.state
  }

  // elements
  case class Elem(
    value: AbsValue,
    state: AbsState
  ) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (
      this.value ⊑ that.value &&
      this.state ⊑ that.state
    )

    // join operator
    def ⊔(that: Elem): Elem = Elem(
      this.value ⊔ that.value,
      this.state ⊔ that.state,
    )
  }
}
