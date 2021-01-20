package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.checker._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.spec.algorithm.Algo
import scala.annotation.unused

// Check phase
case object Check extends PhaseObj[ECMAScript, CheckConfig, List[Bug]] {
  val name = "check"
  val help = "performs static checkers for specifications."

  def apply(
    spec: ECMAScript,
    jisetConfig: JISETConfig,
    config: CheckConfig
  ): List[Bug] = {
    println(s"--------------------------------------------------")
    val completeAlgos = spec.completeAlgos
    val targets =
      if (config.target.isEmpty) completeAlgos
      else completeAlgos.filter(config.target contains _.name)
    println(s"checking ${targets.size} algorithms...")

    println
    println(s"variable reference checking...")
    val refErrors = ReferenceChecker(spec, targets)
    refErrors.foreach(println _)
    println(s"${refErrors.length} algorithms have reference errors.")

    // println
    // println(s"duplicated variable checking...")
    // val dupErrors = DuplicatedVarChecker(spec, targets)
    // dupErrors.foreach(println _)
    // println(s"${dupErrors.length} algorithms have duplicated variable errors.")

    // println
    // println(s"unused variable checking...")
    // val unusedErrors = UnusedVarChecker(spec, targets)
    // unusedErrors.foreach(println _)
    // println(s"${unusedErrors.length} algorithms have unused variable errors.")

    // println
    // val bugs = refErrors ++ dupErrors ++ unusedErrors
    val bugs = refErrors
    println(s"Total ${bugs.length} bugs detected.")
    refErrors
  }

  def defaultConfig: CheckConfig = CheckConfig()
  val options: List[PhaseOption[CheckConfig]] = List(
    ("target", ListOption((c, l) => c.target = l),
      "target algorithms to check")
  )
}

// Check phase config
case class CheckConfig(
    var target: List[String] = Nil
) extends Config
