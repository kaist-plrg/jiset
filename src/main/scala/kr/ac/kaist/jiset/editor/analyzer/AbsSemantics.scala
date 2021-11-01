package kr.ac.kaist.jiset.editor.analyzer

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.editor.analyzer.domain._
import kr.ac.kaist.jiset.error.AnalysisTimeout
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir.{ Id }
import kr.ac.kaist.jiset.js
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.{ DEBUG, LINE_SEP }
import scala.Console._
import scala.annotation.tailrec
import kr.ac.kaist.jiset.spec.algorithm.{ Param }
import kr.ac.kaist.jiset.editor.SyntacticView

// abstract semantics
case class AbsSemantics(
  var npMap: Map[NodePoint[Node], AbsState] = Map(),
  var rpMap: Map[ReturnPoint, AbsRet] = Map(),
  var callInfo: Map[NodePoint[Call], AbsState] = Map(),
  var retEdges: Map[ReturnPoint, Set[NodePoint[Call]]] = Map(),
  timeLimit: Option[Long] = None
) {
  // CFG
  val cfg = js.cfg

  // REPl
  val repl = REPL(this)

  // a worklist of control points
  val worklist: Worklist[ControlPoint] = new QueueWorklist(npMap.keySet)

  // the number of iterations
  def getIter: Int = iter
  private var iter: Int = 0

  // abstract transfer function
  val transfer: AbsTransfer = AbsTransfer(this)

  // set start time of analyzer
  val startTime: Long = System.currentTimeMillis

  // iteration period for check
  val CHECK_PERIOD = 10000

  // log max ijk
  val irIJK = AbsSemantics.MaxIJK()
  val jsIJK = AbsSemantics.MaxIJK()

  // fixpiont computation
  @tailrec
  final def fixpoint: AbsSemantics = worklist.next match {
    case Some(cp) => {
      iter += 1

      // check time limit
      if (iter % CHECK_PERIOD == 0) timeLimit.map(limit => {
        val duration = (System.currentTimeMillis - startTime) / 1000
        if (duration > limit) throw AnalysisTimeout
      })

      // text-based debugging
      if (DEBUG) println(s"${cp.func.name}:$cp")

      // run REPL
      if (USE_REPL) repl(transfer, cp)

      // abstract transfer for the current control point
      else transfer(cp)

      // keep going
      fixpoint
    }
    case None =>
      // finialize REPL
      if (USE_REPL) repl.finished

      // final result
      this
  }

  // get return edges
  def getRetEdges(rp: ReturnPoint): Set[NodePoint[Call]] =
    retEdges.getOrElse(rp, Set())

  // lookup
  def apply(np: NodePoint[Node]): AbsState = npMap.getOrElse(np, AbsState.Bot)
  def apply(rp: ReturnPoint): AbsRet = rpMap.getOrElse(rp, AbsRet.Bot)

  // update internal map
  def +=(pair: (NodePoint[Node], AbsState)): Boolean = {
    val (np, newSt) = pair
    val oldSt = this(np)
    if (!oldSt.isBottom && USE_REPL) repl.merged = true
    if (!(newSt ⊑ oldSt)) {
      npMap += np -> (oldSt ⊔ newSt)
      worklist += np
      true
    }
    false
  }

  // handle calls
  def doCall(
    call: Call,
    callerSt: AbsState,
    func: Function,
    st: AbsState,
    astOpt: Option[js.ast.AST] = None
  ): Unit = {
    val callerNp = NodePoint(call)
    this.callInfo += callerNp -> callerSt

    val isJsCall = func.name match {
      case "Call" | "Construct" => true
      case _ => false
    }
    val np = NodePoint(func.entry)
    this += np -> st.doCall

    val rp = ReturnPoint(func)
    val set = retEdges.getOrElse(rp, Set())
    retEdges += rp -> (set + callerNp)

    val retT = this(rp)
    if (!retT.isBottom) worklist += rp
  }

  // update return points
  def doReturn(rp: ReturnPoint, newRet: AbsRet): Unit = {
    val ReturnPoint(func) = rp
    val retRp = ReturnPoint(func)
    if (!newRet.value.isBottom) {
      val oldRet = this(retRp)
      if (!oldRet.isBottom && USE_REPL) repl.merged = true
      if (newRet !⊑ oldRet) {
        rpMap += retRp -> (oldRet ⊔ newRet)
        worklist += retRp
      }
    }
  }

  // get views by function name
  def getRpsByFuncName(fname: String): Set[ReturnPoint] =
    npMap.keySet.flatMap {
      case NodePoint(entry: Entry) =>
        val func = cfg.funcOf(entry)
        if (func.name == fname) Some(ReturnPoint(func))
        else None
      case _ => None
    }

  // get abstract state of control points
  def getState(cp: ControlPoint): AbsState = cp match {
    case np: NodePoint[_] => this(np)
    case rp: ReturnPoint => this(rp).state
  }

  // get string for result of control points
  def getString(
    cp: ControlPoint,
    color: String,
    detail: Boolean
  ): String = {
    val func = cp.func.name
    val cpStr = cp.toString(detail = detail)
    val k = setColor(color)(s"$func:$cpStr")
    val v = cp match {
      case (np: NodePoint[_]) => this(np).toString(detail = detail)
      case (rp: ReturnPoint) => this(rp).toString(detail = detail)
    }
    s"$k -> $v"
  }

  // check reachability based on call contexts
  def reachable(np: NodePoint[Node]): Boolean =
    !getNps(np).forall(this(_).isBottom)
  def getNps(givenNp: NodePoint[Node]): Set[NodePoint[Node]] = {
    for {
      np <- npMap.keySet
      if givenNp.node == np.node
    } yield np
  }
}
object AbsSemantics {
  // get initial local variables
  def getLocals(
    params: List[Param],
    args: List[AbsValue]
  ): Map[Id, AbsValue] = {
    var map = Map[Id, AbsValue]()

    @tailrec
    def aux(ps: List[Param], as: List[AbsValue]): Unit = (ps, as) match {
      case (Nil, Nil) =>
      case (Param(name, kind) :: pl, Nil) => {
        map += Id(name) -> AbsValue.absent
        aux(pl, Nil)
      }
      case (param :: pl, arg :: al) => {
        map += Id(param.name) -> arg
        aux(pl, al)
      }
      case _ =>
    }

    aux(params, args)
    map
  }

  // maximum ijk
  case class MaxIJK(var i: Int = -1, var j: Int = -1, var k: Int = -1) {
    def update(ijk: (Int, Int, Int)): Unit = {
      val (curI, curJ, curK) = ijk
      if (curI > i) i = curI
      if (curJ > j) j = curJ
      if (curK > k) k = curK
    }
    def get: (Int, Int, Int) = (i, j, k)
    override def toString: String = s"I/J/K = $i/$j/$k"
  }

  // constructors
  def apply(
    sv: SyntacticView,
    timeLimit: Option[Long]
  ): AbsSemantics = {
    val (targetAlgo, asts) = sv.ast.semantics("Evaluation").get
    val nlocals = getLocals(targetAlgo.params, asts.map(AbsValue(_)))
    val func = js.cfg.funcMap(targetAlgo.name)
    val initPair = Initialize(func, nlocals)

    val sem = AbsSemantics(npMap = Map(initPair), timeLimit = timeLimit)
    sem.fixpoint
    sem
  }
}
