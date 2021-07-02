package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.phase._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.ArgParser

sealed trait Command {
  val name: String
  def apply(args: List[String]): Any
  def help: String = ""
}

class CommandObj[Result](
  val name: String,
  val pList: PhaseList[Result]
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

////////////////////////////////////////////////////////////////////////////////
// JISET
////////////////////////////////////////////////////////////////////////////////
// extract
case object CmdExtract extends CommandObj("extract", CmdBase >> Extract) {
  override def help = "extracts ECMAScript model from ecma262/spec.html."
  override def display(spec: ECMAScript): Unit = {
    val ECMAScript(version, grammar, algos, consts, intrinsics, symbols, aoids, section) = spec
    println(s"* version: $version")
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
case object CmdGenModel extends CommandObj("gen-model", CmdExtract >> GenModel) {
  override def help = "generates ECMAScript models."
}

// gen-tsmodel
case object CmdGenTsModel extends CommandObj("gen-tsmodel", CmdExtract >> GenTsModel) {
  override def help = "generates ECMAScript models in TypeScript."
}

// compile-repl
case object CmdCompileREPL extends CommandObj("compile-repl", CmdBase >> CompileREPL) {
  override def help = "performs REPL for printing compile result of particular step."
}

// gen-test
case object CmdGenTest extends CommandObj("gen-test", CmdBase >> GenTest) {
  override def help = "generates tests with the current implementation as the oracle."
}

////////////////////////////////////////////////////////////////////////////////
// JS
////////////////////////////////////////////////////////////////////////////////
// parse
case object CmdParse extends CommandObj("parse", CmdBase >> Parse) {
  override def help = "parses a JavaScript file using the generated parser."
  override def display(script: Script): Unit = println(script)
}

// load
case object CmdLoad extends CommandObj("load", CmdParse >> Load) {
  override def help = "loads a JavaScript AST to the initial IR states."
  override def display(st: ir.State): Unit = println(st.beautified)
}

// eval
case object CmdEval extends CommandObj("eval", CmdLoad >> IREval) {
  override def help = "evaluates a JavaScript file using generated interpreter."
  override def display(st: ir.State): Unit = println(st.beautified)
}

////////////////////////////////////////////////////////////////////////////////
// test262
////////////////////////////////////////////////////////////////////////////////
// filter
case object CmdFilterMeta extends CommandObj("filter-meta", CmdBase >> FilterMeta) {
  override def help = "extracts and filters out metadata of test262 tests."
}

////////////////////////////////////////////////////////////////////////////////
// IRES
////////////////////////////////////////////////////////////////////////////////
// parse-ir
case object CmdIRParse extends CommandObj("parse-ir", CmdBase >> IRParse) {
  override def help = "parses an IR file."
  override def display(pgm: ir.Program): Unit = println(pgm.beautified)
}

// load-ir
case object CmdIRLoad extends CommandObj("load-ir", CmdIRParse >> IRLoad) {
  override def help = "loads an IR AST to the initial IR states."
  override def display(st: ir.State): Unit = println(st.beautified)
}

// eval-ir
case object CmdIREval extends CommandObj("eval-ir", CmdIRLoad >> IREval) {
  override def help = "evaluates an IR file."
  override def display(st: ir.State): Unit = println(st.beautified)
}

// repl-ir
case object CmdIRREPL extends CommandObj("repl-ir", CmdIRLoad >> IRREPL) {
  override def help = "performs REPL for IR instructions."
}

// build-cfg
case object CmdBuildCFG extends CommandObj("build-cfg", CmdExtract >> BuildCFG) {
  override def help = "builds control flow graph (CFG)."
}
