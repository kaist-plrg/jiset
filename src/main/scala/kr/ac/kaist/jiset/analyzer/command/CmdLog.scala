package kr.ac.kaist.jiset.analyzer.command

import kr.ac.kaist.jiset.analyzer._

// log command
case object CmdLog extends Command(
  "log", "Dump the state."
) {
  // options
  val options = Nil

  // TODO run command
  def apply(
    repl: REPL,
    cpOpt: Option[ControlPoint],
    args: List[String]
  ): Unit = notYetCmd
}
