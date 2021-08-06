package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js.{ Initialize => JSInitialize }
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.StateMonad
import kr.ac.kaist.jiset.util.Useful._

// basic abstract states
object BasicState extends Domain {
  lazy val Bot = Elem(false, Map(), Map(), AbsHeap.Bot)
  lazy val Empty = Elem(true, Map(), Map(), AbsHeap.Bot)

  // base globals
  lazy val base: Map[Id, AbsValue] = (for {
    (x, v) <- JSInitialize.initGlobal.toList
    av = AbsValue(v)
  } yield x -> av).toMap

  // monad helper
  val monad: StateMonad[Elem] = new StateMonad[Elem]

  // appender
  implicit val app: App[Elem] = (app, elem) => {
    if (elem.isBottom) app >> "⊥"
    else if (elem.locals.isEmpty) app >> "{}"
    else app.wrap {
      elem.locals.toList
        .sortBy(_._1.toString)
        .foreach { case (k, v) => app :> s"$k -> " >> v >> LINE_SEP }
    }
    app >> LINE_SEP
    app >> "heap: " >> elem.heap
  }

  // constructors
  def apply(
    reachable: Boolean = true,
    locals: Map[Id, AbsValue] = Map(),
    globals: Map[Id, AbsValue] = Map(),
    heap: AbsHeap = AbsHeap.Bot
  ): Elem = Elem(reachable, locals, globals, heap)

  // extractors
  def unapply(elem: Elem) = Some((
    elem.reachable,
    elem.locals,
    elem.globals,
    elem.heap,
  ))

  // elements
  case class Elem(
    reachable: Boolean,
    locals: Map[Id, AbsValue],
    globals: Map[Id, AbsValue],
    heap: AbsHeap
  ) extends ElemTrait {
    // partial order
    override def isBottom = !this.reachable

    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case _ if this.isBottom => true
      case _ if that.isBottom => false
      case (
        Elem(_, llocals, lglobals, lheap),
        Elem(_, rlocals, rglobals, rheap)
        ) => {
        val localsB = (llocals.keySet ++ rlocals.keySet).forall(x => {
          this.lookupLocal(x) ⊑ that.lookupLocal(x)
        })
        val globalsB = (lglobals.keySet ++ rglobals.keySet).forall(x => {
          this.lookupGlobal(x) ⊑ that.lookupGlobal(x)
        })
        val heapB = lheap ⊑ rheap
        localsB && globalsB && heapB
      }
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case _ if this.isBottom => that
      case _ if that.isBottom => this
      case (
        Elem(_, llocals, lglobals, lheap),
        Elem(_, rlocals, rglobals, rheap)
        ) => {
        val newLocals = (for {
          x <- (llocals.keySet ++ rlocals.keySet).toList
          v = this.lookupLocal(x) ⊔ that.lookupLocal(x)
          if !v.isBottom
        } yield x -> v).toMap
        val newGlobals = (for {
          x <- (lglobals.keySet ++ rglobals.keySet).toList
          v = this.lookupGlobal(x) ⊔ that.lookupGlobal(x)
          if !v.isBottom
        } yield x -> v).toMap
        val newHeap = lheap ⊔ rheap
        Elem(true, newLocals, newGlobals, newHeap)
      }
    }

    // lookup variable directly
    def directLookup(x: Id): AbsValue = lookupLocal(x) ⊔ lookupGlobal(x)

    // getters
    def apply(rv: AbsRefValue, cp: ControlPoint): AbsValue = rv match {
      case AbsRefId(x) => this(x, cp)
      case AbsRefProp(base, prop) => this(base, prop)
    }
    def apply(x: Id, cp: ControlPoint): AbsValue = {
      val v = directLookup(x)
      if (cp.isBuiltin && AbsValue.absent ⊑ v) v.removeAbsent ⊔ AbsValue.undef
      else v
    }
    def apply(base: AbsValue, prop: AbsValue): AbsValue = {
      val compValue = base.comp(prop)
      val locValue = heap(base.loc, prop)
      val strValue = (base.str.getSingle, prop.getSingle) match {
        case (FlatBot, _) | (_, FlatBot) => AbsValue.Bot
        case (FlatElem(Str(str)), FlatElem(ASimple(simple))) => simple match {
          case Str("length") => AbsValue(str.length)
          case INum(k) => AbsValue(str(k.toInt).toString)
          case Num(k) => AbsValue(str(k.toInt).toString)
          case _ => AbsValue.Bot
        }
        case _ => AbsValue.str ⊔ AbsValue.int
      }
      compValue ⊔ locValue ⊔ strValue
    }
    def apply(loc: Loc): AbsObj = heap(loc)

    // lookup local variables
    def lookupLocal(x: Id): AbsValue = this match {
      case Elem(_, locals, _, _) =>
        locals.getOrElse(x, AbsValue.Bot)
    }

    // lookup global variables
    def lookupGlobal(x: Id): AbsValue = this match {
      case Elem(_, _, globals, _) =>
        globals.getOrElse(x, base.getOrElse(x, AbsValue.Bot))
    }

    // setters
    def update(refV: AbsRefValue, value: AbsValue): Elem = refV match {
      case AbsRefId(x) => update(x, value)
      case AbsRefProp(base, prop) =>
        update(base.escaped.loc, prop, value)
    }
    def update(x: Id, value: AbsValue): Elem = bottomCheck(value) {
      if (locals contains x) copy(locals = locals + (x -> value))
      else copy(globals = globals + (x -> value))
    }
    def update(aloc: AbsLoc, prop: AbsValue, value: AbsValue): Elem =
      bottomCheck(aloc, prop, value) {
        copy(heap = heap.update(aloc, prop, value))
      }
    def update(
      loc: Loc,
      prop: AbsValue,
      value: AbsValue,
      weak: Boolean = false
    ): Elem = bottomCheck(prop, value) {
      copy(heap = heap.update(loc, prop, value, weak))
    }

    // define global variables
    def defineGlobal(pairs: (Id, AbsValue)*): Elem =
      bottomCheck(pairs.unzip._2) { copy(globals = globals ++ pairs) }

    // define local variables
    def defineLocal(pairs: (Id, AbsValue)*): Elem =
      bottomCheck(pairs.unzip._2) { copy(locals = locals ++ pairs) }

    // object operators
    def append(loc: Loc, value: AbsValue): Elem = bottomCheck(value) {
      copy(heap = heap.append(loc, value))
    }
    def prepend(loc: Loc, value: AbsValue): Elem = bottomCheck(value) {
      copy(heap = heap.prepend(loc, value))
    }
    def pop(loc: Loc, idx: AbsValue): (AbsValue, Elem) = {
      var v: AbsValue = AbsValue.Bot
      val st: Elem = bottomCheck(idx) { ??? }
      (v, st)
    }
    def copyObj(from: Loc)(to: Loc): Elem =
      copy(heap = heap.copyObj(from)(to))
    def keys(loc: Loc, intSorted: Boolean)(to: Loc): Elem =
      copy(heap = heap.keys(loc, intSorted)(to))
    def allocMap(ty: Ty, map: Map[AbsValue, AbsValue] = Map())(to: Loc): Elem =
      bottomCheck(map.toList.flatMap { case (k, v) => List(k, v) }) {
        copy(heap = heap.allocMap(ty, map)(to))
      }
    def allocList(list: List[AbsValue])(to: Loc): Elem = bottomCheck(list) {
      copy(heap = heap.allocList(list)(to))
    }
    def allocSymbol(desc: AbsValue)(to: Loc): Elem = bottomCheck(desc) {
      copy(heap = heap.allocSymbol(desc)(to))
    }
    def setType(loc: AbsLoc, ty: Ty): Elem = bottomCheck(loc) {
      copy(heap = loc.foldLeft(heap) { (h, l) => h.setType(l, ty) })
    }

    // existence checks
    def exists(ref: AbsRefValue): AbsBool = ref match {
      case AbsRefId(id) => directLookup(id).hasAbsent
      case AbsRefProp(base, prop) => this(base.escaped, prop).hasAbsent
    }

    // check bottom elements in abstract semantics
    private def bottomCheck(vs: Domain#Elem*)(f: => Elem): Elem =
      bottomCheck(vs)(f)
    private def bottomCheck(
      vs: Iterable[Domain#Elem]
    )(f: => Elem): Elem = {
      if (vs.exists(_.isBottom)) Bot
      else f
    }
  }
}
