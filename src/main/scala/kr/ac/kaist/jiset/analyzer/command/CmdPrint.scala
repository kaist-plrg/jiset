package kr.ac.kaist.jiset.analyzer.command

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.ir.Expr
import kr.ac.kaist.jiset.util.Useful._

// print command
case object CmdPrint extends Command(
  "print", "Print specific information"
) {
  // options
  val options @ List(reachLoc, expr) = List("reach-loc", "expr")

  // run command
  def apply(
    repl: REPL,
    cpOpt: Option[ControlPoint],
    args: List[String]
  ): Unit = args match {
    case s"-${ `reachLoc` }" :: _ => cpOpt.map { cp =>
      val st = repl.sem.getState(cp)
      st.reachableLocs.foreach(println _)
    }
    case s"-${ `expr` }" :: str :: _ => cpOpt.map { cp =>
      val v = repl.sem.transfer(cp, Expr(str))
      // TODO get string for abstract value
      println(v)
    }
  }
}
