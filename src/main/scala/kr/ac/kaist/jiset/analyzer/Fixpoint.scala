package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg.CFG

class Fixpoint(
    cfg: CFG,
    worklist: Worklist[ControlPoint],
    sem: AbsSemantics
) {
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
