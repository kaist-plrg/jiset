package kr.ac.kaist.jiset.analyzer.state

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.analyzer.state._

case class Ctxt(globals: Env, locals: Env, retVal: Value)
