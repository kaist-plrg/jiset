package kr.ac.kaist.jiset.analyzer.domain.obj

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object BasicDomain extends obj.Domain {
  // map domain
  val MapD = combinator.PMapDomain[String, Value, AbsValue.type](AbsValue)
  type MapD = MapD.Elem

  // list domain
  val ListD = combinator.ListDomain[Value, AbsValue.type](AbsValue)
  type ListD = ListD.Elem

  // abstraction function
  def alpha(obj: Obj): Elem = obj match {
    case SymbolObj(desc) => SymbolElem(desc)
    case MapObj(Ty(name), props) => MapElem(parent = Some(name), map = MapD(props))
    case ListObj(values) => ListElem(list = ListD(values))
  }

  // bottom value
  object Bot extends Elem

  // top value
  object Top extends Elem

  // symbol objects
  case class SymbolElem(desc: String) extends Elem

  // map objects
  case class MapElem(parent: Option[String], map: MapD) extends Elem

  // list objects
  case class ListElem(list: ListD) extends Elem

  trait Elem extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) | (_, Top) => true
      case (Top, _) | (_, Bot) => false
      case (SymbolElem(l), SymbolElem(r)) => l == r
      case (MapElem(lparent, lmap), MapElem(rparent, rmap)) =>
        lparent == rparent & lmap ⊑ rmap
      case (ListElem(l), ListElem(r)) => l ⊑ r
      case _ => false
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Top) => that
      case (Top, _) | (_, Bot) => this
      case (SymbolElem(l), SymbolElem(r)) if l == r => this
      case (MapElem(lp, lmap), MapElem(rp, rmap)) if lp == rp =>
        MapElem(lp, lmap ⊔ rmap)
      case (ListElem(l), ListElem(r)) => ListElem(l ⊔ r)
      case _ => Top
    }

    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Top) => this
      case (Top, _) | (_, Bot) => that
      case (SymbolElem(l), SymbolElem(r)) if l == r => this
      case (MapElem(lp, lmap), MapElem(rp, rmap)) if lp == rp =>
        MapElem(lp, lmap ⊓ rmap)
      case (ListElem(l), ListElem(r)) => ListElem(l ⊓ r)
      case _ => Bot
    }

    // concretization function
    def gamma: concrete.Set[Obj] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[Obj] = Many

    // TODO handling lists
    // lookup
    def apply(prop: AbsStr): (AbsValue, AbsAbsent) = this match {
      case MapElem(_, map) => prop.gamma.map(s => map(s.str)) match {
        case Infinite => (AbsValue.Top, AbsAbsent.Top)
        case Finite(set) =>
          val vopt = set.foldLeft[MapD.AbsVOpt](MapD.AbsVOpt.Bot)(_ ⊔ _)
          (vopt.value, vopt.absent)
      }
      case _ => (AbsValue.Bot, AbsAbsent.Bot)
    }
  }
}
