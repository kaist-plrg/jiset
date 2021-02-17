package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.error.NoMode
import kr.ac.kaist.jiset.ir
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

// help
case object CmdHelp extends CommandObj("help", CmdBase >> Help)

// parse
case object CmdParse extends CommandObj("parse", CmdBase >> Parse)

// compile-repl
case object CmdCompileREPL extends CommandObj("compile-repl", CmdBase >> CompileREPL)

// check
case object CmdCheck extends CommandObj("check", CmdParse >> Check)

// analyze
case object CmdAnalyze extends CommandObj("analyze", CmdParse >> Analyze)

// gen-test
case object CmdGenTest extends CommandObj("gen-test", CmdBase >> GenTest)

// extract tag
case object CmdExtractTag extends CommandObj("extract-tag", CmdBase >> ExtractTag)
