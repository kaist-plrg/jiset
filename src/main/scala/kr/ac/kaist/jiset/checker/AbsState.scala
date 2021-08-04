package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.checker._
import kr.ac.kaist.jiset.util.{ Appender, StateMonad }
import kr.ac.kaist.jiset.util.Useful._

// abstract states
case class AbsState(
  reachable: Boolean,
  map: Map[String, AbsType] = Map(),
  names: Set[String] = Set()
) extends CheckerElem {
  import AbsState._

  // bottom check
  def isBottom: Boolean = !reachable

  // empty check
  def isEmpty: Boolean = reachable && map.size == 0

  // normalization
  def normalized: AbsState = {
    if (!reachable) Bot
    else if (map.exists { case (_, v) => v.isBottom }) Bot
    else this
  }

  // partial order
  def ⊑(that: AbsState): Boolean = (
    (!this.reachable || that.reachable) &&
    (this.map.keySet ++ that.map.keySet).forall(key => this(key) ⊑ that(key))
  )

  // not partial order
  def !⊑(that: AbsState): Boolean = !(this ⊑ that)

  // join operator
  def ⊔(that: AbsState): AbsState = AbsState(
    reachable = this.reachable || that.reachable,
    map = {
      val keys = this.map.keySet ++ that.map.keySet
      val map = keys.map(key => key -> (this(key) ⊔ that(key))).toMap
      map.filter { case (_, v) => !v.isMustAbsent }
    },
    names = this.names ++ that.names
  )

  // meet operator
  def ⊓(that: AbsState): AbsState = {
    val keys = this.map.keySet ++ that.map.keySet
    var isBot = false
    val map = keys.map(k => {
      val newT = this(k) ⊓ that(k)
      if (newT.isBottom) isBot = true
      k -> newT
    }).toMap
    if (isBot) AbsState.Bot
    else AbsState(
      reachable = this.reachable && that.reachable,
      map = map.filter { case (_, v) => !v.isMustAbsent },
      names = this.names.intersect(that.names)
    )
  }

  // temporal variable check
  def isTemporalId(x: String): Boolean =
    x.startsWith("__") && x.endsWith("__")

  // define variable
  def define(
    x: String,
    t: AbsType,
    check: Boolean = false,
    param: Boolean = false
  ): AbsState = norm({
    if (t.isBottom) Bot
    else if (t.isMustAbsent) {
      if (!param) this
      else copy(names = names + x)
    } else {
      CheckerLogger.doCheck({
        if (check && exists(toERef(x), AbsId(x)) == AT && !isTemporalId(x))
          typeBug(s"already defined variable: $x")
      })
      copy(map = map + (x -> t), names = names + x)
    }
  })

  // lookup references
  def lookupVar(
    x: String,
    check: Boolean = true,
    arg: Boolean = false
  ): AbsType = norm {
    val AbsType(ts) = this(x)
    val local = ts - AAbsent
    val global = if (ts contains AAbsent) Global(x).set else Set()
    val t = AbsType(local ++ global)
    val needCheck = check || (arg && !names.contains(x))
    if (needCheck && t.isMustAbsent && !isTemporalId(x)) {
      sem.unknownVars += ((alarmCP, x))
      if (cfg.spec.grammar.nameMap.keySet contains x) AstT(x)
      else AAbsent
    } else {
      sem.unknownVars -= ((alarmCP, x))
      t
    }
  }
  def lookupStrProp(
    expr: Expr,
    base: Type,
    prop: String,
    check: Boolean = true
  ): AbsType = (base, prop) match {
    case (NormalT(t), _) =>
      val pureT = lookupStrProp(expr, t, prop, false)
      if (pureT.isMustAbsent || pureT.isBottom) prop match {
        case "Type" => NORMAL.abs
        case "Value" => t.abs
        case "Target" => EMPTY.abs
        case _ => AAbsent.abs
      }
      else pureT
    case (AbruptT, "Type") => AbsType(BREAK, CONTINUE, RETURN, THROW)
    case (AbruptT, "Value") => AbsType(ESValueT, EMPTY)
    case (AbruptT, "Target") => AbsType(StrT, EMPTY)
    case _ => base.escaped(expr).fold(AbsType.Bot)(_ match {
      case ESValueT => ESValueT.bases.foldLeft(AbsType.Bot) {
        case (t, base) => lookupStrProp(expr, base, prop, check)
      }
      case (nameT: NameT) =>
        val t = nameT(prop)
        if (check && t.isMustAbsent) typeWarning(s"unknown property: $base.$prop")
        t
      case NilT if prop == "length" => ANum(0)
      case ListT(_) | StrT | AStr(_) if prop == "length" => NumT
      case (record: RecordT) => record(prop)
      case MapT(elem) => elem
      case _ => AbsType.Bot
    })
  }
  def lookupGeneralProp(
    expr: Expr,
    base: Type,
    prop: AbsType,
    check: Boolean = true
  ): AbsType = {
    var t = AbsType.Bot
    prop.set.foreach {
      case AStr(prop) => t ⊔= lookupStrProp(expr, base, prop, check)
      case _ =>
    }
    base.escaped(expr).foreach(_ match {
      case MapT(elem) => t ⊔= elem
      case ListT(elem) if prop ⊑ NumT.abs => t ⊔= elem
      case StrT if prop ⊑ NumT.abs => t ⊔= StrT
      case _ =>
    })
    t
  }
  def lookup(
    expr: Expr,
    ref: AbsRef,
    check: Boolean = true
  ): AbsType = norm(ref match {
    case AbsId(x) => lookupVar(x, check)
    case AbsStrProp(base, prop) =>
      base.set.map(lookupStrProp(expr, _, prop, check)).foldLeft(AbsType.Bot)(_ ⊔ _)
    case AbsGeneralProp(base, prop) =>
      base.set.map(lookupGeneralProp(expr, _, prop, check)).foldLeft(AbsType.Bot)(_ ⊔ _)
  })

  // existence check
  def exists(expr: Expr, ref: AbsRef): AbsType = norm(ref match {
    case AbsGeneralProp(base, prop) => BoolT
    case _ => !lookup(expr, ref, check = false).isAbsent
  })

  // update reference
  def update(expr: Expr, ref: AbsRef, t: AbsType): AbsState = norm(ref match {
    case AbsId(x) => define(x, t)
    case AbsStrProp(_, _) if (lookup(expr, ref) ⊓ t).isBottom =>
      typeWarning(s"invalid property update: ${ref} with ${t}")
      this
    case _ => this
  })

  // contains operation
  def contains(list: AbsType, t: AbsType): AbsType = {
    notyet("contains")
    BoolT
  }

  // TODO not yet defined operations
  def append(t: AbsType, list: AbsType): AbsState = notyet("append")
  def prepend(t: AbsType, list: AbsType): AbsState = notyet("prepend")
  def delete(ref: AbsRef): AbsState = notyet("delete")
  def pop(list: AbsType, k: AbsType): (AbsType, AbsState) = {
    if (k !⊑ NumericT) typeWarning(s"invalid pop index: ${k}")
    val ty = list.set.foldLeft(AbsType.Bot) {
      case (ty, ListT(elem)) => ty ⊔ elem
      case (ty, _) => typeWarning(s"invalid pop expression: ${list}"); ty
    }
    (norm(ty), this)
  }

  // typeof operation
  def typeof(t: AbsType): AbsType = norm(AbsType(for {
    x <- t.set
    y <- x.typeNameSet
  } yield AStr(y): Type))

  // is-instance-of operation
  def isInstanceOf(t: AbsType, name: String): AbsType = norm {
    val names = for {
      x <- t.set
      y <- x.instanceNameSet
    } yield y
    if (names.isEmpty) AbsType.Bot
    else if (names == Set(name)) AT
    else if (!names.contains(name)) AF
    else BoolT
  }
  def isInstanceOf(t: AbsType, name: String, k: Int): AbsType = BoolT

  // private helper functions
  private def norm(f: => AbsState): AbsState = if (isBottom) Bot else f
  private def norm(f: => AbsType): AbsType = if (isBottom) AbsType.Bot else f
  private def apply(x: String): AbsType =
    if (reachable) map.getOrElse(x, AAbsent.abs) else AbsType.Bot
  private def notyet(t: AbsType, name: String): (AbsType, AbsState) =
    (t, notyet(name))
  private def notyet(name: String) = norm {
    typeWarning(s"not yet implemented: AbsState.$name")
    this
  }
}
object AbsState {
  // bottom value
  val Bot: AbsState = AbsState(reachable = false)

  // empty value
  val Empty: AbsState = AbsState(reachable = true)

  // monad helper
  val monad: StateMonad[AbsState] = new StateMonad[AbsState]
}
