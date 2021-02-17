package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.InfNum
import kr.ac.kaist.jiset.util.Conversion._

// ECMASCript specifications
case class ECMAScript(
  grammar: Grammar,
  algos: List[Algo],
  intrinsics: Set[String],
  symbols: Set[String],
  aoids: Set[String],
  section: Section
) {
  // normal algorithm names
  lazy val normalAlgos: Set[String] =
    algos.collect { case Algo(normal: NormalHead, _, _) => normal.name }.toSet

  // global names
  lazy val globals: Set[String] = (
    ECMAScript.PREDEF ++
    normalAlgos ++
    intrinsics.map("INTRINSIC_" + _) ++
    symbols.map("SYMBOL_" + _) ++
    aoids
  )

  // arity counter of algorithms
  lazy val arities: Map[String, (InfNum, InfNum)] =
    algos.map(a => (a.name, a.arity)).toMap ++ ECMAScript.PREDEF_FUNC

  // completed/incompleted algorithms
  lazy val (completedAlgos, incompletedAlgos): (List[Algo], List[Algo]) =
    algos.partition(_.isComplete)
}

object ECMAScript {
  //  pre-defined algorithms
  val PREDEF_FUNC = Map[String, (InfNum, InfNum)](
    // ECMAScript types
    "Type" -> (1, 1),
    // Completion-related ECMAScript internal algorithms
    "NormalCompletion" -> (1, 1),
    "ThrowCompletion" -> (1, 1),
    "ReturnIfAbrupt" -> (1, 1),
    "Completion" -> (1, 1),
    // JISET specific internal algorithms
    "IsAbruptCompletion" -> (1, 1),
    "WrapCompletion" -> (1, 1),
    "GetArgument" -> (2, 2),
    // TEMPORARY -> should be extracted in later
    "Number::exponentiate" -> (2, 2),
    "Number::mutiply" -> (2, 2),
    "Number::divide" -> (2, 2),
    "Number::remainder" -> (2, 2),
    "Number::add" -> (2, 2),
    "BigInt::bitwiseNOT" -> (2, 2),
    "BigInt::mutiply" -> (2, 2),
    "BigInt::add" -> (2, 2),
    "BigInt::subtract" -> (2, 2)
  )

  // pre-defined global identifiers
  val PREDEF = Set(
    // ECMAScript types
    "BigInt", "Boolean", "Null", "Number",
    "Object", "String", "Symbol", "Undefined",
    "Reference",
    // JISET specific global variables
    "GLOBAL_agent", "GLOBAL_context", "GLOBAL_symbolRegistry", "GLOBAL_executionStack",
    "REALM",
    "PRIMITIVE"
  ) ++ PREDEF_FUNC.keySet

}
