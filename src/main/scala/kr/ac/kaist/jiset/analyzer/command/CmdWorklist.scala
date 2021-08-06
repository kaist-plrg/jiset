package kr.ac.kaist.jiset.analyzer.command

import kr.ac.kaist.jiset.analyzer._

// worklist command
case object CmdWorklist extends Command(
  "worklist", "Show all the control points in the worklist"
) {
  // options
  val options @ List(detail) = List("detail")

  // run command
  def apply(
    repl: REPL,
    cp: Option[ControlPoint],
    args: List[String]
  ): Unit = {
    val worklist = repl.sem.worklist
    val size = worklist.size
    println(s"Total $size elements exist in the worklist.")
    args match {
      case s"-$detail" :: _ => repl.sem.worklist.foreach(println(_))
      case _ =>
    }
  }
}
