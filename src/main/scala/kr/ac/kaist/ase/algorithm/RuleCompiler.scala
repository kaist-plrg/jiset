package kr.ac.kaist.ase.algorithm

import kr.ac.kaist.ase.core

object RuleCompiler {
  def apply(algo: Algorithm): core.Func =
    core.Func(algo.params.map(param => core.Id(param.name)), core.INotYetImpl(algo.filename))
}
