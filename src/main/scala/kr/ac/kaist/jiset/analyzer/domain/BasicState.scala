package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js.{ Initialize => JSInitialize }
import kr.ac.kaist.jiset.util.StateMonad

// basic abstract states
object BasicState extends Domain {
  object Bot extends Elem
  object Top extends Elem
  case class Base(
    locals: Map[Id, AbsValue],
    globals: Map[Id, AbsValue],
    heap: AbsHeap
  ) extends Elem

  // constructors
  def apply(
    locals: Map[Id, AbsValue] = Map(),
    globals: Map[Id, AbsValue] = Map(),
    heap: AbsHeap = AbsHeap()
  ) = Base(locals, globals, heap)

  // base globals
  lazy val base: Map[Id, AbsValue] = (for {
    (x, v) <- JSInitialize.initGlobal.toList
    av = AbsValue(v)
  } yield x -> av).toMap

  // monad helper
  val monad: StateMonad[Elem] = new StateMonad[Elem]

  // elements
  sealed trait Elem extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case BasicOrder(bool) => bool
      case (Base(llocals, lglobals, lheap), Base(rlocals, rglobals, rheap)) => {
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
      case BasicJoin(elem) => elem
      case (Base(llocals, lglobals, lheap), Base(rlocals, rglobals, rheap)) => {
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
        Base(newLocals, newGlobals, newHeap)
      }
    }

    // lookup variable directly
    def directLookup(x: Id): AbsValue = lookupLocal(x) ⊔ lookupGlobal(x)

    // getters
    def apply(rv: AbsRefValue, cp: ControlPoint): AbsValue = rv match {
      case AbsRefId(x) => this(x, cp)
      case AbsRefProp(base, prop) => ???
    }
    def apply(x: Id, cp: ControlPoint): AbsValue = {
      val v = directLookup(x)
      if (cp.isBuiltin) v.replaceAbsentWith(AbsValue.undef)
      else v
    }

    // lookup local variables
    def lookupLocal(x: Id): AbsValue = this match {
      case Bot => AbsValue.Bot
      case Base(locals, _, _) => locals.getOrElse(x, AbsValue.Bot)
      case Top => AbsValue.Top
    }

    // lookup global variables
    def lookupGlobal(x: Id): AbsValue = this match {
      case Bot => AbsValue.Bot
      case Base(_, globals, _) =>
        globals.getOrElse(x, base.getOrElse(x, AbsValue.Bot))
      case Top => AbsValue.Top
    }

    // define global variables
    def defineGlobal(x: Id, v: AbsValue): Elem = {
      if (v.isBottom) Bot else this match {
        case Bot | Top => this
        case (st: Base) => st.copy(globals = st.globals + (x -> v))
      }
    }

    // define local variables
    def defineLocal(x: Id, v: AbsValue): Elem = {
      if (v.isBottom) Bot else this match {
        case Bot | Top => this
        case (st: Base) => st.copy(locals = st.locals + (x -> v))
      }
    }
  }
}
