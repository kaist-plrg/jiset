package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg.CFG
import kr.ac.kaist.jiset.spec.algorithm._

class Fixpoint(
  cfg: CFG,
  worklist: Worklist[ControlPoint],
  sem: AbsSemantics
) {
  // initial worklist
  cfg.allFunctions.foreach(func => func.algo.head match {
    case (head: SyntaxDirectedHead) if head.withParams.isEmpty =>
      val entry = func.entry
      Initialize(head).foreach {
        case (types, st) =>
          val view = View(entry, types)
          val cp = ControlPoint(entry, view)
          sem += view -> st
          worklist.push(cp)
      }
    case _ =>
  })

  // abstract transfer function
  val transfer = new AbsTransfer(cfg, sem)

  // fixpoint computation
  def compute: Unit = while (!worklist.isEmpty) step

  // abstract one-step execution using worklist
  def step: Unit = {
    val cp = worklist.pop
    val st = sem(cp.view)
    transfer(st, cp).foreach {
      case Result(cp, newSt) =>
        val view = cp.view
        val oldSt = sem(view)
        if (!(newSt ⊑ oldSt)) {
          sem += view -> (oldSt ⊔ newSt)
          worklist.push(cp)
        }
    }
  }
}
