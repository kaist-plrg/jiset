package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// basic abstract return values and heaps
object BasicRet extends Domain {
  lazy val Bot = Elem(
    value = AbsValue.Bot,
    heap = AbsHeap.Bot,
  )

  // constructors
  def apply(
    value: AbsValue = AbsValue.Bot,
    heap: AbsHeap = AbsHeap.Bot
  ): Elem = Elem(value, heap)

  // extractors
  def unapply(elem: Elem) = Some((
    elem.value,
    elem.heap,
  ))

  // appender
  implicit val app: App[Elem] = (app, elem) => {
    app >> elem.value >> " @ " >> elem.heap
  }

  // elements
  case class Elem(
    value: AbsValue,
    heap: AbsHeap
  ) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (
      this.value ⊑ that.value &&
      this.heap ⊑ that.heap
    )

    // join operator
    def ⊔(that: Elem): Elem = Elem(
      this.value ⊔ that.value,
      this.heap ⊔ that.heap,
    )
  }
}
