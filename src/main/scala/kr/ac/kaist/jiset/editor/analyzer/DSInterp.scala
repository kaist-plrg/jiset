package kr.ac.kaist.jiset.editor.analyzer

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Useful._

class DSInterp(st: State, timeLimit: Option[Long], useHook: Boolean) extends Interp(st, timeLimit, useHook) {

  override def interp(ref: Ref): RefValue = ref match {
    case RefId(id) => if (st.locals contains id) RefValueId(id) else (error("global access"))
    case RefProp(ref, expr) => { error("heap access") }
  }
}

object DSInterp {
  def apply(
    st: State,
    timeLimit: Option[Long] = Some(100)
  ): State = {
    val interp = new DSInterp(st, timeLimit, false)
    interp.fixpoint
    st
  }
}