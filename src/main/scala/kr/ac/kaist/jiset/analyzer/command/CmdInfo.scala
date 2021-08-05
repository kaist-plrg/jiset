package kr.ac.kaist.jiset.analyzer.command

import kr.ac.kaist.jiset.analyzer._

// info command
case object CmdInfo extends Command(
  "info", "Show abstract state of node"
) {
  // options
  val options @ List(ret, block) = List("ret", "block")

  // run command
  def apply(
    repl: REPL,
    cp: Option[ControlPoint],
    args: List[String]
  ): Unit = notYetCmd
  // args match {
  //   case opt :: target :: _ if options contains opt.substring(1) =>
  //     printInfo(opt.substring(1), target)
  //   case _ => println("Inappropriate option")
  // }

  // TODO print information
  // def printInfo(opt: String, target: String): Unit = {
  //   import CmdInfo._
  //   val info = opt match {
  //     case CmdInfo.ret => sem.getReturnPointByName(target)
  //     case CmdInfo.block if optional(target.toInt) != None =>
  //       sem.getNodePointsById(target.toInt)
  //     case _ => println("Inappropriate argument"); Set()
  //   }
  //   info.foreach(cp => {
  //     println(sem.getString(cp, CYAN, true))
  //     println
  //   })
  // }
}
