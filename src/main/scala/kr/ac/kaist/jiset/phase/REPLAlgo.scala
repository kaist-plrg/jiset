package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.algorithm._
import kr.ac.kaist.jiset.util.BoolOption
import kr.ac.kaist.jiset.{ LINE_SEP, JISETConfig }

// REPLAlgo phase
case object REPLAlgo extends PhaseObj[Unit, REPLAlgoConfig, Unit] {
  val name = "repl-algo"
  val help = "REPL for algorithms."

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: REPLAlgoConfig
  ): Unit = REPL.run(config.onlyFailed, config.onlyLanguage)

  def defaultConfig: REPLAlgoConfig = REPLAlgoConfig()
  val options: List[PhaseOption[REPLAlgoConfig]] = List(
    ("only-failed", BoolOption(c => c.onlyFailed = true),
      "REPL with only failed steps."),
    ("only-lang", BoolOption(c => c.onlyLanguage = true),
      "REPL with only steps in core Language parts.")
  )
}

// REPLAlgo phase config
case class REPLAlgoConfig(
    var onlyFailed: Boolean = false,
    var onlyLanguage: Boolean = false
) extends Config
