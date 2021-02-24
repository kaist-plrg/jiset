package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.DEBUG
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.cfg.{ CFG, DotPrinter }
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util.Useful._

class Fixpoint(sem: AbsSemantics, interactMode: Boolean) {
  // abstract transfer function
  val transfer = new AbsTransfer(sem)

  // worklist
  val worklist = sem.worklist

  // interactive mode
  var interact = interactMode

  // fixpoint computation
  def compute: Unit = worklist.headOption.map(cp => {
    if (interact) {
      val dot = (new DotPrinter)(cp, sem).toString
      dumpFile(dot, s"$CFG_DIR.dot")
      executeCmd(s"""dot -Tpdf "$CFG_DIR.dot" -o "$CFG_DIR.pdf"""")
      println(sem.getString(cp))
      println
      if (scala.io.StdIn.readLine == null) interact = false
    }
    worklist.next
    transfer(cp)
    compute
  })
}
