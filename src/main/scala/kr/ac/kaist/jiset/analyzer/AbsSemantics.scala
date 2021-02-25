package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util.Useful._
import scala.Console.CYAN

class AbsSemantics(val cfg: CFG) {
  // ECMAScript
  val spec: ECMAScript = cfg.spec

  // internal map from control points to abstract states
  private var npMap: Map[NodePoint, AbsState] = initNpMap
  private var rpMap: Map[ReturnPoint, (AbsHeap, AbsValue)] = Map()

  // worklist
  val worklist: Worklist[ControlPoint] = new StackWorklist(npMap.keySet)

  // global variables
  val globals: Map[String, AbsValue] = (for {
    (x, v) <- cfg.initGlobals
  } yield x -> AbsValue(v)).toMap

  //////////////////////////////////////////////////////////////////////////////
  // Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // lookup
  def apply(np: NodePoint): AbsState = npMap.getOrElse(np, AbsState.Bot)
  def apply(rp: ReturnPoint): (AbsHeap, AbsValue) =
    rpMap.getOrElse(rp, (AbsHeap.Bot, AbsValue.Bot))

  // update internal map
  def +=(pair: (NodePoint, AbsState)): Unit = {
    val (np, newSt) = pair
    val oldSt = this(np)
    if (!(newSt ⊑ oldSt)) {
      npMap += np -> (oldSt ⊔ newSt)
      worklist += np
    }
  }
  def doReturn(pair: (ReturnPoint, (AbsHeap, AbsValue))): Unit = {
    val (rp, (newH, newV)) = pair
    val (oldH, oldV) = this(rp)
    if (!(newH ⊑ oldH && newV ⊑ oldV)) {
      rpMap += rp -> (oldH ⊔ newH, oldV ⊔ newV)
      worklist += rp
    }
  }

  // get function of given control points
  def funcOf(cp: ControlPoint): Function = cp match {
    case NodePoint(node, _) => cfg.funcOf(node)
    case ReturnPoint(func, _) => func
  }

  // conversion to string
  override def toString: String =
    rpMap.keySet.toList.map(getString).sorted.mkString(LINE_SEP)

  // get string for result of control points
  def getString(cp: ControlPoint): String = {
    val cyan = setColor(CYAN)
    val (k, v) = cp match {
      case np @ NodePoint(entry: Entry, view) =>
        val st = this(np)
        (cyan(s"${cfg.funcOf(entry).name}:$view:ENTRY"), beautify(st))
      case (np: NodePoint) =>
        val st = this(np)
        (np.toString, beautify(st))
      case (rp: ReturnPoint) =>
        val (h, v) = this(rp)
        (cyan(s"$rp:RETURN"), beautify(v) + (
          if (h.isBottom) ""
          else s" @ ${beautify(h)}"
        ))
    }
    s"$k -> $v"
  }

  //////////////////////////////////////////////////////////////////////////////
  // Private Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // initialization of node points with abstract states
  private def initNpMap: Map[NodePoint, AbsState] = (for {
    func <- cfg.funcs.toList
    (types, st) <- getTypes(func.algo.head)
    view = View(types)
    cp = NodePoint(func.entry, view)
  } yield cp -> st).toMap

  // target algorithms
  private def targetPatterns = List(
    // """Literal\[.*""".r,
    // """PrimaryExpression.*IsIdentifierRef""".r,
    """PrimaryExpression\[0,0\].Evaluation""".r,
  )
  private def isTarget(head: SyntaxDirectedHead): Boolean = (
    head.withParams.isEmpty &&
    targetPatterns.exists(_.matches(head.printName))
  )

  // initial abstract state for syntax-directed algorithms
  private def getTypes(head: Head): List[(List[Type], AbsState)] = head match {
    case (head: SyntaxDirectedHead) if isTarget(head) => head.optional.subsets.map(opt => {
      var st = AbsState.Empty
      val types: List[Type] = head.types.map {
        case (name, _) if opt contains name =>
          st += name -> AbsAbsent.Top
          AbsentT
        case (name, astName) =>
          st += name -> AbsAST(ASTVal(astName))
          AstT(astName)
      }
      (types, st)
    }).toList
    case _ => Nil
  }
}
