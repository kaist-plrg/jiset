package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec

// reduce a filtered program set
case class Reducer(
  fset: FilteredProgramSet,
  loopMax: Long = 100L
) {
  // reducer loop
  @tailrec
  final def loop(iter: Long = 0): Unit = if (iter < loopMax) {
    if (LOG) {
      nfLog.println(s"========================================")
      nfLog.println(s"[loop#$iter]")
    }
    // select
    val selected = select(fset.programs)

    // reduce
    for { reduced <- reduce(selected, fset.getUniqueNIds(selected)) } {
      fset += reduced
    }

    // loop
    loop(iter + 1)
  } else nfLog.close()

  // select one js program to reduce
  def select(ps: Array[JsProgram]): JsProgram = {
    val selected = weightedChoose(ps.map(p => (p, p.size)))
    if (LOG) {
      nfLog.println(s"----------------------------------------")
      nfLog.println(s"!!! selected(size: ${selected.size})")
      nfLog.println(selected.raw)
    }
    selected
  }
  // reduce a given js program
  def reduce(p: JsProgram, nids: Set[Int]): Option[JsProgram] = {
    val reduced = RandomMutator(p, nids).mutate
    // reduced.foreach(r => println(r.raw))
    if (LOG) {
      nfLog.println(s"----------------------------------------")
      reduced match {
        case Some(r) => nfLog.println("!!! success", p.size, r.size)
        case None => nfLog.println("!!! failed")
      }
      nfLog.flush
    }
    reduced
  }

  // log
  val nfLog = getPrintWriter(s"$EDITOR_LOG_DIR/reducer.log")
}
