package kr.ac.kaist.jiset.analyzer.domain.obj

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object BasicDomain extends obj.Domain {
  // symbol domain
  val SymbolD = generator.SetDomain[String]()
  type SymbolD = SymbolD.Elem

  // map domain
  val MapD = combinator.PMapDomain[String, Value, AbsValue.type](AbsValue)
  type MapD = MapD.Elem

  // list domain
  val ListD = combinator.ListDomain[Value, AbsValue.type](AbsValue)
  type ListD = ListD.Elem

  // abstraction function
  def alpha(obj: Obj): Elem = obj match {
    case SymbolObj(desc) => Elem(symbol = SymbolD(desc))
    case MapObj(_, props) => Elem(map = MapD(props))
    case ListObj(values) => Elem(list = ListD(values))
  }

  // bottom value
  val Bot: Elem = Elem()

  // top value
  val Top: Elem = Elem(SymbolD.Top, MapD.Top, ListD.Top)

  case class Elem(
    symbol: SymbolD = SymbolD.Bot,
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

    // TODO handling lists
    // lookup
    def apply(prop: AbsStr): (AbsValue, AbsAbsent) =
      prop.gamma.map(s => map(s.str)) match {
        case Infinite => (AbsValue.Top, AbsAbsent.Top)
        case Finite(set) =>
          val vopt = set.foldLeft[MapD.AbsVOpt](MapD.AbsVOpt.Bot)(_ ⊔ _)
          (vopt.value, vopt.absent)
      }
  }
}
