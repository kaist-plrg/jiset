package kr.ac.kaist.ase.node.algorithm
import kr.ac.kaist.ase.node.core.{ Id => CId, _ }
import kr.ac.kaist.ase.node.core.Beautifier

object RuleCompiler {
  def apply(f: Algorithm): Func = f match {
    case Algorithm(pstrs, steps) => {
      val body = steps.foldLeft(List[Inst]()) {
        case (a, step) => a ++ StepCompiler(step)
      }
      val params = pstrs.map(CId(_))
      Func(params, ISeq(body))
    }
  }
}
object StepCompiler {
  def apply(f: Step): List[Inst] = f match {
    case Stmt0(e0, e1) => List(INotYetImpl("assign"))
    case _ => List(INotYetImpl("err"))
  }
}