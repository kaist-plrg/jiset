package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.{ cfg => CFG, _ }
import scala.annotation.tailrec

// reduce a filtered program set
object Reducer {
  def apply(fset: FilteredProgramSet): Unit = loop(fset)

  // constants
  var LOOP_MAX = 0L

  // reducer loop
  @tailrec
  private def loop(
    fset: FilteredProgramSet,
    iter: Long = 0
  ): Unit = if (iter < LOOP_MAX) {
    var selected = select(fset.programs)
    for { reduced <- reduce(selected, fset.getUniqueNIds(selected)) } {
      fset += reduced
    }
    loop(fset, iter + 1)
  }

  // select one js program to reduce
  def select(ps: Array[JsProgram]): JsProgram = ???

  // reduce a js program
  def reduce(p: JsProgram, nids: Set[Int]): Option[JsProgram] = ???
}
