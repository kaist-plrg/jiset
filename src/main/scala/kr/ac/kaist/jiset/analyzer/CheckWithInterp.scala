package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.TEST_MODE
import kr.ac.kaist.jiset.analyzer.NativeHelper._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.util.Useful._

case class CheckWithInterp(
  val sem: AbsSemantics,
  val interp: Interp,
  val execLevel: Int
) {
  // run and check the soundness
  def runAndCheck: Unit = {
    // run concrete execution
    cpOpt match {
      case Some(ReturnPoint(_, _)) =>
      case _ => interp.step
    }

    // soundness check
    nodeOpt.map(node => absStOpt match {
      // unexpected terminations
      case None => if (worklist.isEmpty) fail(
        s"${node.uidString} is wanted but the worklist is empty."
      )
      // unexpected control flows
      case Some((NodePoint(absNode, _), _)) if node != absNode => fail(
        s"${node.uidString} is not same with ${absNode.uidString}.",
        absNode
      )
      case Some((np, absSt)) => if (execLevel >= 2) {
        // singletone check
        if (!absSt.isSingle) fail(
          "the abstract state is not single",
          np.func
        )
        if (!CheckWithInterp.check(np, absSt, st)) fail(
          "the abstract state is not sound",
          np.func
        )
      }
    })
  }

  def worklist = sem.worklist
  def cpOpt = worklist.headOption
  def absStOpt = cpOpt match {
    case Some(np: NodePoint[Node]) => Some((np, sem(np)))
    case _ => None
  }
  def st = interp.st
  def nodeOpt = st.context.cursorOpt match {
    case Some(NodeCursor(node, _)) => Some(node)
    case _ => None
  }
  def fail(msg: String): Unit = fail(msg, None)
  def fail(msg: String, node: Node): Unit = fail(msg, Some(cfg.funcOf(node)))
  def fail(msg: String, func: Function): Unit = fail(msg, Some(func))
  def fail(msg: String, funcOpt: Option[Function]): Unit = {
    if (TEST_MODE) error(msg) else warn(msg)
    sem.repl.continue = false
  }
}
object CheckWithInterp {
  def apply(
    sem: AbsSemantics,
    script: ast.Script,
    execLevel: Int,
    timeLimit: Option[Long] = Some(ANALYZE_TIMEOUT)
  ): CheckWithInterp = {
    val initSt = Initialize(script)
    val interp = new Interp(initSt, timeLimit = None)
    CheckWithInterp(sem, interp, execLevel)
  }

  // check soundness of abstract state
  def checkTy(lty: Ty, rty: Ty): Boolean =
    (lty == rty) || { println(s"different type: $lty != $rty"); false }
  def checkLength[T](l: Vector[T], r: Vector[T]): Boolean = (
    l.length == r.length
  ) || {
      println(s"different length: $l (${l.length}) != $r (${r.length})")
      false
    }
  def check(np: ControlPoint, absSt: AbsState, st: State): Boolean = {
    val AbsState(reachable, absLocals, absGlobals, absHeap) = absSt
    val State(_, context, _, globals, heap, _) = st
    val locals = context.locals
    var visited = Set[Loc]()

    def checkValue(absValue: AbsValue, value: Value): Boolean = {
      absValue.getSingle match {
        case FlatElem(avalue) => checkSingleValue(avalue, value)
        case _ => false
      }
    }
    def checkSingleValue(avalue: AValue, value: Value): Boolean = (value match {
      case _: AComp | _: Const | _: Func | _: ASTVal | _: SimpleValue =>
        avalue == AValue.from(value)
      case addr: Addr => avalue match {
        case loc: Loc =>
          checkLoc(loc, addr)
        case _ => false
      }
      case _ => true
    }) || { println(s"$avalue != $value"); false }
    def checkLoc(loc: Loc, addr: Addr): Boolean = {
      if (visited contains loc) true
      else if (!absSt.heap.map.contains(loc)) {
        if (loc.isNamed) { visited += loc; true }
        else { println(s"$loc is required but removed."); false }
      } else {
        visited += loc
        val absObj = absSt(loc)
        val obj = st(addr)
        import AbsObj._
        (absObj, obj) match {
          case (Bot | MergedMap(_, _, _) | MergedList(_), _) => false
          case (SymbolElem(adesc), IRSymbol(desc)) => checkValue(adesc, desc)
          case (am @ OrderedMap(aty, _, _), m @ IRMap(ty, _, _)) => checkTy(aty, ty) && {
            am.keys(intSorted = false) match {
              case MergedList(_) => false
              case KeyWiseList(aprops) => {
                val props = m.keys(intSorted = false)
                val lengthB = checkLength(aprops, props)
                val propsB = (aprops zip props).forall {
                  case (aprop, prop) => (
                    checkValue(aprop, prop) &&
                    checkValue(am(aprop), m(prop))
                  ) || {
                      println(aprops, props)
                      println(s"$loc[$aprop] != $addr[$prop]")
                      false
                    }
                }
                lengthB && propsB
              }
              case _ => false
            }
          }
          case (KeyWiseList(avalues), IRList(values)) => {
            checkLength(avalues, values) && (avalues zip values).forall {
              case (aprop, prop) => checkValue(aprop, prop)
            }
          }
          case (NotSupportedElem(aty, adesc), IRNotSupported(tyname, desc)) =>
            checkTy(aty, Ty(tyname)) && adesc == desc
          case (absObj, irObj) =>
            println(s"$absObj != $irObj")
            false
        }
      }
    }

    val localCheck = (locals.keySet ++ absLocals.keySet).forall(x => (
      checkValue(absSt(x, np), st(x)) ||
      { println(s"local variable $x is not sound."); false }
    ))

    val globalCheck = (globals.keySet ++ absGlobals.keySet).forall(x => (
      checkValue(absSt(x, np), st(x)) ||
      { println(s"global variable $x is not sound."); false }
    ))

    reachable && localCheck && globalCheck
  }
}
