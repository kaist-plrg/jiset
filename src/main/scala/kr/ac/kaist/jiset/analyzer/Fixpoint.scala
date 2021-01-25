package kr.ac.kaist.jiset.analyzer

class Fixpoint(
    worklist: Worklist[View],
    sem: AbsSemantics
) {
  def compute: Unit = while (!worklist.isEmpty) step
  def step: Unit = {
    val view = worklist.pop
    val st = sem(view)
    AbsTransfer(view, st).foreach {
      case (view, newSt) =>
        val oldSt = sem(view)
        if (!(newSt ⊑ oldSt)) {
          sem += view -> (oldSt ⊔ newSt)
          worklist.push(view)
        }
    }
  }
}
