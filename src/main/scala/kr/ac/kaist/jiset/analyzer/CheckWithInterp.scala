package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.NativeHelper._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.util.Useful._

class CheckWithInterp(
  sem: AbsSemantics,
  interp: Interp
) {
  // run and check the soundness
  def runAndCheck: Unit = sem.worklist.headOption match {
    case None => {
      run
      nodeOpt.map(node => {
        fail(s"${node.uidString} is wanted but the worklist is empty.")
      })
    }
    case Some(ReturnPoint(_, _)) =>
    case Some(np @ NodePoint(anode, _)) => {
      run
      nodeOpt.map(node => if (node != anode) fail(
        s"${node.uidString} is not same with ${anode.uidString}.",
        np.func
      ))
    }
  }

  def run = interp.step
  def st = interp.st
  def nodeOpt = st.context.cursorOpt match {
    case Some(NodeCursor(node)) => Some(node)
    case _ => None
  }
  def fail(msg: String): Unit = fail(msg, None)
  def fail(msg: String, func: Function): Unit = fail(msg, Some(func))
  def fail(msg: String, funcOpt: Option[Function]): Unit = {
    funcOpt.map(func => dumpFunc(func, pdf = true))
    println(s"# iter: ${sem.getIter}")
    error(msg)
  }
}
object CheckWithInterp {
  def apply(sem: AbsSemantics, script: ast.Script): CheckWithInterp = {
    val initSt = Initialize(script)
    val interp = new Interp(initSt)
    new CheckWithInterp(sem, interp)
  }
}
