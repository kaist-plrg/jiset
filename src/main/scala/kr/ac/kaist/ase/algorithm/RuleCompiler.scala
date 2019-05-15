package kr.ac.kaist.ase.algorithm

import kr.ac.kaist.ase.core.{ Id => CId, _ }

object RuleCompiler {
  def apply(f: Algorithm): Func = f match {
    case Algorithm(pstrs, stepList) => {
      val body = stepList.steps.foldLeft(List[Inst]()) {
        case (a, step) => a ++ StepCompiler(step)
      }
      val params = pstrs.map(param => CId(param.name))
      Func(params, ISeq(body))
    }
  }
}
object StepCompiler {
  def apply(f: Step): List[Inst] = f match {
    case _ => List(INotYetImpl("err"))
  }
}
