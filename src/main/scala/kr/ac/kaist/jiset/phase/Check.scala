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
    val CheckConfig(targetString) = config
    val targetNames: Array[String] = targetString.getOrElse("").split(",").map(_.trim())
    val targetFilter: Algo => Boolean = if (targetString != None) {
      x => targetNames contains x.name
    } else {
      x => true
    }
    println(s"--------------------------------------------------")
    val algos = spec.algos
    println(s"# algorithms: ${algos.length}")
    val targets = algos.filter(_.isComplete).filter(targetFilter)
    println(s"checking ${targets.size} algorithms...")
    val intrinsic = spec.intrinsic
    val symbols = spec.symbols

    println(s"variable reference checking...")
    val refErrors = ReferenceChecker(spec, targets)
    refErrors.foreach(println _)
    println(s"${refErrors.length} algorithms have reference errors.")

    refErrors
  }

  def defaultConfig: CheckConfig = CheckConfig()
  val options: List[PhaseOption[CheckConfig]] = List(
    ("targetString", StrOption((c, s) => c.targetString = Some(s)),
      "name of target algorithms to check, divided by comma")
  )
}

// Check phase config
case class CheckConfig(
    var targetString: Option[String] = None
) extends Config
