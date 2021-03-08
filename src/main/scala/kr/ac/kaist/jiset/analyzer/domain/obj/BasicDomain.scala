package kr.ac.kaist.jiset.analyzer.domain.obj

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.util.Useful._

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

    // TODO prune
    def prune(v: Obj): Elem = this

    // concretization function
    def gamma: concrete.Set[Obj] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[Obj] = Many

    // TODO handling lists
    // lookup
    def apply(sem: AbsSemantics, prop: AbsStr): AbsValue = this match {
      case MapElem(ty, map) => prop.gamma.map(s => map(s.str)) match {
        case Finite(set) =>
          val vopt = set.foldLeft[MapD.AbsVOpt](MapD.AbsVOpt.Bot)(_ ⊔ _)
          val typeV = if (vopt.absent.isTop) {
            val typeV = ty.fold(AbsValue.Bot)(sem.lookup(_, prop))
            if (typeV.isBottom) alarm(s"unknown property: ${beautify(prop)} @ ${beautify(this)}")
            typeV
          } else AbsValue.Bot
          vopt.value ⊔ typeV
        case Infinite => ???
      }
      case ListElem(list) => prop.getSingle match {
        case One(Str("length")) => list.length
        case _ => ???
      }
      case _ => ???
    }

    // update
    def +(pair: (String, AbsValue)): AbsObj = update(pair._1, pair._2)
    def update(prop: String, value: AbsValue): AbsObj = this match {
      case MapElem(ty, map) => MapElem(ty, map + (prop -> value))
      case _ => ???
    }
  }
}
