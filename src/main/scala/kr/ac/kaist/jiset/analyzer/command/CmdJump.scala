package kr.ac.kaist.jiset.analyzer.command

import kr.ac.kaist.jiset.analyzer._

// jump command
case object CmdJump extends Command(
  "jump", "Jump to a specific iteration."
) {
  // options
  val options @ List(merged) = List("merged")

  // run command
  def apply(
    repl: REPL,
    cp: Option[ControlPoint],
    args: List[String]
  ): Unit = args match {
    case s"-$merged" :: _ =>
      repl.untilMerged = true; repl.continue = true
    case arg :: _ =>
      val iter = arg.toInt
      if (iter > repl.iter) { repl.jumpTo = Some(iter); repl.continue = true }
      else println(s"The iteration [$iter] is already passed.")
    case _ => println("Inappropriate argument")
  }
}
