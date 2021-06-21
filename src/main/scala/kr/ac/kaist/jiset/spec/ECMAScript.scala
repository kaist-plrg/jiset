package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.extractor.ECMAScriptParser
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar._

// ECMASCript specifications
case class ECMAScript(
  grammar: Grammar,
  algos: List[Algo],
  consts: Set[String],
  intrinsics: Set[String],
  symbols: Set[String],
  aoids: Set[String],
  section: Section
) {
  // completed/incompleted algorithms
  lazy val (completedAlgos, incompletedAlgos): (List[Algo], List[Algo]) =
    algos.partition(_.isComplete)
}
