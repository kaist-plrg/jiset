package kr.ac.kaist.jiset.analyzer.command

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.util.Useful._

// print command
case object CmdPrint extends Command(
  "print", "Print specific information"
) {
  // options
  val options @ List(reachLoc) = List("reach-loc")

  // run command
  def apply(
    repl: REPL,
    cpOpt: Option[ControlPoint],
    args: List[String]
  ): Unit = args match {
    case s"-${ `reachLoc` }" :: _ => cpOpt.map {
      case np: NodePoint[_] => repl.sem(np).reachableLocs.foreach(println _)
      case rp: ReturnPoint => repl.sem(rp).state.reachableLocs.foreach(println _)
    }
  }
}
