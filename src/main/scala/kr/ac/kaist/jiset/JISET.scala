package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.error._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.phase._

object JISET {
  ////////////////////////////////////////////////////////////////////////////////
  // Main entry point
  ////////////////////////////////////////////////////////////////////////////////
  def main(tokens: Array[String]): Unit = try tokens.toList match {
    case str :: args => cmdMap.get(str) match {
      case Some(CmdHelp) => println(JISET.help)
      case Some(cmd) => cmd(args)
      case None => throw NoCmdError(str)
    }
    case Nil => throw NoInputError
  } catch {
    // JISETError: print the error message.
    case ex: JISETError =>
      Console.err.println(ex.getMessage)
    // Unexpected: print the stack trace.
    case ex: Throwable =>
      Console.err.println("* Unexpected error occurred.")
      Console.err.println(ex.toString)
      Console.err.println(ex.getStackTrace.mkString(LINE_SEP))
  }

  def apply[Result](
    command: Command[Result],
    runner: JISETConfig => Result,
    config: JISETConfig
  ): Result = {
    // set the start time.
    val startTime = System.currentTimeMillis

    // execute the command.
    val result: Result = runner(config)

    // duration
    val duration = System.currentTimeMillis - startTime

    // display the result.
    if (!config.silent) {
      command.display(result)
    }

    // display the time.
    if (config.time) {
      val name = config.command.name
      println(s"The command '$name' took $duration ms.")
    }

    // return result
    result
  }

  // commands
  val commands: List[Command[_]] = List(
    CmdHelp,

    // JISET
    CmdExtract,
    CmdGenModel,
    CmdGenTsModel,
    CmdCompileREPL,
    CmdGenTest,

    // JS
    CmdParse,
    CmdLoad,
    CmdEval,

    // test262
    CmdFilterMeta,

    // IRES
    CmdIRParse,
    CmdIRLoad,
    CmdIREval,
    CmdIRREPL,
    CmdBuildCFG,
  )
  val cmdMap = commands.foldLeft[Map[String, Command[_]]](Map()) {
    case (map, cmd) => map + (cmd.name -> cmd)
  }

  // phases
  var phases: List[Phase] = List(
    Help,

    // JISET
    Extract,
    GenModel,
    GenTsModel,
    CompileREPL,
    GenTest,

    // JS
    Parse,
    Load,

    // IRES
    IRParse,
    IRLoad,
    IREval,
    IRREPL,
    BuildCFG,
  )

  // global options
  val options: List[PhaseOption[JISETConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "does not show final results."),
    ("debug", BoolOption(c => DEBUG = true),
      "turns on the debug mode."),
    ("interactive", BoolOption(c => INTERACTIVE = true),
      "turns on the interactive mode."),
    ("no-bugfix", BoolOption(c => BUGFIX = false),
      "uses semantics including specification bugs."),
    ("log", BoolOption(c => LOG = true),
      "turns on the logging mode."),
    ("time", BoolOption(c => c.time = true),
      "displays the duration time.")
  )

  // indentation
  private val INDENT = 20
  private def wrap(str: String): String = s"%-${INDENT}s".format(str)

  // print help message.
  val help: String = {
    val app = new Appender
    app >> "* command list:" >> LINE_SEP
    app >> "    Each command consists of following phases." >> LINE_SEP
    app >> "    format: {command} {phase} [>> {phase}]*" >> LINE_SEP
    app >> LINE_SEP
    for (cmd <- commands) {
      app >> "    " >> wrap(cmd.name) >> cmd.help >> LINE_SEP
      app >> "    " >> " " * INDENT >> "(" >> cmd.pList.toString >> ")" >> LINE_SEP
    }
    app >> LINE_SEP
    app >> "* phase list:" >> LINE_SEP
    app >> "    Each phase has following options." >> LINE_SEP
    app >> "    format: {phase} [-{phase}:{option}[={input}]]*" >> LINE_SEP
    app >> LINE_SEP
    for (phase <- phases) {
      app >> "    " >> wrap(phase.name) >> phase.help >> LINE_SEP
      app >> LINE_SEP
      for ((name, desc) <- phase.getOptDescs) {
        app >> "    " >> " " * INDENT >> "If " >> name >> " is given, " >> desc >> LINE_SEP
      }
      app >> LINE_SEP
    }
    app >> "* global option:" >> LINE_SEP
    app >> LINE_SEP
    for ((opt, kind, desc) <- options) {
      app >> "    If -" >> opt >> kind.postfix >> " is given, " >> desc >> LINE_SEP
    }
    app.toString
  }
}

case class JISETConfig(
  var command: Command[_],
  var args: List[String] = Nil,
  var silent: Boolean = false,
  var debug: Boolean = false,
  var time: Boolean = false
) extends Config
