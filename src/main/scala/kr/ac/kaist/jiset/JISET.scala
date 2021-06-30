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
    command: CommandObj[Result],
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
  val commands: List[Command] = List(
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
  val cmdMap = commands.foldLeft[Map[String, Command]](Map()) {
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
      "final results are not displayed."),
    ("debug", BoolOption(c => DEBUG = true),
      "turn on the debub mode."),
    ("interactive", BoolOption(c => INTERACTIVE = true),
      "turn on the interactive mode."),
    ("no-bugfix", BoolOption(c => BUGFIX = false),
      "turn off the bugfix mode."),
    ("log", BoolOption(c => LOG = true),
      "turn on the logging."),
    ("time", BoolOption(c => c.time = true),
      "display duration time.")
  )

  // indentation
  private val INDENT = 20

  // print help message.
  val help: String = {
    val s: StringBuilder = new StringBuilder
    s.append("* command list:").append(LINE_SEP)
    s.append("    Each command consists of following phases.").append(LINE_SEP)
    s.append("    format: {command} {phase} [>> {phase}]*").append(LINE_SEP).append(LINE_SEP)
    commands foreach (cmd => {
      s.append(s"    %-${INDENT}s".format(cmd.name))
        .append(cmd.toString.replace(LINE_SEP, LINE_SEP + "    " + " " * INDENT))
        .append(LINE_SEP)
    })
    s.append(LINE_SEP)
    s.append("* phase list:").append(LINE_SEP)
    s.append("    Each phase has following options.").append(LINE_SEP)
    s.append("    format: {phase} [-{phase}:{option}[={input}]]*").append(LINE_SEP).append(LINE_SEP)
    phases foreach (phase => {
      s.append(s"    %-${INDENT}s".format(phase.name))
      Useful.indentation(s, phase.help, INDENT + 4)
      s.append(LINE_SEP)
        .append(LINE_SEP)
      phase.getOptDescs foreach {
        case (name, desc) =>
          s.append(s"%${INDENT + 4}s".format("") + s"If $name is given, $desc").append(LINE_SEP)
      }
      s.append(LINE_SEP)
    })
    s.append("* global option:").append(LINE_SEP).append(LINE_SEP)
    options.foreach {
      case (opt, kind, desc) =>
        val name = s"-${opt}${kind.postfix}"
        s.append(s"    If $name is given, $desc").append(LINE_SEP)
    }
    s.toString
  }
}

case class JISETConfig(
  var command: Command,
  var args: List[String] = Nil,
  var silent: Boolean = false,
  var debug: Boolean = false,
  var time: Boolean = false
) extends Config
