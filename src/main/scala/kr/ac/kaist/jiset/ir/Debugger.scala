package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.{ DEBUG, TIMEOUT }
import scala.annotation.tailrec

// IR runtime
trait Debugger {
  val st: State
  val interp = new Interp(st)

  // step-over
  final def stepOver: Boolean = ???

  // step-out
  final def stepOut: Boolean = ???
}
