package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.checker._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.spec.algorithm.Algo

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
    val algos = spec.algos
    println(s"# algorithms: ${algos.length}")
    val completeAlgos = spec.completeAlgos
    println(s"# complete algorithms: ${completeAlgos.length}")
    val targets =
      if (config.target.isEmpty) completeAlgos
      else completeAlgos.filter(config.target contains _.name)
    println(s"checking ${targets.size} algorithms...")
    val intrinsic = spec.intrinsic
    val symbols = spec.symbols

    println
    println(s"variable reference checking...")
    val refErrors = ReferenceChecker(spec, targets)
    refErrors.foreach(println _)
    println(s"${refErrors.length} algorithms have reference errors.")

    println
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
