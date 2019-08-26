package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.core._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.{ LINE_SEP, JISETConfig }

// REPLCore phase
case object REPLCore extends PhaseObj[State, REPLCoreConfig, Unit] {
  val name = "repl-core"
  val help = "REPL for Core syntax."

  def apply(
    st: State,
    jisetConfig: JISETConfig,
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
