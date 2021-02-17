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
      val init = Initialize(head)
      val entry = func.entry
      val view = FlowView(entry)
      val cp = ControlPoint(entry, view)
      sem += view -> init
      worklist.push(cp)
    case _ =>
  })

  // abstract transfer function
  val transfer = new AbsTransfer(cfg)

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
