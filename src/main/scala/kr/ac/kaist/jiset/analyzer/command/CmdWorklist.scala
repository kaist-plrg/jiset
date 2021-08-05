package kr.ac.kaist.jiset.analyzer.command

import kr.ac.kaist.jiset.analyzer._

// worklist command
case object CmdWorklist extends Command(
  "worklist", "Show all the control points in the worklist"
) {
  // options
  val options = Nil

  // run command
  def apply(
    repl: REPL,
    cp: Option[ControlPoint],
    args: List[String]
  ): Unit = notYetCmd
  // TODO worklist.foreach(println(_)); true
}
