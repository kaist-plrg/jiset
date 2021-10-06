package kr.ac.kaist.jiset.viewer

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._

// partial evaluator for IR functions with a given syntactic view
object PartialEval {
  // TODO
  def apply(view: SyntacticView): Algo = {
    val (targetAlgo, initialValues) = view.ast.semantics("Evaluation").get
    // TODO: partial evauation of algo
    targetAlgo
  }
}
