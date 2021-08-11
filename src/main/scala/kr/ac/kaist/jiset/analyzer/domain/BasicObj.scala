package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.parser.ESValueParser
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// basic abstract objects
object BasicObj extends Domain {
  case object Bot extends Elem
  case class SymbolElem(desc: AbsValue) extends Elem
  case class MergedMapElem(ty: Ty, prop: AbsValue, value: AbsValue) extends Elem
  case class MapElem(ty: Ty, map: Map[AValue, AbsValue], props: Vector[AValue]) extends Elem {
    def mergedProp: AbsValue =
      map.keySet.map(AbsValue(_)).foldLeft(AbsValue.Bot)(_ ⊔ _)
    def sortedProps(intSorted: Boolean): Vector[AValue] = if (intSorted) {
      (for {
        ASimple(Str(s)) <- props
        d = ESValueParser.str2num(s)
        if toStringHelper(d) == s
        i = d.toInt
        if d == i
      } yield (s, i))
        .sortBy(_._2)
        .map { case (s: String, _) => ASimple(Str(s)) }
    } else if (ty.name == "SubMap") props
    else props.sortBy(_.toString)
  }
  case class MergedListElem(value: AbsValue) extends Elem
  case class ListElem(values: Vector[AbsValue]) extends Elem
  case class NotSupportedElem(ty: Ty, desc: String) extends Elem

  // abstraction functions
  def apply(obj: Obj): Elem = obj match {
    case IRSymbol(desc) => SymbolElem(AbsValue(desc))
    case IRMap(ty, props, size) => MapElem(ty, (props.toList.map {
      case (k, (v, _)) => AValue.from(k) -> AbsValue(v)
    }).toMap, props.toVector.sortBy(_._2._2).map(_._1).map(AValue.from))
    case IRList(values) => ListElem(values.map(AbsValue(_)))
    case IRNotSupported(tyname, desc) => NotSupportedElem(Ty(tyname), desc)
  }

  // appender
  implicit val app: App[Elem] = (app, elem) => elem match {
    case Bot => app >> "⊥"
    case SymbolElem(desc) => app >> "'" >> desc.toString
    case MergedMapElem(ty, prop, value) =>
      app >> s"$ty "
      app >> "{{" >> prop.toString >> " -> " >> value.toString >> "}}"
    case MapElem(ty, map, props) =>
      app >> s"$ty "
      if (map.isEmpty) app >> "{}"
      else app.wrap {
        for (k <- props) app :> s"$k -> " >> map(k) >> LINE_SEP
      }
    case MergedListElem(value) =>
      app >> "[[" >> value.toString >> "]]"
    case ListElem(values) =>
      app >> values.mkString("[", ", ", "]")
    case NotSupportedElem(ty, desc) =>
      app >> s"???[$ty](" >> desc >> ")"
  }

  // elements
  sealed trait Elem extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) => true
      case (_, Bot) => false
      case (SymbolElem(ldesc), SymbolElem(rdesc)) => ldesc ⊑ rdesc
      case (MergedMapElem(lty, lp, lv), MergedMapElem(rty, rp, rv)) =>
        lty == rty && lp ⊑ rp && lv ⊑ rv
      case (MapElem(lty, lmap, lprops), MapElem(rty, rmap, rprops)) => (
        lty == rty &&
        lprops == rprops &&
        lprops.forall(x => this(x) ⊑ that(x))
      )
      case (lmap @ MapElem(lty, _, _), MergedMapElem(rty, rprop, rvalue)) =>
        lty == rty && lmap.mergedProp ⊑ rprop && lmap.mergedValue ⊑ rvalue
      case (MergedListElem(lv), MergedListElem(rv)) =>
        lv ⊑ rv
      case (ListElem(lvs), ListElem(rvs)) =>
        lvs.length == rvs.length && (lvs zip rvs).forall { case (l, r) => l ⊑ r }
      case (ListElem(_), MergedListElem(rv)) =>
        this.mergedValue ⊑ rv
      case (NotSupportedElem(lty, ld), NotSupportedElem(rty, rd)) =>
        lty == rty && ld == rd
      case _ => false
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) => that
      case (_, Bot) => this
      case _ if this ⊑ that => that
      case _ if that ⊑ this => this
      case (SymbolElem(ldesc), SymbolElem(rdesc)) => SymbolElem(ldesc ⊔ rdesc)
      case (MergedMapElem(lty, lp, lv), MergedMapElem(rty, rp, rv)) if lty == rty =>
        MergedMapElem(lty, lp ⊔ rp, lv ⊔ rv)
      case (l @ MapElem(lty, lmap, lprops), r @ MapElem(rty, rmap, rprops)) if lty == rty =>
        if (lprops == rprops) MapElem(lty, (lmap.keySet ++ rmap.keySet).toList.map(x => {
          x -> this(x) ⊔ that(x)
        }).toMap, lprops)
        else MergedMapElem(lty, l.mergedProp ⊔ r.mergedProp, l.mergedValue ⊔ r.mergedValue)
      case (lmap @ MapElem(lty, _, _), MergedMapElem(rty, rp, rv)) if lty == rty =>
        MergedMapElem(lty, lmap.mergedProp ⊔ rp, lmap.mergedValue ⊔ rv)
      case (MergedListElem(lv), MergedListElem(rv)) =>
        MergedListElem(lv ⊔ rv)
      case (ListElem(lvs), ListElem(rvs)) => if (lvs.length == rvs.length) {
        ListElem((lvs zip rvs).map { case (l, r) => l ⊔ r })
      } else MergedListElem(this.mergedValue ⊔ that.mergedValue)
      case (ListElem(lvs), MergedListElem(rv)) =>
        MergedListElem(this.mergedValue ⊔ rv)
      case (NotSupportedElem(lty, ld), NotSupportedElem(rty, rd)) if lty == rty && ld == rd => this
      case _ =>
        error(s"cannot merge: ${this.getTy} with ${that.getTy}")
    }

    // lookup
    def apply(key: AValue): AbsValue = this match {
      case Bot => AbsValue.Bot
      case SymbolElem(desc) => key match {
        case ASimple(Str("Description")) => desc
        case _ => AbsValue.Bot
      }
      case MergedMapElem(_, prop, value) =>
        if (AbsValue(key) ⊑ prop) value
        else AbsValue.absent
      case MapElem(_, map, _) => map.getOrElse(key, AbsValue.absent)
      case MergedListElem(value) => value
      case ListElem(values) => key match {
        case ASimple(INum(long)) =>
          val idx = long.toInt
          if (0 <= idx && idx < values.length) values(idx)
          else AbsValue.absent
        case ASimple(Str("length")) => AbsValue(values.length)
        case _ => AbsValue.Bot
      }
      case NotSupportedElem(_, desc) => AbsValue.Bot
    }

    // get type
    def getTy: Ty = this match {
      case Bot =>
        warn("try to read type of bottom object."); Ty("")
      case SymbolElem(desc) => Ty("Symbol")
      case MergedMapElem(ty, _, _) => ty
      case MapElem(ty, _, _) => ty
      case MergedListElem(value) => Ty("List")
      case ListElem(values) => Ty("List")
      case NotSupportedElem(ty, desc) => ty
    }

    // abstract lookup
    def apply(akey: AbsValue): AbsValue = akey.getSingle match {
      case FlatBot => AbsValue.Bot
      case FlatTop => mergedValue
      case FlatElem(key) => this(key)
    }

    // singleton checks
    def isSingle: Boolean = this match {
      case SymbolElem(desc) => desc.isSingle
      case MapElem(_, map, _) => map.forall(_._2.isSingle)
      case ListElem(values) => values.forall(_.isSingle)
      case NotSupportedElem(_, desc) => true
      case _ => false
    }

    // get reachable locations
    def reachableLocs: Set[Loc] = this match {
      case SymbolElem(desc) => desc.loc.toSet
      case MergedMapElem(_, prop, value) => prop.loc.toSet ++ value.loc.toSet
      case MapElem(_, map, props) => map.foldLeft(Set[Loc]()) {
        case (set, (k, v)) => set ++ v.loc.toSet ++ (k match {
          case loc: Loc => Some(loc)
          case _ => None
        })
      }
      case MergedListElem(value) => value.loc.toSet
      case ListElem(values) => values.foldLeft(Set[Loc]())(_ ++ _.loc.toSet)
      case _ => Set()
    }

    // merged value of all possible values
    lazy val mergedValue: AbsValue = this match {
      case Bot => AbsValue.Bot
      case SymbolElem(desc) => desc
      case MergedMapElem(_, _, value) => value
      case MapElem(_, map, _) => map.foldLeft[AbsValue](AbsValue.Bot) {
        case (lv, (_, rv)) => lv ⊔ rv
      }
      case MergedListElem(value) => value
      case ListElem(values) => values.foldLeft[AbsValue](AbsValue.Bot)(_ ⊔ _)
      case NotSupportedElem(_, desc) => AbsValue.Bot
    }

    // updates
    def update(prop: AbsValue, value: AbsValue, weak: Boolean): Elem = {
      def aux(key: AValue): MapUpdater = map => MapElem(
        map.ty,
        map.map + (key -> value),
        if (map.map contains key) map.props else map.props :+ key
      )
      def mergedAux(map: MergedMapElem): MergedMapElem = MergedMapElem(
        map.ty,
        map.prop ⊔ prop,
        map.value ⊔ value,
      )
      modifyMap(prop, aux, mergedAux, aux, mergedAux, weak)
    }

    // delete
    def delete(prop: AbsValue, weak: Boolean): Elem = {
      def aux(key: AValue): MapUpdater = map => MapElem(
        map.ty,
        map.map - key,
        if (map.map contains key) map.props.filter(_ != key) else map.props
      )
      def mergedAux(map: MergedMapElem): MergedMapElem = map
      modifyMap(prop, aux, mergedAux, aux, mergedAux, weak)
    }

    // helper for map structures
    type MapUpdater = MapElem => MapElem
    private def modifyMap(
      prop: AbsValue,
      jsF: AValue => MapUpdater,
      jsMergedF: MergedMapElem => MergedMapElem,
      f: AValue => MapUpdater,
      mergedF: MergedMapElem => MergedMapElem,
      weak: Boolean
    ): Elem = this match {
      // for JavaScript
      case map @ MergedMapElem(ty @ Ty("SubMap"), _, _) =>
        jsMergedF(map)
      case map @ MapElem(ty @ Ty("SubMap"), _, _) =>
        prop.keyValue.getSingle match {
          case FlatBot => this
          case FlatElem(key) if !weak => jsF(key)(map)
          case _ => jsMergedF(MergedMapElem(
            ty,
            map.mergedProp,
            map.mergedValue,
          ))
        }
      // for IR
      case map @ MergedMapElem(ty, _, _) =>
        mergedF(map)
      case map @ MapElem(ty, _, _) =>
        prop.keyValue.getSingle match {
          case FlatBot => this
          case FlatElem(key) if !weak => f(key)(map)
          case _ => mergedF(MergedMapElem(
            ty,
            map.mergedProp,
            map.mergedValue,
          ))
        }
      case _ => this
    }

    // appends
    def append(value: AbsValue, weak: Boolean): Elem =
      modifyList(_ :+ value, _ ⊔ value, weak)

    // prepends
    def prepend(value: AbsValue, weak: Boolean): Elem =
      modifyList(value +: _, _ ⊔ value, weak)

    // pops
    def pop(idx: AbsValue, weak: Boolean): (AbsValue, Elem) = {
      idx.int.getSingle match {
        case FlatElem(INum(long)) => {
          val k = long.toInt
          var v: AbsValue = AbsValue.Bot
          val newObj = modifyList(
            vs => {
              if (0 <= k && k < vs.length) {
                v = vs(k); vs.slice(0, k) ++ vs.slice(k + 1, vs.length)
              } else {
                v = AbsValue.absent; vs
              }
            },
            mv => { v = mv; mv },
            weak
          )
          (v, newObj)
        }
        case _ => (mergedValue, MergedListElem(mergedValue))
      }
    }

    // helper for map structures
    type ListUpdater = Vector[AbsValue] => Vector[AbsValue]
    private def modifyList(
      f: ListUpdater,
      mergedF: AbsValue => AbsValue,
      weak: Boolean
    ): Elem = this match {
      case MergedListElem(value) => MergedListElem(mergedF(value))
      case ListElem(values) =>
        if (weak) MergedListElem(mergedF(mergedValue))
        else ListElem(f(values))
      case _ => this
    }

    // keys of map
    def keys(intSorted: Boolean): Elem = this match {
      case MergedMapElem(_, prop, _) => MergedListElem(prop)
      case map: MapElem => ListElem(map.sortedProps(intSorted).map(AbsValue(_)))
      case _ => Bot
    }

    // set type of objects
    def setType(ty: Ty): Elem = this match {
      case MergedMapElem(_, prop, value) => MergedMapElem(ty, prop, value)
      case MapElem(_, map, props) => MapElem(ty, map, props)
      case _ => error("cannot set type of non-map abstract objects.")
    }

    // check contains
    def contains(value: AbsValue): AbsBool = (this, value.getSingle) match {
      case (Bot, _) | (_, FlatBot) => AbsBool.Bot
      case (ListElem(values), FlatElem(_)) =>
        if (values contains value) AT
        else if (values.forall(v => (v ⊓ value).isBottom)) AF
        else AB
      case (MergedListElem(mergedValue), _) =>
        if ((mergedValue ⊓ value).isBottom) AF
        else AB
      case _ => AbsBool.Bot
    }
  }
}
