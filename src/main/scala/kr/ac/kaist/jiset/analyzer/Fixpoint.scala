package kr.ac.kaist.jiset.analyzer

class Fixpoint(
    worklist: Worklist[ControlPoint],
    sem: AbsSemantics
) {
  // fixpoint computation
  def compute: Unit = while (!worklist.isEmpty) step

  // abstract one-step execution using worklist
  def step: Unit = {
    val cp = worklist.pop
    val st = sem(cp.view)
    AbsTransfer(cp, st).foreach {
      case (cp, newSt) =>
        val view = cp.view
        val oldSt = sem(view)
        if (!(newSt ⊑ oldSt)) {
          sem += view -> (oldSt ⊔ newSt)
          worklist.push(cp)
        }
    }
  }
}
