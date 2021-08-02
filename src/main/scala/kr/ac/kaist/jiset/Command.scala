package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.phase._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.ArgParser
import scala.Console.CYAN

sealed abstract class Command[Result](
  val name: String,
  val pList: PhaseList[Result]
) {
  def help: String
  def display(res: Result): Unit = ()

  def apply(args: List[String]): Result = {
    val jisetConfig = JISETConfig(this)
    val parser = new ArgParser(this, jisetConfig)
    val runner = pList.getRunner(parser)
    parser(args)
    JISET(this, runner(_), jisetConfig)
  }

  override def toString: String = pList.toString

  def >>[C <: Config, R](phase: Phase[Result, C, R]): PhaseList[R] = pList >> phase
}

// base command
case object CmdBase extends Command("", PhaseNil) {
  def help = "does nothing."
}

// help
case object CmdHelp extends Command("help", CmdBase >> Help) {
  def help = "shows help messages."
}

////////////////////////////////////////////////////////////////////////////////
// JISET (JavaScript IR-based Semantics Extraction Toolchain)
////////////////////////////////////////////////////////////////////////////////
// extract
case object CmdExtract extends Command("extract", CmdBase >> Extract) {
  def help = "extracts ECMAScript model from ecma262/spec.html."
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
case object CmdGenModel extends Command("gen-model", CmdExtract >> GenModel) {
  def help = "generates ECMAScript models."
}

// compile-repl
case object CmdCompileREPL extends Command("compile-repl", CmdBase >> CompileREPL) {
  def help = "performs REPL for printing compile result of particular step."
}

// gen-test
case object CmdGenTest extends Command("gen-test", CmdBase >> GenTest) {
  def help = "generates tests with the current implementation as the oracle."
}

////////////////////////////////////////////////////////////////////////////////
// JS
////////////////////////////////////////////////////////////////////////////////
// parse
case object CmdParse extends Command("parse", CmdBase >> Parse) {
  def help = "parses a JavaScript file using the generated parser."
  override def display(script: js.ast.Script): Unit = println(script)
}

// load
case object CmdLoad extends Command("load", CmdParse >> Load) {
  def help = "loads a JavaScript AST to the initial IR states."
  override def display(st: ir.State): Unit = println(st.beautified)
}

// eval
case object CmdEval extends Command("eval", CmdLoad >> IREval) {
  def help = "evaluates a JavaScript file using generated interpreter."
  override def display(st: ir.State): Unit = println(st.beautified)
}

// repl
case object CmdREPL extends Command("repl", CmdLoad >> IRREPL) {
  def help = "performs REPL for a JavaScript file"
}

////////////////////////////////////////////////////////////////////////////////
// JSAVER (JavaScript Static Analyzer via ECMAScript Representations)
////////////////////////////////////////////////////////////////////////////////
// analyze
case object CmdAnalyze extends Command("analyze", CmdParse >> Analyze) {
  def help = "performs static analysis for a given JavaScript program."
  override def display(st: analyzer.AbsSemantics): Unit = ???
}

////////////////////////////////////////////////////////////////////////////////
// test262
////////////////////////////////////////////////////////////////////////////////
// filter
case object CmdFilterMeta extends Command("filter-meta", CmdBase >> FilterMeta) {
  def help = "extracts and filters out metadata of test262 tests."
}

////////////////////////////////////////////////////////////////////////////////
// IRES (Intermediate Representations for ECMAScript)
////////////////////////////////////////////////////////////////////////////////
// parse-ir
case object CmdIRParse extends Command("parse-ir", CmdBase >> IRParse) {
  def help = "parses an IR file."
  override def display(pgm: ir.Program): Unit = println(pgm.beautified)
}

// load-ir
case object CmdIRLoad extends Command("load-ir", CmdIRParse >> IRLoad) {
  def help = "loads an IR AST to the initial IR states."
  override def display(st: ir.State): Unit = println(st.beautified)
}

// eval-ir
case object CmdIREval extends Command("eval-ir", CmdIRLoad >> IREval) {
  def help = "evaluates an IR file."
  override def display(st: ir.State): Unit = println(st.beautified)
}

// repl-ir
case object CmdIRREPL extends Command("repl-ir", CmdIRLoad >> IRREPL) {
  def help = "performs REPL for IR instructions."
}

////////////////////////////////////////////////////////////////////////////////
// JSTAR (JavaScript Specification Type Analyzer using Refinement)
////////////////////////////////////////////////////////////////////////////////
// build-cfg
case object CmdBuildCFG extends Command("build-cfg", CmdExtract >> BuildCFG) {
  def help = "builds control flow graph (CFG)."
}

// type-check
case object CmdTypeCheck extends Command("type-check", CmdBuildCFG >> TypeCheck) {
  def help = "performs type checking for specifications."
  override def display(sem: checker.AbsSemantics): Unit = {
    println(sem.getString(CYAN))
    println(sem.getInfo)
  }
}
