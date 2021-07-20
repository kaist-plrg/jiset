package kr.ac.kaist.jiset

import scala.Console.RED
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._

package object analyzer {
  // initialization
  def init(cfg: CFG): Unit = {
    // set CFG
    _cfg = Some(cfg)

    // create log directory
    mkdir(ANALYZE_LOG_DIR)

    // set the beginning time of analysis
    AnalysisStat.analysisStartTime = System.currentTimeMillis

    // initialize type infos
    Type.infos
  }

  // analyze with initialization
  def analyze(cfg: CFG): AnalysisResult = {
    // initialization
    init(cfg)

    // fixpoint computation
    AbsTransfer.compute

    // return analysis result
    AbsSemantics.toResult
  }

  // load analysis result with initialization
  def load(cfg: CFG, result: AnalysisResult): AnalysisResult = {
    // initialization
    init(cfg)

    // load result
    AbsSemantics.load(result)

    // return analysis result
    result
  }

  //////////////////////////////////////////////////////////////////////////////
  // global data defined after initialization
  //////////////////////////////////////////////////////////////////////////////
  // CFG
  private var _cfg: Option[CFG] = None
  lazy val cfg: CFG = _cfg.getOrElse {
    error("Please initialize CFG before performing type analysis.")
  }

  // worklists
  lazy val worklist: Worklist[ControlPoint] =
    new StackWorklist(AbsSemantics.npMap.keySet)

  //////////////////////////////////////////////////////////////////////////////
  // global mutable data
  //////////////////////////////////////////////////////////////////////////////
  // options
  var TARGET: Option[String] = None
  var USE_VIEW: Boolean = true
  var PRUNE: Boolean = true
  var CHECK_ALARM: Boolean = false
  var REPL: Boolean = false
  var DOT: Boolean = false
  var PDF: Boolean = false
  var PARTIAL_MODEL: Option[String] = None

  // alarm
  var alarmCP: ControlPoint = null
  var alarmCPStr: String = ""

  //////////////////////////////////////////////////////////////////////////////
  // global helpers
  //////////////////////////////////////////////////////////////////////////////
  // print writers
  val nfAlarms = getPrintWriter(s"$ANALYZE_LOG_DIR/alarms")
  val nfErrors = getPrintWriter(s"$ANALYZE_LOG_DIR/errors")

  // size
  def numError = errorMap.foldLeft(0) { case (n, (_, s)) => n + s.size }
  def numWarning = alarmMap.foldLeft(0) { case (n, (_, s)) => n + s.size }

  private var alarmMap: Map[String, Set[String]] = Map()
  private var errorMap: Map[Int, Set[String]] = Map()
  def warning(
    msg: String,
    cp: ControlPoint = alarmCP,
    cpStr: String = alarmCPStr
  ): Unit = alarm(msg, error = false, cp, cpStr)
  def alarm(
    msg: String,
    error: Boolean = true,
    cp: ControlPoint = alarmCP,
    cpStr: String = alarmCPStr
  ): Unit = if (TEST_MODE) {
  } else if (cp == null) {
    nfAlarms.println(msg)
    nfAlarms.flush()
  } else {
    val key = cp match {
      case NodePoint(node, _) => s"node${node.uid}"
      case ReturnPoint(func, _) => s"func${func.uid}"
    }
    val set = alarmMap.getOrElse(key, Set())
    if (!(set contains msg)) {
      alarmMap += key -> (set + msg)
      val errMsg = s"[Bug] $msg @ $cpStr"
      val func = AbsSemantics.funcOf(cp)
      if (error && func.complete) {
        val key = func.uid
        val set = errorMap.getOrElse(key, Set())
        if (!(set contains msg)) {
          errorMap += key -> (set + msg)
          val errMsg = s"[Bug] $msg @ ${func.name}"
          if (!LOG) Console.err.println(setColor(RED)(errMsg))
          nfErrors.println(errMsg)
          nfErrors.flush()
          if (CHECK_ALARM) AnalyzeREPL.run(cp)
        }
      }
      if (LOG) {
        nfAlarms.println(errMsg)
        nfAlarms.flush()
      }
    }
  }

  // analysis cfg path
  val CFG_PATH = s"$ANALYZE_LOG_DIR/cfg"

  // dump CFG in DOT/PDF format
  def dumpCFG(
    cp: Option[ControlPoint] = None,
    pdf: Boolean = true,
    depth: Option[Int] = None
  ): Unit = try {
    val dot = (new DotPrinter)(cp, depth).toString
    dumpFile(dot, s"$CFG_PATH.dot")
    if (pdf) {
      executeCmd(s"""unflatten -l 10 -o ${CFG_PATH}_trans.dot $CFG_PATH.dot""")
      executeCmd(s"""dot -Tpdf "${CFG_PATH}_trans.dot" -o "$CFG_PATH.pdf"""")
      println(s"Dumped CFG to $CFG_PATH.pdf")
    } else println(s"Dumped CFG to $CFG_PATH.dot")
  } catch {
    case _: Throwable => printlnColor(RED)(s"Cannot dump CFG")
  }

  // dump CFG in DOT/PDF format
  def dumpFunc(
    func: Function,
    pdf: Boolean = true
  ): Unit = try {
    val dot = (new DotPrinter)(func).toString
    dumpFile(dot, s"$CFG_PATH.dot")
    if (pdf) {
      executeCmd(s"""unflatten -l 10 -o ${CFG_PATH}_trans.dot $CFG_PATH.dot""")
      executeCmd(s"""dot -Tpdf "${CFG_PATH}_trans.dot" -o "$CFG_PATH.pdf"""")
      println(s"Dumped CFG to $CFG_PATH.pdf")
    } else println(s"Dumped CFG to $CFG_PATH.dot")
  } catch {
    case _: Throwable => printlnColor(RED)(s"Cannot dump CFG")
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
}
