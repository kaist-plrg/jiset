package kr.ac.kaist.jiset.analyzer.domain.obj

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

object BasicDomain extends obj.Domain {
  // map domain
  val MapD = combinator.MapDomain[String, Value, AbsValue.type](AbsValue)
  type MapD = MapD.Elem

  // list domain
  val ListD = combinator.ListDomain[Value, AbsValue.type](AbsValue)
  type ListD = ListD.Elem

  // abstraction function
  def alpha(obj: Obj): Elem = obj match {
    case SymbolObj(desc) => Elem(symbol = AbsStr(desc))
    case MapObj(props) => Elem(map = MapD(props))
    case ListObj(values) => Elem(list = ListD(values))
  }

  // bottom value
  val Bot: Elem = Elem()

  // top value
  val Top: Elem = Elem(AbsStr.Top, MapD.Top, ListD.Top)

  case class Elem(
    symbol: AbsStr = AbsStr.Bot,
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
