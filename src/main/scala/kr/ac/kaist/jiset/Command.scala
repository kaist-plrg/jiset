package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.phase._
import kr.ac.kaist.jiset.spec._
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
case object CmdParse extends CommandObj("parse", CmdBase >> Parse) {
  override def display(spec: ECMAScript): Unit = {
    val ECMAScript(grammar, algos, consts, intrinsics, symbols, aoids, section) = spec
    println(s"* grammar:")
    println(s"  - lexical production: ${grammar.lexProds.length}")
    println(s"  - non-lexical production: ${grammar.prods.length}")
    println(s"* algorithms:")
    println(s"  - incomplete: ${spec.incompletedAlgos.length}")
    println(s"  - complete: ${spec.completedAlgos.length}")
    println(s"  - total: ${algos.length}")
    println(s"* consts: ${consts.size}")
    println(s"* intrinsics: ${intrinsics.size}")
    println(s"* symbols: ${symbols.size}")
    println(s"* aoids: ${aoids.size}")
    println(s"* incompleted steps: ${spec.incompletedAlgos.map(_.todos.length).sum}")
  }
}

// gen-model
case object CmdGenModel extends CommandObj("gen-model", CmdParse >> GenModel)

// compile-repl
case object CmdCompileREPL extends CommandObj("compile-repl", CmdBase >> CompileREPL)

// gen-test
case object CmdGenTest extends CommandObj("gen-test", CmdBase >> GenTest)
