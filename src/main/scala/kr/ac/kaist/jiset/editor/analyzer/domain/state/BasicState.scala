package kr.ac.kaist.jiset.editor.analyzer.domain

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.editor.analyzer._
import kr.ac.kaist.jiset.ir.{ AllocSite => _, Stringifier => IrStringifier, _ }
import kr.ac.kaist.jiset.js
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.StateMonad
import kr.ac.kaist.jiset.util.Useful._

// basic abstract states
object BasicState extends Domain {
  lazy val Bot = Elem(false, Map())
  lazy val Empty = Elem(true, Map())

  // base globals
  lazy val base: Map[Id, AbsValue] = (for {
    (x, v) <- js.Initialize.initGlobal.toList
    av = AbsValue(v)
  } yield x -> av).toMap

  // monad helper
  val monad: StateMonad[Elem] = new StateMonad[Elem]

  // appender
  val irStringifier = new IrStringifier()
  import irStringifier._
  import AbsValue._
  def mkAppender(detail: Boolean): App[Elem] = (app, elem) => {
    if (elem.isBottom) app >> "⊥"
    else {
      app.wrap {
        app :> "locals: " >> elem.locals >> LINE_SEP
      }
    }
  }
  implicit val app: App[Elem] = mkAppender(true)
  val shortApp: App[Elem] = mkAppender(false)

  // constructors
  def apply(
    reachable: Boolean = true,
    locals: Map[Id, AbsValue] = Map()
  ): Elem = Elem(reachable, locals)

  // extractors
  def unapply(elem: Elem) = Some((
    elem.reachable,
    elem.locals
  ))

  // elements
  case class Elem(
    reachable: Boolean,
    locals: Map[Id, AbsValue]
  ) extends ElemTrait {
    // partial order
    override def isBottom = !this.reachable

    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case _ if this.isBottom => true
      case _ if that.isBottom => false
      case (
        Elem(_, llocals),
        Elem(_, rlocals)
        ) => {
        val localsB = (llocals.keySet ++ rlocals.keySet).forall(x => {
          this.lookupLocal(x) ⊑ that.lookupLocal(x)
        })
        localsB
      }
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case _ if this.isBottom => that
      case _ if that.isBottom => this
      case (
        Elem(_, llocals),
        Elem(_, rlocals)
        ) => {
        val newLocals = (for {
          x <- (llocals.keySet ++ rlocals.keySet).toList
          v = this.lookupLocal(x) ⊔ that.lookupLocal(x)
          if !v.isBottom
        } yield x -> v).toMap
        Elem(true, newLocals)
      }
    }

    // singleton checks
    def isSingle: Boolean = (
      reachable &&
      locals.forall(_._2.isSingle)
    )

    // singleton location checks
    def isSingle(loc: Loc): Boolean = false

    // handle calls
    def doCall: Elem = this
    def doProcStart(fixed: Set[Loc]): Elem = this

    // handle returns (this: return states / to: caller states)
    def doReturn(to: Elem, defs: (Id, AbsValue)*): Elem = doReturn(to, defs)
    def doReturn(to: Elem, defs: Iterable[(Id, AbsValue)]): Elem = Elem(
      reachable = true,
      locals = to.locals ++ defs
    )
    def doProcEnd(to: Elem, defs: (Id, AbsValue)*): Elem = doProcEnd(to, defs)
    def doProcEnd(to: Elem, defs: Iterable[(Id, AbsValue)]): Elem = Elem(
      reachable = true,
      locals = to.locals ++ defs
    )

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
      val compValue = (base.getSingle, prop.getSingle) match {
        case (FlatElem(CompValue(ty, _, _)), FlatElem(Str("Type"))) => BaseElem(ty)
        case (FlatElem(CompValue(_, v, _)), FlatElem(Str("Value"))) => BaseElem(v)
        case (FlatElem(CompValue(_, _, tgtOpt)), FlatElem(Str("Target"))) => BaseElem(tgtOpt.map(Str).getOrElse(Absent))
        case (FlatElem(_), _) => BaseBot
        case (FlatBot, _) | (_, FlatBot) => BaseBot
        case _ => BaseTop
      }
      if (!compValue.isBottom) compValue else {
        val escaped = base.escaped
        if (escaped.isBottom) BaseTop else {
          val strValue = (escaped.getSingle, prop.getSingle) match {
            case (FlatBot, _) | (_, FlatBot) => AbsValue.Bot
            case (FlatElem(Str(str)), FlatElem(simple)) => simple match {
              case Str("length") => BaseElem(INum(str.length))
              case INum(k) => BaseElem(Str(str(k.toInt).toString))
              case Num(k) => BaseElem(Str(str(k.toInt).toString))
              case _ => AbsValue.Bot
            }
            case _ => BaseTop
          }
          compValue ⊔ strValue
        }
      }
    }
    def apply(loc: Loc): AbsValue = BaseTop

    // lookup local variables
    def lookupLocal(x: Id): AbsValue = this match {
      case Elem(_, locals) =>
        locals.getOrElse(x, AbsValue.Bot)
    }

    val dynamicGlobal: Set[String] = {
      import kr.ac.kaist.jiset.js._
      Set(
        CONTEXT,
        EXECUTION_STACK,
        GLOBAL,
        JOB_QUEUE,
        PRIMITIVE,
        RET_CONT,
        SYMBOL_REGISTRY
      )
    }

    def isPermittedGlobal(id: Id) = if (dynamicGlobal contains id.name) false else true

    // lookup global variables
    def lookupGlobal(x: Id): AbsValue = this match {
      case Elem(_, _) =>
        if (isPermittedGlobal(x)) js.Initialize.initGlobal.get(x).map(BaseElem).getOrElse(AbsValue.Bot) else BaseTop
    }

    // setters
    def update(refV: AbsRefValue, value: AbsValue): Elem = refV match {
      case AbsRefId(x) => update(x, value)
      case AbsRefProp(base, prop) => this
    }
    def update(x: Id, value: AbsValue): Elem =
      bottomCheck(value) {
        if (locals contains x) copy(locals = locals + (x -> value))
        else this
      }
    def update(aloc: AbsValue, prop: AbsValue, value: AbsValue): Elem = this

    // existence checks
    def exists(ref: AbsRefValue): AbsValue = ref match {
      case AbsRefId(id) => directLookup(id).isAbsent
      case AbsRefProp(base, prop) => this(base.escaped, prop).isAbsent
    }

    // delete a property from a map
    def delete(refV: AbsRefValue): Elem = this

    // object operators
    def append(loc: AbsValue, value: AbsValue): Elem =
      this
    def prepend(loc: AbsValue, value: AbsValue): Elem =
      this
    def pop(loc: AbsValue, idx: AbsValue): (AbsValue, Elem) = {
      (BaseTop, this)
    }
    def copyObj(from: AbsValue)(to: AllocSite): Elem =
      this
    def keys(loc: AbsValue, intSorted: Boolean)(to: AllocSite): Elem =
      this
    def allocMap(ty: Ty, pairs: List[(AbsValue, AbsValue)])(to: AllocSite): Elem =
      this
    def allocList(list: List[AbsValue])(to: AllocSite): Elem =
      this
    def allocSymbol(desc: AbsValue)(to: AllocSite): Elem =
      this
    def setType(loc: AbsValue, ty: Ty): Elem =
      this
    def contains(loc: AbsValue, value: AbsValue): AbsValue =
      BaseTop

    // define global variables
    def defineGlobal(pairs: (Id, AbsValue)*): Elem = this
    // define local variables
    def defineLocal(pairs: (Id, AbsValue)*): Elem =
      bottomCheck(pairs.unzip._2) { copy(locals = locals ++ pairs) }

    // conversion to string
    def toString(detail: Boolean): String = {
      val app = new Appender
      implicit val stApp =
        if (detail) BasicState.app else BasicState.shortApp
      app >> this
      app.toString
    }

    // get string wth detailed shapes of locations
    def getString(value: AbsValue): String = {
      val app = new Appender
      app >> value.toString
      app.toString
    }

    // check bottom elements in abstract semantics
    private def bottomCheck(vs: Domain#Elem*)(f: => Elem): Elem =
      bottomCheck(vs)(f)
    private def bottomCheck(
      vs: Iterable[Domain#Elem]
    )(f: => Elem): Elem = {
      if (this.isBottom || vs.exists(_.isBottom)) Bot
      else f
    }
  }
}
