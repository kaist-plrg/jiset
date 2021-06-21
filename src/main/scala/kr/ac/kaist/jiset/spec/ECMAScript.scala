package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.extractor.ECMAScriptParser
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Useful._

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

  // dump ECMAScript to directory
  def dumpTo(dirname: String): Unit = {
    mkdir(dirname)
    dumpFile(grammar, s"$dirname/grammar")
    mkdir(s"$dirname/algorithm")
    for (algo <- algos) dumpFile(algo, s"$dirname/algorithm/${algo.name}")
    dumpFile(consts.toList.sorted.mkString(LINE_SEP), s"$dirname/consts")
    dumpFile(intrinsics.toList.sorted.mkString(LINE_SEP), s"$dirname/intrinsics")
    dumpFile(symbols.toList.sorted.mkString(LINE_SEP), s"$dirname/symbols")
    dumpFile(aoids.toList.sorted.mkString(LINE_SEP), s"$dirname/aoids")
  }
}
