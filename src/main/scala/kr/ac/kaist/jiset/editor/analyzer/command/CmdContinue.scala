package kr.ac.kaist.jiset.editor.analyzer.command

import kr.ac.kaist.jiset.editor.analyzer._

// continue command
case object CmdContinue extends Command(
  "continue", "Continue static analysis."
) {
  // options
  val options: List[String] = Nil

  // run command
  def apply(
    repl: REPL,
    cpOpt: Option[ControlPoint],
    args: List[String]
  ): Unit = repl.continue = true
}
