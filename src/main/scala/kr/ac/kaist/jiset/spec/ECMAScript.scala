package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar._

// ECMASCript specifications
case class ECMAScript(
    grammar: Grammar,
    algos: List[Algo],
    intrinsics: Set[String],
    symbols: Set[String],
    aoids: Set[String]
) {
  // normal algorithm names
  lazy val normalAlgos: Set[String] =
    algos.collect { case Algo(normal: NormalHead, _) => normal.name }.toSet

  // global names
  lazy val globals: Set[String] = (
    ECMAScript.PREDEF ++
    normalAlgos ++
    intrinsics.map("INTRINSIC_" + _) ++
    symbols.map("SYMBOL_" + _) ++
    aoids
  )

  // complete algorithms
  lazy val completeAlgos: List[Algo] = algos.filter(_.isComplete)
}

object ECMAScript {
  // pre-defined global identifiers
  val PREDEF = Set(
    // Completion-related ECMAScript internal algorithms
    "NormalCompletion", "ThrowCompletion", "ReturnIfAbrupt", "Completion",
    // ECMAScript types
    "Type", "BigInt", "Boolean", "Null", "Number",
    "Object", "String", "Symbol", "Undefined",
    "Reference",
    // JISET specific internal algorithms
    "IsAbruptCompletion", "WrapCompletion", "GetArgument",
    // JISET specific global variables
    "GLOBAL_agent", "GLOBAL_context", "GLOBAL_symbolRegistry", "GLOBAL_executionStack",
    "REALM",
    "PRIMITIVE"
  )
}
