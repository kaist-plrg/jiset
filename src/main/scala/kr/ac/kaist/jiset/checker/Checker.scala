package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.spec.algorithm.Algo

trait Checker {
  def apply(spec: ECMAScript, targets: List[Algo]): List[Bug]
}
