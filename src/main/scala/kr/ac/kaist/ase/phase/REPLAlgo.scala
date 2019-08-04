package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.algorithm._
import kr.ac.kaist.ase.util.BoolOption
import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }

// REPLAlgo phase
case object REPLAlgo extends PhaseObj[Unit, REPLAlgoConfig, Unit] {
  val name = "repl-algo"
  val help = "REPL for algorithms."

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: REPLAlgoConfig
  ): Unit = REPL.run(config.onlyFailed)

  def defaultConfig: REPLAlgoConfig = REPLAlgoConfig()
  val options: List[PhaseOption[REPLAlgoConfig]] = List(
    ("only-failed", BoolOption(c => c.onlyFailed = true),
      "REPL with only failed steps.")
  )
}

// REPLAlgo phase config
case class REPLAlgoConfig(
  var onlyFailed: Boolean = false
) extends Config
