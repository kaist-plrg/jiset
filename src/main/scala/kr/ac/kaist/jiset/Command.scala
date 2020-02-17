package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.error.NoMode
import kr.ac.kaist.ires.ir
import kr.ac.kaist.jiset.algorithm.Algorithm
import kr.ac.kaist.jiset.phase._
import kr.ac.kaist.jiset.util.ArgParser

sealed trait Command {
  val name: String
  def apply(args: List[String]): Any
}

class CommandObj[Result](
    override val name: String,
    pList: PhaseList[Result]
) extends Command {
  def apply(args: List[String]): Result = {
    val jisetConfig = JISETConfig(this)
    val parser = new ArgParser(this, jisetConfig)
    val runner = pList.getRunner(parser)
    parser(args)
    JISET(this, runner(_), jisetConfig)
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
  override def display(func: ir.Func): Unit = println(ir.beautify(func))
}

// infer-algo
case object CmdInferAlgo extends CommandObj("infer-algo", CmdParseAlgo >> InferAlgo) {
}

// repl-algo
case object CmdREPLAlgo extends CommandObj("repl-algo", CmdBase >> REPLAlgo)

// gen-model
case object CmdGenModel extends CommandObj("gen-model", CmdBase >> GenModel)

// grammar-diff
case object CmdGrammarDiff extends CommandObj("grammar-diff", CmdBase >> GrammarDiff)

// algo-step-diff
case object CmdAlgoStepDiff extends CommandObj("algo-step-diff", CmdBase >> AlgoStepDiff)

// help
case object CmdHelp extends CommandObj("help", CmdBase >> Help)
