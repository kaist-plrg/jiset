package kr.ac.kaist.jiset.analyzer.domain.refvalue

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.analyzer.domain.generator._

object BasicDomain extends refvalue.Domain {
  // abstraction function
  def alpha(refVal: RefValue): Elem = refVal match {
    case RefValueId(name) => Id(name)
    case RefValueProp(addr, name) => ObjProp(AbsAddr(addr), AbsStr(name))
    case RefValueString(str, name) => StrProp(AbsStr(str), AbsStr(name))
  }

  // constructor for abstract value bases
  def apply(base: AbsValue, prop: AbsStr): AbsRefValue = {
    var refv: Elem = Bot
    if (!base.addr.isBottom) refv ⊔= ObjProp(base.addr, prop)
    if (!base.str.isBottom) refv ⊔= StrProp(base.str, prop)
    refv
  }

  // bottom value
  object Bot extends Elem

  // top value
  object Top extends Elem

  // id reference values
  case class Id(name: String) extends Elem

  // object reference values
  case class ObjProp(addr: AbsAddr, prop: AbsStr) extends Elem

  // string reference values
  case class StrProp(str: AbsStr, prop: AbsStr) extends Elem

  trait Elem extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) | (_, Top) => true
      case (Id(lname), Id(rname)) => lname == rname
      case (ObjProp(la, lp), ObjProp(ra, rp)) => la ⊑ ra && lp ⊑ rp
      case (StrProp(ls, lp), StrProp(rs, rp)) => ls ⊑ rs && lp ⊑ rp
      case _ => false
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Top) => that
      case (_, Bot) | (Top, _) => this
      case (Id(lname), Id(rname)) if lname == rname => this
      case (ObjProp(la, lp), ObjProp(ra, rp)) => ObjProp(la ⊔ ra, lp ⊔ rp)
      case (StrProp(ls, lp), StrProp(rs, rp)) => StrProp(ls ⊔ rs, lp ⊔ rp)
      case _ => Top
    }

    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Top) => this
      case (_, Bot) | (Top, _) => that
      case (Id(lname), Id(rname)) if lname == rname => this
      case (ObjProp(la, lp), ObjProp(ra, rp)) => ObjProp(la ⊓ ra, lp ⊓ rp)
      case (StrProp(ls, lp), StrProp(rs, rp)) => StrProp(ls ⊓ rs, lp ⊓ rp)
      case _ => Bot
    }

    // concretization function
    def gamma: concrete.Set[RefValue] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[RefValue] = Many

    // conversion to abstract values with abstract states
    def toValue(st: AbsState, sem: AbsSemantics): AbsValue = this match {
      case Bot => AbsValue.Bot
      case Top => AbsValue.Top
      case Id(x) =>
        val (localV, absent) = st.env(x)
        val globalV: AbsValue = if (absent.isTop) sem.globalVars.getOrElse(x, {
          alarm(s"unknown variable: $x")
          AbsAbsent.Top
        })
        else AbsValue.Bot
        localV ⊔ globalV
      case ObjProp(addr, prop) => addr.toSet.toList.map {
        case (addr @ NamedAddr(x)) =>
          val obj = sem.globalHeaps.getOrElse(addr, AbsObj.Bot)
          val (v, a) = obj(prop) // XXX ignore absent values
          if (a.isTop) alarm(s"unknown property: #$x[${beautify(prop)}]")
          v
        case DynamicAddr(k) => ???
      }.foldLeft[AbsValue](AbsValue.Bot)(_ ⊔ _)
      case StrProp(str, prop) => ???
    }
  }
}
