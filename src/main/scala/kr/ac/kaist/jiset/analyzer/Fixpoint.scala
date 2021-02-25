package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.cfg.{ CFG, DotPrinter }
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec

class Fixpoint(sem: AbsSemantics, interactMode: Boolean) {
  // abstract transfer function
  val transfer = new AbsTransfer(sem)

  // worklist
  val worklist = sem.worklist

  // interactive mode
  var interact = interactMode

  // fixpoint computation
  @tailrec
  final def compute: Unit = worklist.headOption match {
    case Some(cp) =>
      if (interact) {
        val dot = (new DotPrinter)(cp, sem).toString
        dumpFile(dot, s"$CFG_DIR.dot")
        executeCmd(s"""dot -Tpdf "$CFG_DIR.dot" -o "$CFG_DIR.pdf"""")
        println(sem.getString(cp))
        println
        val str = scala.io.StdIn.readLine()
        str match {
          case null | "q" | "quit" | "exit" => interact = false
          case _ =>
        }
      }
      worklist.next
      transfer(cp)
      compute
    case None =>
  }
}
