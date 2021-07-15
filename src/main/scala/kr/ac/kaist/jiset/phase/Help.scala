package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.{ JISET, JISETConfig }

// Help phase
case object Help extends Phase[Unit, HelpConfig, Unit] {
  val name = "help"
  val help = "shows help messages."

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: HelpConfig
  ): Unit = println(JISET.help)
  def defaultConfig: HelpConfig = HelpConfig()
  val options: List[PhaseOption[HelpConfig]] = Nil
}

case class HelpConfig() extends Config
