package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.{ ASE, ASEConfig }

// Help phase
case object Help extends PhaseObj[Unit, HelpConfig, Unit] {
  val name = "help"
  val help = ""

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: HelpConfig
  ): Unit = println(ASE.help)
  def defaultConfig: HelpConfig = HelpConfig()
  val options: List[PhaseOption[HelpConfig]] = Nil
}

case class HelpConfig() extends Config
