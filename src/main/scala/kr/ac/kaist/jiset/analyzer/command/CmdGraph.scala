package kr.ac.kaist.jiset.analyzer.command

import kr.ac.kaist.jiset.analyzer.NativeHelper._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.util.Useful._

// graph command
case object CmdGraph extends Command(
  "graph", "Dump the current control graph."
) {
  // options
  // val options @ List(total) = List("total")
  val options = Nil

  // run command
  def apply(
    repl: REPL,
    cp: Option[ControlPoint],
    args: List[String]
  ): Unit = cp.map(cp => dumpFunc(cp.func, pdf = true))
  // optional(args.head.toInt) match {
  //   case Some(depth) => dumpCFG(cp, depth = Some(depth))
  //   case None if args.isEmpty => dumpCFG(cp, depth = Some(0))
  //   case None if args.head == s"-$total" => dumpCFG(cp, depth = None)
  //   case None => graphFunc(args.head, args.tail)
  // }

  // TODO for functions
  // def graphFunc(
  //   fname: String,
  //   tail: List[String]
  // ): Unit = cfg.funcs.find(x => x.name == fname) match {
  //   case None => println("Inappropriate function name")
  //   case Some(func) =>
  //     if (func.complete) println("* complete function")
  //     else println("* incomplete function")
  //     val rpList = sem.getRpsForREPLByName(fname).toList.sortBy(_.view.toString)
  //     (optional(rpList(tail.head.toInt)), optional(tail.tail.head.toInt)) match {
  //       case (Some(rp), Some(depth)) => dumpCFG(Some(rp), depth = Some(depth))
  //       case (Some(rp), None) => dumpCFG(Some(rp), depth = Some(0))
  //       case (None, _) if tail.isEmpty =>
  //         dumpFunc(func)
  //         println
  //         println(s"View of function ${func.name}:")
  //         rpList.zipWithIndex.foreach {
  //           case (rp, i) => println(s"  $i: ${rp.view}")
  //         }
  //       case _ => println("Inappropriate argument")
  //     }
  // }
}
