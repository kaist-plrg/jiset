package kr.ac.kaist.jiset.analyzer.command

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.util.Useful._

// info command
case object CmdInfo extends Command(
  "info", "Show abstract state of node"
) {
  // options
  val options @ List(ret, block) = List("ret", "block")

  // run command
  def apply(
    repl: REPL,
    cpOpt: Option[ControlPoint],
    args: List[String]
  ): Unit = args match {
    case opt :: target :: _ if options contains opt.substring(1) =>
      showInfo(repl, opt.substring(1), target)
    case _ => cpOpt match {
      case Some(cp) =>
        println(repl.cpInfo(cp, false))
        println
      case None =>
        showInfo(repl, ret, "RunJobs")
    }
  }

  // show information
  def showInfo(repl: REPL, opt: String, target: String): Unit = {
    val sem = repl.sem
    val info = opt match {
      case `ret` =>
        val fname = target
        sem.rpMap.keySet.filter(_.func.name == fname)
      case `block` if optional(target.toInt) != None =>
        val uid = target.toInt
        sem.npMap.keySet.filter(_.node.uid == uid)
      case _ =>
        println("Inappropriate argument")
        Set()
    }
    info.foreach(cp => { println(repl.cpInfo(cp, true)); println })
  }
}
