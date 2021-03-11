package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.InfNum

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
  lazy val normalAlgos: List[Algo] =
    algos.collect { case algo @ Algo(normal: NormalHead, _, _) => algo }

  // syntax-directed algorithms
  lazy val syntaxAlgos: List[Algo] =
    algos.collect { case algo @ Algo(head: SyntaxDirectedHead, _, _) => algo }

  // global names
  lazy val globals: Set[String] = (
    ECMAScript.PREDEF ++
    normalAlgos.map(_.name) ++
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

  // get syntax directed algos
  def getSyntaxAlgo(lhs: String, method: String): Set[String] = {
    // exclude index
    var excludes = Set[Int]()
    val rhsList = grammar.nameMap(lhs).rhsList
    // direct
    val direct = syntaxAlgos.filter {
      case Algo(head: SyntaxDirectedHead, _, _) =>
        val isTarget =
          head.lhsName == lhs && head.methodName == method
        if (isTarget) excludes += head.idx
        isTarget
      case _ => ??? // impossible
    }.map(_.name).toSet

    // chain
    val chain: Set[String] = (for {
      idx <- 0 until rhsList.length
      if !excludes.contains(idx)
      rhs = rhsList(idx)
      if rhs.isSingleNT
    } yield getSyntaxAlgo(rhs.name, method)).flatten.toSet

    direct ++ chain
  }
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
