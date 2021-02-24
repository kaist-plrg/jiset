package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.DEBUG
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.cfg.{ CFG, DotPrinter }
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util.Useful._

class Fixpoint(sem: AbsSemantics, interactMode: Boolean) {
  // CFG
  val cfg = sem.cfg

  // interactive mode
  var interact = interactMode

  // TODO target algorithms
  def isTarget(head: SyntaxDirectedHead): Boolean = {
    val patterns = List(
      """Literal\[.*""".r,
      """PrimaryExpression.*IsIdentifierRef""".r,
    )
    head.withParams.isEmpty && patterns.exists(_.matches(head.printName))
  }

  // initialization
  cfg.funcs.foreach(func => func.algo.head match {
    case (head: SyntaxDirectedHead) if isTarget(head) =>
      val entry = func.entry
      Initialize(head).foreach {
        case (types, st) =>
          val view = View(types)
          val cp = NodePoint(entry, view)
          sem += cp -> st
      }
    case _ =>
  })

  // abstract transfer function
  val transfer = new AbsTransfer(sem)

  // worklist
  val worklist = sem.worklist

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
