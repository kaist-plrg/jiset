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
    algos.collect { case algo @ Algo(normal: NormalHead, _, _, _) => algo }

  // syntax-directed algorithms
  lazy val syntaxAlgos: List[(Algo, SyntaxDirectedHead)] = algos.collect {
    case algo @ Algo(head: SyntaxDirectedHead, _, _, _) => (algo, head)
  }

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
    var names = Set[String]()
    var visited = Set[String]()
    def aux(lhs: String): Unit = if (!(visited contains lhs)) {
      visited += lhs
      val rhsList = grammar.nameMap(lhs).rhsList
      var excludes = Set[Int]()

      // direct
      for ((algo, head) <- syntaxAlgos) {
        if (head.lhsName == lhs && head.methodName == method) {
          excludes += head.idx
          names += algo.name
        }
      }

      // chain
      for {
        idx <- 0 until rhsList.length
        if !(excludes contains idx)
        rhs = rhsList(idx)
        nt <- rhs.toNTs match {
          case List(nt) => Some(nt)
          case _ => None
        }
      } aux(nt.name)
    }

    aux(lhs)
    names
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
