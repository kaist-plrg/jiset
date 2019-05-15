package kr.ac.kaist.ase

import kr.ac.kaist.ase.error.NoMode
import kr.ac.kaist.ase.model.Script
import kr.ac.kaist.ase.algorithm.Algorithm
import kr.ac.kaist.ase.phase._
import kr.ac.kaist.ase.util.ArgParser

sealed trait Command {
  val name: String
  def apply(args: List[String]): Any
}

class CommandObj[Result](
    override val name: String,
    pList: PhaseList[Result],
    modeMap: Map[String, PhaseList[Result]] = Map[String, PhaseList[Result]]()
) extends Command {
  def apply(args: List[String]): Result = {
    val aseConfig = ASEConfig(this)
    val parser = new ArgParser(this, aseConfig)
    val modePattern = "--(.+)".r
    (args match {
      case modePattern(mode) :: remain => modeMap.get(mode) match {
        case Some(pl) => (pl, remain)
        case None => throw NoMode(name, mode)
      }
      case _ => (pList, args)
    }) match {
      case (pList, args) =>
        val runner = pList.getRunner(parser)
        parser(args)
        ASE(this, runner(_), aseConfig)
    }
  }

  def display(res: Result): Unit = ()

  override def toString: String = modeMap.foldLeft(pList.toString) {
    case (str, (mode, pList)) => s"$str$LINE_SEP--$mode: " + pList.toString
  }

  def >>[C <: Config, R](phase: PhaseObj[Result, C, R]): PhaseList[R] = pList >> phase
}

// base command
case object CmdBase extends CommandObj("", PhaseNil)

// parse-algo
case object CmdParseAlgo extends CommandObj("parse-algo", CmdBase >> ParseAlgo) {
  override def display(algos: List[Algorithm]): Unit = println(algos)
}

// parse
case object CmdParse extends CommandObj("parse", CmdBase >> Parse) {
  override def display(script: Script): Unit = println(script)
}

// parse-core
case object CmdParseCore extends CommandObj("parse-core", CmdBase >> ParseCore) {
  override def display(pgm: core.Program): Unit = println(core.beautify(pgm))
}

// load-core
case object CmdLoadCore extends CommandObj("load-core", CmdParseCore >> LoadCore) {
  override def display(st: core.State): Unit = println(core.beautify(st))
}

// eval-core
case object CmdEvalCore extends CommandObj("eval-core", CmdLoadCore >> EvalCore) {
  override def display(st: core.State): Unit = println(core.beautify(st))
}

// repl-core
case object CmdREPLCore extends CommandObj("repl-core", CmdLoadCore >> REPLCore)

// load-script
case object CmdLoadGlobal extends CommandObj("load-script", CmdBase >> LoadGlobal)

// help
case object CmdHelp extends CommandObj("help", CmdBase >> Help)
