package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.node.core._
import kr.ac.kaist.ase.util._
import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }

// REPLCore phase
case object REPLCore extends PhaseObj[State, REPLCoreConfig, Unit] {
  val name = "repl-core"
  val help = "REPL for Core syntax."

  def apply(
    st: State,
    aseConfig: ASEConfig,
    config: REPLCoreConfig
  ): Unit = REPL.run(st, config.detail)

  def defaultConfig: REPLCoreConfig = REPLCoreConfig()
  val options: List[PhaseOption[REPLCoreConfig]] = List(
    ("detail", BoolOption(c => c.detail = true),
      "Show detailed status of the current state.")
  )
}

// REPLCore phase config
case class REPLCoreConfig(
  var detail: Boolean = false
) extends Config
