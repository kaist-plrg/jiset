package kr.ac.kaist.jiset

import scala.Console.RED
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._

package object analyzer {
  // inputs
  lazy val cfg: CFG = _cfg
  lazy val worklist: Worklist[ControlPoint] =
    new StackWorklist(AbsSemantics.npMap.keySet)

  // initialization
  private var _cfg: CFG = null
  def init(cfg: CFG): Unit = _cfg = cfg

  // options
  var TARGET: Option[String] = None
  var USE_VIEW: Boolean = true
  var PRUNE: Boolean = true
  var CHECK_ALARM: Boolean = false
  var REPL: Boolean = false
  var DOT: Boolean = false
  var PDF: Boolean = false

  // initialize
  mkdir(ANALYZE_LOG_DIR)
  val nfAlarms = getPrintWriter(s"$ANALYZE_LOG_DIR/alarms")
  val nfErrors = getPrintWriter(s"$ANALYZE_LOG_DIR/errors")

  // alarm
  var alarmCP: ControlPoint = null
  var alarmCPStr: String = ""

  private var alarmMap: Map[String, Set[String]] = Map()
  private var errorMap: Map[Int, Set[String]] = Map()
  def warning(msg: String): Unit = alarm(msg, error = false)
  def alarm(msg: String, error: Boolean = true): Unit = if (TEST_MODE) {
  } else if (alarmCP == null) {
    nfAlarms.println(msg)
    nfAlarms.flush()
  } else {
    val key = alarmCP match {
      case NodePoint(node, _) => s"node${node.uid}"
      case ReturnPoint(func, _) => s"func${func.uid}"
    }
    val set = alarmMap.getOrElse(key, Set())
    if (!(set contains msg)) {
      alarmMap += key -> (set + msg)
      val errMsg = s"[Bug] $msg @ $alarmCPStr"
      if (error) {
        val func = AbsSemantics.funcOf(alarmCP)
        val key = func.uid
        val set = errorMap.getOrElse(key, Set())
        if (!(set contains msg)) {
          errorMap += key -> (set + msg)
          val errMsg = s"[Bug] $msg @ ${func.name}"
          Console.err.println(setColor(RED)(errMsg))
          nfErrors.println(errMsg)
          nfErrors.flush()
          if (CHECK_ALARM) AnalyzeREPL.run(alarmCP)
        }
      }
      if (LOG) {
        nfAlarms.println(errMsg)
        nfAlarms.flush()
      }
    }
  }

  // dump CFG in DOT/PDF format
  def dumpCFG(
    cp: Option[ControlPoint] = None,
    pdf: Boolean = true,
    depth: Option[Int] = None
  ): Unit = try {
    val dot = (new DotPrinter)(cp, depth).toString
    dumpFile(dot, s"$CFG_DIR.dot")
    if (pdf) {
      executeCmd(s"""unflatten -l 10 -o ${CFG_DIR}_trans.dot $CFG_DIR.dot""")
      executeCmd(s"""dot -Tpdf "${CFG_DIR}_trans.dot" -o "$CFG_DIR.pdf"""")
      println(s"Dumped CFG to $CFG_DIR.pdf")
    } else println(s"Dumped CFG to $CFG_DIR.dot")
  } catch {
    case _: Throwable => printlnColor(RED)(s"Cannot dump CFG")
  }

  // abstract types
  val T = Bool(true)
  val F = Bool(false)
  val AT = Bool(true).abs
  val AF = Bool(false).abs

  val TARGET_BUILTIN = Set("Math")

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
  val COMPLETED = ConstT("completed")

  // singleton types
  type Null = Null.type
  type Undef = Undef.type
  type Absent = Absent.type

  // implicit conversion
  implicit def double2num(x: Double) = Num(x)
  implicit def bigint2bigint(x: scala.BigInt) = BigInt(x)
  implicit def string2str(x: String) = Str(x)
  implicit def boolean2bool(x: Boolean) = Bool(x)
  implicit def type2atype[T](t: T)(implicit f: T => Type) = t.abs
}
