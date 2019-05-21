package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core
import kr.ac.kaist.ase.algorithm.Algorithm

object AlgoCompiler {
  def apply(algo: Algorithm): core.Func =
    core.Func(algo.params.map(core.Id(_)), core.INotYetImpl(algo.filename))
}
