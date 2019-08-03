package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.algorithm._
import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }

// REPLAlgo phase
case object REPLAlgo extends PhaseObj[Unit, REPLAlgoConfig, Unit] {
  val name = "repl-algo"
  val help = "REPL for algorithms."

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: REPLAlgoConfig
  ): Unit = REPL.run

  def defaultConfig: REPLAlgoConfig = REPLAlgoConfig()
  val options: List[PhaseOption[REPLAlgoConfig]] = Nil
}

// REPLAlgo phase config
case class REPLAlgoConfig() extends Config
