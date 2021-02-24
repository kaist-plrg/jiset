package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg.CFG
import kr.ac.kaist.jiset.spec.algorithm._

class Fixpoint(sem: AbsSemantics) {
  // CFG
  val cfg = sem.cfg

  // TODO target algorithms
  def isTarget(head: SyntaxDirectedHead): Boolean = (
    head.withParams.isEmpty && (
      head.printName.startsWith("Literal[")
    )
  )

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
  def compute: Unit = worklist.next.map(cp => {
    println(sem.getString(cp))
    println
    transfer(cp)
    compute
  })
}
