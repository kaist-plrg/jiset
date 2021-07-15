package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.{ LINE_SEP, JISETConfig }

// IRREPL phase
case object IRREPL extends Phase[State, IRREPLConfig, Unit] {
  val name = "repl-ir"
  val help = "performs REPL for IR instructions."

  def apply(
    st: State,
    jisetConfig: JISETConfig,
    config: IRREPLConfig
  ): Unit = REPL.run(st, config.detail, config.timeout)

  def defaultConfig: IRREPLConfig = IRREPLConfig()
  val options: List[PhaseOption[IRREPLConfig]] = List(
    ("detail", BoolOption(c => c.detail = true),
      "Show detailed status of the current state."),
    ("timeout", NumOption((c, i) => c.timeout = if (i == 0) None else Some(i)),
      "set timeout of interpreter(second), 0 for unlimited.")
  )
}

// IRREPL phase config
case class IRREPLConfig(
  var detail: Boolean = false,
  var timeout: Option[Long] = Some(10)
) extends Config
