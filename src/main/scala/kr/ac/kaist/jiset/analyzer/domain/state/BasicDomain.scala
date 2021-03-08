package kr.ac.kaist.jiset.analyzer.domain.state

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.util.Useful._

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

    // define variable
    def +(pair: (String, AbsValue)): Elem = copy(env = env + pair)

    // update references
    def update(sem: AbsSemantics, refv: AbsRefValue, v: AbsValue): Elem = refv match {
      case AbsRefValue.Id(x) =>
        val (localV, absent) = env(x)
        if (!localV.isBottom) {
          if (absent.isTop) alarm(s"unknown variable: $x")
          this + (x -> v)
        } else if (absent.isTop) sem.globalEnv.get(x) match {
          case Some(globalV) =>
            if (!(v ⊑ globalV))
              alarm(s"wrong update of global variable $x with ${beautify(v)}")
            this
          case None =>
            alarm(s"unknown variable: $x")
            this
        }
        else Bot
      case AbsRefValue.ObjProp(ty, addr, prop) => prop.getSingle match {
        // TODO check properties for types
        case One(Str(p)) => addr.toSet.foldLeft(this) {
          case (st, a: DynamicAddr) =>
            val obj = heap(a) + (p -> v)
            copy(heap = heap + (a -> obj))
          case _ => ???
        }
        case _ => ???
      }
    }

    // update references
    def delete(sem: AbsSemantics, refv: AbsRefValue): Elem = ???

    // lookup reference values
    def apply(sem: AbsSemantics, refv: AbsRefValue): AbsValue = refv match {
      case AbsRefValue.Bot => AbsValue.Bot
      case AbsRefValue.Top => AbsValue.Top
      case AbsRefValue.Id(x) =>
        val (localV, absent) = env(x)
        val globalV: AbsValue = if (absent.isTop) sem.globalEnv.getOrElse(x, {
          alarm(s"unknown variable: $x")
          AbsAbsent.Top
        })
        else AbsValue.Bot
        localV ⊔ globalV
      case AbsRefValue.ObjProp(ty, addr, prop) =>
        val tyV = ty.toSet.toList.map(ty => sem.lookup(ty.name, prop))
        val addrV = addr.toSet.toList.map(this(sem, _, prop))
        (tyV ++ addrV).foldLeft(AbsValue.Bot)(_ ⊔ _)
      case AbsRefValue.StrProp(str, prop) => ???
    }

    // lookup properties
    def apply(sem: AbsSemantics, addr: Addr, prop: AbsStr): AbsValue = {
      val obj = this(sem, addr)
      obj(sem, prop)
    }

    // lookup addresses
    def apply(sem: AbsSemantics, addr: Addr): AbsObj = addr match {
      case (_: NamedAddr) => sem.globalHeap.getOrElse(addr, AbsObj.Bot)
      case (_: DynamicAddr) => heap(addr)
    }

    // allocate a new symbol
    def allocSymbol(
      asite: Int,
      desc: String
    ): (AbsPure, Elem) = {
      import AbsObj._
      val addr = DynamicAddr(asite)
      val obj: AbsObj = SymbolElem(desc)
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
      val list: ListD = ListD.Fixed(vs.toVector)
      val addr = DynamicAddr(asite)
      val obj: AbsObj = ListElem(list)
      (AbsPure(addr), copy(heap = heap + (addr -> obj)))
    }

    // append an element to a list
    def append(v: AbsValue, addr: AbsAddr): Elem = ???

    // prepend an element to a list
    def prepend(v: AbsValue, addr: AbsAddr): Elem = ???

    // copy an object
    def copyOf(v: AbsValue): (AbsValue, Elem) = ???

    // get keys of an object
    def keysOf(v: AbsValue): (AbsValue, Elem) = ???

    // pop a value from a list
    def pop(list: AbsValue, idx: AbsValue): (AbsValue, Elem) = ???

    // get type of values
    def typeOf(v: AbsValue): AbsValue = ???

    // check whether lists contains elements
    def contains(list: AbsValue, v: AbsValue): AbsValue = ???
  }
}
