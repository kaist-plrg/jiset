package kr.ac.kaist.jiset.analyzer.command

import kr.ac.kaist.jiset.analyzer._
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
    cp: Option[ControlPoint],
    args: List[String]
  ): Unit = repl.stop
}
