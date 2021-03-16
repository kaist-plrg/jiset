package kr.ac.kaist.jiset.analyzer.domain.state

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.analyzer.domain.obj._

object BasicDomain extends state.Domain {
  // abstraction function
  def alpha(st: State): Elem = Elem(AbsEnv(st.env), AbsHeap(st.heap))

  // bottom value
  val Bot: Elem = Elem(AbsEnv.Bot, AbsHeap.Bot)

  // top value
  val Top: Elem = Elem(AbsEnv.Top, AbsHeap.Top)

  // empty value
  val Empty: Elem = Elem(AbsEnv.Empty, AbsHeap.Empty)

  // constructor
  def apply(env: AbsEnv = AbsEnv.Bot, heap: AbsHeap = AbsHeap.Bot): Elem =
    Elem(env, heap)

  // extractor
  def unapply(elem: Elem): Option[(AbsEnv, AbsHeap)] = Some((elem.env, elem.heap))

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

    private def checkBot(st: => Elem): Elem = if (isBottom) this else st

    // define variable
    def +(pair: (String, AbsValue)): Elem = checkBot(copy(env = env + pair))

    // update references
    def update(sem: AbsSemantics, refv: AbsRefValue, v: AbsValue): Elem =
      checkBot(refv match {
        case AbsRefValue.Id(x) =>
          val (localV, absent) = env(x)
          if (!localV.isBottom) {
            if (absent.isTop) alarm(s"unknown local variable: $x")
            this + (x -> v)
          } else if (absent.isTop) sem.globalEnv.get(x) match {
            case Some(globalV) =>
              if (!(v ⊑ globalV))
                alarm(s"wrong update of global variable $x with ${beautify(v)}")
              this
            case None =>
              alarm(s"unknown global variable: $x")
              this
          }
          else Bot
        case AbsRefValue.Prop(base, prop) =>
          copy(heap = base.escaped.addr.toSet.foldLeft(heap) {
            case (h, a: DynamicAddr) =>
              val obj = update(heap(a), prop, v)
              h + (a -> obj)
            case _ => ???
          })
        case _ => ???
      })

    // update
    def update(obj: AbsObj, prop: AbsPure, value: AbsValue): AbsObj = {
      import AbsObj._
      obj match {
        case MapElem(Some("SubMap"), _) => obj // TODO to be more precise
        case MapElem(ty, map) => prop.getSingle match {
          case Zero => AbsObj.Bot
          case One(Str(p)) => MapElem(ty, map + (p -> value))
          case _ => obj // TODO unsound
        }
        case _ => ???
      }
    }

    // update references
    def delete(sem: AbsSemantics, refv: AbsRefValue): Elem = checkBot(???)

    // lookup helper
    def lookup(sem: AbsSemantics, base: String, props: String*): AbsValue = {
      val baseV = lookupVariable(sem, base)
      props.foldLeft(baseV) {
        case (v, prop) =>
          val pureV = v.escaped
          lookupProp(sem, pureV, AbsPure(prop))
      }
    }

    // lookup reference values
    def lookup(sem: AbsSemantics, refv: AbsRefValue): AbsValue = refv match {
      case AbsRefValue.Bot => AbsValue.Bot
      case AbsRefValue.Top => AbsValue.Top
      case AbsRefValue.Id(x) => lookupVariable(sem, x)
      case AbsRefValue.Prop(base, prop) => lookupProp(sem, base, prop)
    }
    private def lookupVariable(sem: AbsSemantics, x: String): AbsValue = {
      val (localV, absent) = env(x)
      val globalV: AbsValue = if (absent.isTop) sem.globalEnv.getOrElse(x, {
        alarm(s"unknown variable: $x")
        AbsAbsent.Top
      })
      else AbsValue.Bot
      localV ⊔ globalV
    }
    private def lookupProp(
      sem: AbsSemantics,
      base: AbsValue,
      prop: AbsPure
    ): AbsValue = {
      var v = AbsValue.Bot
      val pure = base.escaped
      for (ty <- pure.ty) v ⊔= lookup(sem, ty.name, prop.str)
      for (addr <- pure.addr) v ⊔= lookup(sem, addr, prop)
      v ⊔= lookup(pure.str, prop)
      v
    }

    // lookup strings
    def lookup(str: AbsStr, prop: AbsPure): AbsValue = {
      var v = AbsValue.Bot
      if (!prop.int.isBottom) v ⊔= AbsStr.Top
      if (AbsValue("length") ⊑ prop.str) v ⊔= AbsINum.Top
      v
    }

    // lookup objects
    def lookup(sem: AbsSemantics, obj: AbsObj, prop: AbsPure): AbsValue = {
      import AbsObj._
      obj match {
        case MapElem(Some("SubMap"), _) => AbsAbsent.Top // TODO
        case MapElem(ty, map) => prop.str.gamma.map(s => map(s.str)) match {
          case Finite(set) =>
            val vopt = set.foldLeft[MapD.AbsVOpt](MapD.AbsVOpt.Bot)(_ ⊔ _)
            val typeV = if (vopt.absent.isTop) {
              val typeV = ty.fold(AbsValue.Bot)(lookup(sem, _, prop.str))
              if (typeV.isBottom) alarm(s"unknown property: ${beautify(prop)} @ ${beautify(obj)}")
              typeV
            } else AbsValue.Bot
            vopt.value ⊔ typeV
          case Infinite =>
            alarm(s"top string property @ ${beautify(obj)}")
            AbsValue.Bot
        }
        case ListElem(list) =>
          val strV: AbsValue = prop.str.getSingle match {
            case One(Str("length")) | Many => list.length
            case _ => AbsValue.Bot
          }
          val intV =
            if (prop.int.isBottom) AbsValue.Bot
            else list.value
          strV ⊔ intV
        case SymbolElem(desc) =>
          alarm(s"access of the property ${beautify(prop)} for a symbol @${beautify(desc)}")
          AbsValue.Bot
        case AbsObj.Top =>
          alarm(s"access of the property ${beautify(prop)} for the top object")
          AbsValue.Bot
        case AbsObj.Bot =>
          alarm(s"access of the property ${beautify(prop)} for the bottom object")
          AbsValue.Bot
      }
    }

    // lookup type properties
    def lookup(sem: AbsSemantics, ty: String, prop: String): AbsValue = {
      sem.typeMap.get(ty) match {
        case Some(info) =>
          val props = info.props
          props.getOrElse(prop, info.parent.fold(AbsValue.Bot)(lookup(sem, _, prop)))
        case None if (ty == "SubMap") => AbsAbsent.Top // TODO unsound
        case None =>
          alarm(s"unknown type: $ty")
          AbsValue.Bot
      }
    }
    def lookup(sem: AbsSemantics, ty: String, prop: AbsStr): AbsValue = {
      // TODO SubMap types
      if (ty == "SubMap") AbsValue.Bot else prop.gamma match {
        case Infinite => AbsValue.Top
        case Finite(ps) => ps.toList.foldLeft(AbsValue.Bot) {
          case (v, Str(prop)) => v ⊔ lookup(sem, ty, prop)
        }
      }
    }

    // lookup properties
    def lookup(sem: AbsSemantics, addr: Addr, prop: AbsPure): AbsValue = {
      val obj = lookup(sem, addr)
      lookup(sem, obj, prop)
    }

    // lookup addresses
    def lookup(sem: AbsSemantics, addr: Addr): AbsObj = addr match {
      case (_: NamedAddr) => sem.globalHeap.getOrElse(addr, {
        alarm(s"unknown address: ${beautify(addr)}")
        AbsObj.Bot
      })
      case (_: DynamicAddr) => heap(addr)
    }

    // allocate a new symbol
    def allocSymbol(
      asite: Int,
      desc: String
    ): (AbsPure, Elem) = {
      import AbsObj._
      val addr = DynamicAddr(asite)
      val obj: AbsObj = SymbolElem(AbsStr(desc))
      (AbsPure(addr), copy(heap = heap + (addr -> obj)))
    }

    // allocate a new map
    def allocMap(
      asite: Int,
      ty: String,
      props: Map[String, AbsValue]
    ): (AbsPure, Elem) = {
      import AbsObj._
      val map: MapD = MapD(props.map {
        case (k, v) => k -> MapD.AbsVOpt(v)
      }, MapD.AbsVOpt(None))
      val addr = DynamicAddr(asite)
      val obj: AbsObj = MapElem(Some(ty), map)
      (AbsPure(addr), copy(heap = heap + (addr -> obj)))
    }

    // allocate a new list
    def allocList(
      asite: Int,
      vs: List[AbsValue]
    ): (AbsPure, Elem) = {
      import AbsObj._
      val list: ListD = ListD(vs.foldLeft(AbsValue.Bot)(_ ⊔ _))
      val addr = DynamicAddr(asite)
      val obj: AbsObj = ListElem(list)
      (AbsPure(addr), copy(heap = heap + (addr -> obj)))
    }

    // prune
    def prune(refv: AbsRefValue, v: PureValue, cond: Boolean): Elem = refv match {
      case AbsRefValue.Id(x) =>
        val (localV, absent) = env(x)
        if (absent.isTop) alarm(s"unknown variable: $x")
        if (!localV.isBottom) {
          val newV =
            if (cond) localV ⊓ AbsValue(AbsPure.alpha(v), AbsComp.alpha(v))
            else localV.prune(v)
          // normalize
          if (newV.escaped.isBottom) Bot else this + (x -> newV)
        } else Bot
      case AbsRefValue.Prop(base, prop) => this // TODO
      case _ => ???
    }

    // append an element to a list
    def append(sem: AbsSemantics, v: AbsValue, addr: AbsAddr): Elem = {
      import AbsObj._
      copy(heap = addr.toSet.foldLeft(heap) {
        case (h, a: DynamicAddr) => h(a) match {
          case ListElem(list) => h + (a -> ListElem(ListD(list.value ⊔ v)))
          case _ => ???
        }
        case _ => ???
      })
    }

    // prepend an element to a list
    def prepend(v: AbsValue, addr: AbsAddr): Elem = ???

    // copy an object
    def copyOf(sem: AbsSemantics, asite: Int, pure: AbsPure): (AbsPure, Elem) = {
      val objs = pure.addr.toList.map(lookup(sem, _))
      val obj = objs.foldLeft[AbsObj](AbsObj.Bot)(_ ⊔ _)
      val addr = DynamicAddr(asite)
      (AbsPure(addr), copy(heap = heap + (addr -> obj)))
    }

    // get keys of an object
    def keysOf(v: AbsValue): (AbsValue, Elem) = ???

    // pop a value from a list
    def pop(list: AbsValue, idx: AbsValue): (AbsValue, Elem) = ???

    // get type of pure values
    def typeOf(sem: AbsSemantics, v: AbsPure): AbsValue = {
      import AbsObj._
      var set = Set[Str]()
      if (!v.addr.isBottom) for (addr <- v.addr.toSet) {
        set += Str(lookup(sem, addr) match {
          case SymbolElem(_) => "Symbol"
          case MapElem(Some(parent), _) =>
            if (parent endsWith "Object") "Object" else parent
          case MapElem(None, _) => "Record"
          case ListElem(_) => "List"
          case _ => ???
        })
      }
      if (!v.ty.isBottom) set ++= v.ty.toSet.map(t => Str(t.name))
      if (!v.const.isBottom) ???
      if (!v.clo.isBottom) ???
      if (!v.cont.isBottom) ???
      if (!v.ast.isBottom) ???
      if (!v.num.isBottom) set += Str("Number")
      if (!v.int.isBottom) set += Str("Number")
      if (!v.bigint.isBottom) set += Str("BigInt")
      if (!v.str.isBottom) set += Str("String")
      if (!v.bool.isBottom) set += Str("Boolean")
      if (!v.undef.isBottom) set += Str("Undefined")
      if (!v.nullval.isBottom) set += Str("Null")
      if (!v.absent.isBottom) set += Str("Absent")
      AbsStr(set)
    }

    // check whether lists contains elements
    def contains(list: AbsPure, v: AbsPure): AbsValue = AbsBool.Top // TODO
  }
}
