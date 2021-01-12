package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.checker._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._

// Check phase
case object Check extends PhaseObj[ECMAScript, CheckConfig, Unit] {
  val name = "parse"
  val help = "performs static checkers for specifications."

  def apply(
    spec: ECMAScript,
    jisetConfig: JISETConfig,
    config: CheckConfig
  ): Unit = {
    val algos = spec.algos
    val targets = algos.filter(_.isComplete)
    println(s"checking ${targets.size} algorithms...")

    println(s"variable reference checking...")
    val names = algos.map(_.name).toSet
    val failed = targets.filter(!ReferenceChecker(names, _))
    println(s"${failed.length} algorithms have reference errors.")
  }

  def defaultConfig: CheckConfig = CheckConfig()
  val options: List[PhaseOption[CheckConfig]] = List()
}

// Check phase config
case class CheckConfig() extends Config
