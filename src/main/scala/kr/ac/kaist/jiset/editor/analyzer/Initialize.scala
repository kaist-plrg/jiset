package kr.ac.kaist.jiset.editor.analyzer

import kr.ac.kaist.jiset.editor.analyzer.domain._
import kr.ac.kaist.jiset.editor.SyntacticView
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js
import kr.ac.kaist.jiset.js.ast._

object Initialize {
  // the initial pair of control points and abstract states
  def apply(func: Function, locals: Map[Id, AbsValue]): (NodePoint[Entry], AbsState) =
    initCp(func) -> initSt(func, locals)

  // initial control point
  def initCp(func: Function) = {
    val entry = func.entry
    NodePoint(entry)
  }

  // initial state
  def initSt(func: Function, locals: Map[Id, AbsValue]): AbsState =
    baseSt.copy(locals = locals)

  // base initial state
  lazy val baseSt: AbsState = AbsState.Empty
}
