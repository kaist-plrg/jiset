package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast._

object Initialize {
  // the initial pair of control points and abstract states
  def apply(script: Script): (NodePoint[Entry], AbsState) =
    initCp -> initSt(script)

  // initial control point
  lazy val initCp = {
    val runJobs = cfg.algoFuncMap("RunJobs")
    val entry = runJobs.entry
    NodePoint(entry, BaseView)
  }

  // initial state
  def initSt(script: Script): AbsState = script match {
    case Script0(Some(body), _, _) =>
      baseSt.defineGlobal(Id(SCRIPT_BODY), AbsValue(body))
    case _ => baseSt
  }

  // base initial state
  lazy val baseSt: AbsState = AbsState()
}
