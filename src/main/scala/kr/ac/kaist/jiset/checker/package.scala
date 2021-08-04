package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.{ Worklist, StackWorklist }
import kr.ac.kaist.jiset.util.Useful._
import scala.Console.RED
import scala.collection.mutable.{ Map => MMap }

package object checker {
  // perform type checking
  private var alreadyChecked: Boolean = false
  def performTypeCheck(
    cfg: CFG,
    givenSem: Option[AbsSemantics] = None
  ): Unit = {
    // check whether already checked
    if (alreadyChecked) error(s"Trying to perform type checking more than once")
    alreadyChecked = true

    // set CFG
    cfgOpt = Some(cfg)

    // set abstract semantics
    semOpt = Some(givenSem.getOrElse(AbsSemantics(AbsSemantics.initNpMap)))

    // set worklist
    worklistOpt = Some(new StackWorklist(sem.npMap.keySet))

    // set the beginning time of type checking
    Stat.checkStartTime = System.currentTimeMillis

    // initialize type infos
    Type.infos

    // perform type checking
    if (givenSem.isEmpty) AbsTransfer.compute
  }

  //////////////////////////////////////////////////////////////////////////////
  // global data defined after initialization
  //////////////////////////////////////////////////////////////////////////////
  // CFG
  private var cfgOpt: Option[CFG] = None
  lazy val cfg: CFG = get("CFG", cfgOpt)

  // abstract semantics
  private var semOpt: Option[AbsSemantics] = None
  lazy val sem: AbsSemantics = get("AbsSemantics", semOpt)

  // worklists
  private var worklistOpt: Option[Worklist[ControlPoint]] = None
  lazy val worklist: Worklist[ControlPoint] = get("Worklist", worklistOpt)

  //////////////////////////////////////////////////////////////////////////////
  // global mutable data
  //////////////////////////////////////////////////////////////////////////////
  // options
  var TARGET: Option[String] = None
  var USE_VIEW: Boolean = true
  var PRUNE: Boolean = true
  var CHECK_BUG: Boolean = false
  var USE_REPL: Boolean = false
  var DOT: Boolean = false
  var PDF: Boolean = false
  var PARTIAL_MODEL: Option[String] = None

  // current control points for alarms
  var alarmCP: ControlPoint = null

  //////////////////////////////////////////////////////////////////////////////
  // global helpers
  //////////////////////////////////////////////////////////////////////////////
  // get global data
  private def get[T](name: String, dataOpt: Option[T]): T = dataOpt.getOrElse {
    error(s"Please initialize $name before performing type checking.")
  }

  // type warnings
  private val warnMap: MMap[Int, Set[String]] = MMap()
  def numWarn = warnMap.foldLeft(0) { case (n, (_, s)) => n + s.size }
  def typeWarning(
    msg: String,
    cp: ControlPoint = alarmCP
  ): Unit = typeAlarm(msg, bug = false, cp = cp)

  // type bugs
  private val bugMap: MMap[Int, Set[String]] = MMap()
  def numBug = bugMap.foldLeft(0) { case (n, (_, s)) => n + s.size }
  def typeBug(
    msg: String,
    cp: ControlPoint = alarmCP
  ): Unit = typeAlarm(msg, bug = true, cp = cp)

  // type alarms
  def typeAlarm(
    msg: String,
    bug: Boolean,
    cp: ControlPoint
  ): Unit = if (!TEST_MODE) {
    val (fid, postfix, isBug) = if (cp == null) (-1, "", bug) else {
      val func = sem.funcOf(cp)
      (func.uid, s" @ ${func.name}", bug && func.complete)
    }
    val map = if (isBug) bugMap else warnMap
    val set = map.getOrElse(fid, Set())
    if (!(set contains msg)) {
      map += fid -> (set + msg)
      val head = if (isBug) "Bug" else "Warning"
      val alarm = s"[$head] $msg$postfix"
      if (isBug) Console.err.println(setColor(RED)(alarm))
      // if (isBug && CHECK_BUG) REPL.run(cp)
    }
  }

  // abstract types
  val T = ABool(true)
  val F = ABool(false)
  val AT = ABool(true).abs
  val AF = ABool(false).abs

  // targets
  val TARGET_BUILTIN = Set(
    "Array", "BigInt", "Boolean", "Function", "Math",
    "Number", "Object", "Proxy", "String", "Symbol", "Promise"
  )
  val NON_TARGET_BUILTIN = Set(
    "Number.prototype.toExponential",
    "Number.prototype.toFixed",
    "Number.prototype.toPrecision",
  )

  // constants
  val EMPTY = ConstT("empty")
  val UNRESOLVABLE = ConstT("unresolvable")
  val LEXICAL = ConstT("lexical")
  val INITIALIZED = ConstT("initialized")
  val UNINITIALIZED = ConstT("uninitialized")
  val BASE = ConstT("base")
  val DERIVED = ConstT("derived")
  val STRICT = ConstT("strict")
  val GLOBAL = ConstT("global")
  val UNLINKED = ConstT("unlinked")
  val LINKING = ConstT("linking")
  val LINKED = ConstT("linked")
  val EVALUATING = ConstT("evaluating")
  val EVALUATED = ConstT("evaluated")
  val NUMBER = ConstT("Number")
  val BIGINT = ConstT("BigInt")
  val NORMAL = ConstT("normal")
  val BREAK = ConstT("break")
  val CONTINUE = ConstT("continue")
  val RETURN = ConstT("return")
  val THROW = ConstT("throw")
  val SUSPENDED_START = ConstT("suspendedStart")
  val SUSPENDED_YIELD = ConstT("suspendedYield")
  val EXECUTING = ConstT("executing")
  val AWAITING_RETURN = ConstT("awaitingDASHreturn")
  val COMPLETED = ConstT("completed")
  val PENDING = ConstT("pending")
  val FULFILLED = ConstT("fulfilled")
  val REJECTED = ConstT("rejected")
  val FULFILL = ConstT("Fulfill")
  val REJECT = ConstT("Reject")

  // singleton types
  type ANull = ANull.type
  type AUndef = AUndef.type
  type AAbsent = AAbsent.type

  // implicit conversion
  implicit def double2num(x: Double) = ANum(x)
  implicit def bigint2bigint(x: scala.BigInt) = ABigInt(x)
  implicit def string2str(x: String) = AStr(x)
  implicit def boolean2bool(x: Boolean) = ABool(x)
  implicit def type2atype[T](t: T)(implicit f: T => Type) = t.abs

  // path type
  type Path = List[NodePoint[Call]]
}
