package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js.{ Initialize => JSInitialize }
import kr.ac.kaist.jiset.util.StateMonad
import kr.ac.kaist.jiset.util.Useful._

// basic abstract states
object BasicState extends Domain {
  lazy val Bot = Elem(reachable = false)
  lazy val Empty = Elem(reachable = true)
  lazy val Top = error("impossible define top value.")

  // base globals
  lazy val base: Map[Id, AbsValue] = (for {
    (x, v) <- JSInitialize.initGlobal.toList
    av = AbsValue(v)
  } yield x -> av).toMap

  // monad helper
  val monad: StateMonad[Elem] = new StateMonad[Elem]

  // elements
  case class Elem(
    reachable: Boolean,
    locals: Map[Id, AbsValue] = Map(),
    globals: Map[Id, AbsValue] = Map(),
    heap: AbsHeap = AbsHeap.Bot
  ) extends ElemTrait {
    // partial order
    override def isBottom = !this.reachable
    override def isTop = false

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
      case AbsRefProp(base, prop) => ???
    }
    def apply(x: Id, cp: ControlPoint): AbsValue = {
      val v = directLookup(x)
      if (cp.isBuiltin) v.replaceAbsentWith(AbsValue.undef)
      else v
    }

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

    // define global variables
    def defineGlobal(pairs: (Id, AbsValue)*): Elem =
      copy(globals = globals ++ pairs)

    // define local variables
    def defineLocal(pairs: (Id, AbsValue)*): Elem =
      copy(locals = locals ++ pairs)
  }
}
