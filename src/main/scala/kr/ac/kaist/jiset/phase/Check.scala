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

    println
    println(s"branch checking...")
    val branchErrors = MissingRetChecker(spec, targets)
    branchErrors.foreach(println _)
    println(s"${branchErrors.length} algorithms have branch errors")

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
