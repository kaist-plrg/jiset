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
    pList: PhaseList[Result]
) extends Command {
  def apply(args: List[String]): Result = {
    val aseConfig = ASEConfig(this)
    val parser = new ArgParser(this, aseConfig)
    val runner = pList.getRunner(parser)
    parser(args)
    ASE(this, runner(_), aseConfig)
  }

  def display(res: Result): Unit = ()

  override def toString: String = pList.toString

  def >>[C <: Config, R](phase: PhaseObj[Result, C, R]): PhaseList[R] = pList >> phase
}

// base command
case object CmdBase extends CommandObj("", PhaseNil)

// parse-algo
case object CmdParseAlgo extends CommandObj("parse-algo", CmdBase >> ParseAlgo) {
  override def display(algo: Algorithm): Unit = println(algo)
}

// compile-algo
case object CmdCompileAlgo extends CommandObj("compile-algo", CmdParseAlgo >> CompileAlgo) {
  override def display(func: core.Func): Unit = println(core.beautify(func))
}

// infer-algo
case object CmdInferAlgo extends CommandObj("infer-algo", CmdParseAlgo >> InferAlgo) {
}

// parse
case object CmdParse extends CommandObj("parse", CmdBase >> Parse) {
  override def display(script: Script): Unit = println(script)
}

// filter
case object CmdFilterMeta extends CommandObj("filter-meta", CmdBase >> FilterMeta)

// load
case object CmdLoad extends CommandObj("load", CmdParse >> Load)

// eval
case object CmdEval extends CommandObj("eval", CmdLoad >> EvalCore) {
  override def display(st: core.State): Unit = println(core.beautify(st))
}

// repl
case object CmdREPL extends CommandObj("repl", CmdLoad >> REPLCore)

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

// repl-algo
case object CmdREPLAlgo extends CommandObj("repl-algo", CmdBase >> REPLAlgo)

// gen-model
case object CmdGenModel extends CommandObj("gen-model", CmdBase >> GenModel)

// help
case object CmdHelp extends CommandObj("help", CmdBase >> Help)
