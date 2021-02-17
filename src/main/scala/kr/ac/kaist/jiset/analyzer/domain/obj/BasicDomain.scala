package kr.ac.kaist.jiset.analyzer.domain.obj

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object BasicDomain extends obj.Domain {
  // map domain
  val MapD = combinator.MapDomain[Value, Value, AbsValue.type](AbsValue)
  type MapD = MapD.Elem

  // list domain
  val ListD = combinator.ListDomain[Value, AbsValue.type](AbsValue)
  type ListD = ListD.Elem

  // abstraction function
  def alpha(obj: Obj): Elem = obj match {
    case IRSymbol(v) => Elem(symbol = AbsValue(v))
    case IRMap(_, props, _) =>
      Elem(map = MapD(props.map { case (k, (v, _)) => k -> v }))
    case IRList(values) => Elem(list = ListD(values.toList))
    case _ => Bot
  }

  // bottom value
  val Bot: Elem = Elem()

  // top value
  val Top: Elem = Elem(AbsValue.Top, MapD.Top, ListD.Top)

  case class Elem(
      symbol: AbsValue = AbsValue.Bot,
      map: MapD = MapD.Bot,
      list: ListD = ListD.Bot
  ) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (
      this.symbol ⊑ that.symbol &&
      this.map ⊑ that.map &&
      this.list ⊑ that.list
    )

    // join operator
    def ⊔(that: Elem): Elem = Elem(
      this.symbol ⊔ that.symbol,
      this.map ⊔ that.map,
      this.list ⊔ that.list
    )

    // meet operator
    def ⊓(that: Elem): Elem = Elem(
      this.symbol ⊓ that.symbol,
      this.map ⊓ that.map,
      this.list ⊓ that.list
    )

    // concretization function
    def gamma: concrete.Set[Obj] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[Obj] = Many
  }
}
