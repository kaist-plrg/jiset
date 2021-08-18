package kr.ac.kaist.jiset.analyzer.command

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.cfg._
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
  ): Unit = {
    val cp = cpOpt.getOrElse(repl.sem.runJobsRp)
    args match {
      case s"-${ `reachLoc` }" :: _ => {
        val st = repl.sem.getState(cp)
        st.reachableLocs.foreach(println _)
      }
      case s"-${ `expr` }" :: str :: _ => {
        val sem = repl.sem
        val v = sem.transfer(cp, Expr(str))
        val st = cp match {
          case np: NodePoint[Node] => sem(np)
          case rp: ReturnPoint => sem(rp).state
        }
        println(st.getString(v))
      }
      case _ => println("Inappropriate argument")
    }
  }
}
