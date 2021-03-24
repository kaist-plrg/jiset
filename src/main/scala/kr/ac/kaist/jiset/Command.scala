package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.error.NoMode
import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.phase._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.ArgParser
import org.jsoup.nodes.Element
import scala.Console._

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
    val ECMAScript(grammar, algos, intrinsics, symbols, aoids, section) = spec
    println(s"* grammar:")
    println(s"  - lexical production: ${grammar.lexProds.length}")
    println(s"  - non-lexical production: ${grammar.prods.length}")
    println(s"* algorithms:")
    println(s"  - incomplete: ${spec.incompletedAlgos.length}")
    println(s"  - complete: ${spec.completedAlgos.length}")
    println(s"  - total: ${algos.length}")
    println(s"* intrinsics: ${intrinsics.size}")
    println(s"* symbols: ${symbols.size}")
    println(s"* aoids: ${aoids.size}")
    println(s"* incompleted steps: ${spec.incompletedAlgos.map(_.todos.length).sum}")
  }
}

// style-guide
case object CmdStyleGuide extends CommandObj("style-guide", CmdParse >> StyleGuide)

// compile-repl
case object CmdCompileREPL extends CommandObj("compile-repl", CmdBase >> CompileREPL)

// check
case object CmdCheck extends CommandObj("check", CmdParse >> Check)

// build-cfg
case object CmdBuildCFG extends CommandObj("build-cfg", CmdParse >> BuildCFG)

// analyze
case object CmdAnalyze extends CommandObj("analyze", CmdBuildCFG >> Analyze) {
  override def display(unit: Unit): Unit = {
    println(AbsSemantics.getString(CYAN))
    println(AbsSemantics.getInfo)
  }
}

// gen-test
case object CmdGenTest extends CommandObj("gen-test", CmdBase >> GenTest)

// extract tag
case object CmdExtractTag extends CommandObj("extract-tag", CmdBase >> ExtractTag) {
  override def display(elems: List[Element]): Unit = elems.foreach(println _)
}
