package kr.ac.kaist.jiset.editor.analyzer.command

import kr.ac.kaist.jiset.editor.analyzer._
import kr.ac.kaist.jiset.util.Useful._

// stop command
case object CmdStop extends Command(
  "stop", "Stop the repl."
) {
  // options
  val options = Nil

  // run command
  def apply(
    repl: REPL,
    cpOpt: Option[ControlPoint],
    args: List[String]
  ): Unit = repl.stop
}
