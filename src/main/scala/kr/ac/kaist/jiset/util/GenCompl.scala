package kr.ac.kaist.jiset.util

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.util.JvmUseful._

// DO NOT DELETE - PLEASE RUN THIS OBJECT WHEN COMMANDS ARE MODIFIED.
object GenCompl {
  // generate completion file jiset-auto-completion
  def run: Unit = dumpFile(content, path)

  // commands
  val commands = JISET.commands

  // phases
  val phases = JISET.phases

  // global options
  val options = JISET.options

  // get content
  def content: String = {
    val app = new Appender
    app >> "# `complete` for zsh" >> LINE_SEP
    app >> "if type complete &>/dev/null; then" >> LINE_SEP
    app >> "  :" >> LINE_SEP
    app >> "else" >> LINE_SEP
    app >> "  autoload bashcompinit" >> LINE_SEP
    app >> "  bashcompinit" >> LINE_SEP
    app >> "fi" >> LINE_SEP
    app >> LINE_SEP
    app >> "# completion for jiset" >> LINE_SEP
    app >> "_jiset_completions() {" >> LINE_SEP
    app >> "  local cur prev opts lastc informats outformats datafiles" >> LINE_SEP
    app >> "  cur=\"${COMP_WORDS[COMP_CWORD]}\"" >> LINE_SEP
    app >> "  prev=\"${COMP_WORDS[COMP_CWORD-1]}\"" >> LINE_SEP
    app >> LINE_SEP

    // commands
    app >> "  cmdList=\""
    for (cmd <- commands) app >> cmd.name >> " "
    app >> "\"" >> LINE_SEP

    // global options
    app >> "  globalOpt=\""
    for (opt <- options) app >> "-" + opt._1 >> " "
    app >> "\"" >> LINE_SEP

    // phase options
    for (phase <- phases) {
      app >> "  " >> norm(phase.name) >> "Opt=\""
      for (opt <- phase.options) app >> "-" >> phase.name >> ":" >> opt._1 >> " "
      app >> "\"" >> LINE_SEP
    }

    // completion for commands
    app >> LINE_SEP
    app >> "  # completion for commands" >> LINE_SEP
    app >> "  case \"${COMP_CWORD}\" in" >> LINE_SEP
    app >> "    1)" >> LINE_SEP
    app >> "      COMPREPLY=($(compgen -W \"${cmdList}\" \"${cur}\"))" >> LINE_SEP
    app >> "      return 0" >> LINE_SEP
    app >> "      ;;" >> LINE_SEP
    app >> "  esac" >> LINE_SEP
    app >> "  cmd=\"${COMP_WORDS[1]}\"" >> LINE_SEP

    // completion for options
    app >> LINE_SEP
    app >> "  # completion for options" >> LINE_SEP
    app >> "  case \"${cur}\" in" >> LINE_SEP
    app >> "    -*)" >> LINE_SEP
    app >> "      case \"${cmd}\" in" >> LINE_SEP
    for (cmd <- commands) {
      app >> "        " >> cmd.name >> ")" >> LINE_SEP
      app >> "          COMPREPLY=($(compgen -W \"${globalOpt}"
      for (pname <- cmd.pList.nameList) {
        app >> " ${" >> norm(pname) >> "Opt}"
      }
      app >> "\"))" >> LINE_SEP
      app >> "          return 0" >> LINE_SEP
      app >> "          ;;" >> LINE_SEP
    }
    app >> "      esac" >> LINE_SEP
    app >> "      return 0" >> LINE_SEP
    app >> "      ;;" >> LINE_SEP
    app >> "  esac" >> LINE_SEP
    app >> LINE_SEP
    app >> "  COMPREPLY=( $(compgen -X '' -f \"${cur}\") )" >> LINE_SEP
    app >> "}" >> LINE_SEP
    app >> LINE_SEP
    app >> "# completion setting" >> LINE_SEP
    app >> "complete -o filenames -o bashdefault -F _jiset_completions jiset" >> LINE_SEP

    app.toString
  }

  // path of completion file
  val path = s"$BASE_DIR/jiset-auto-completion"

  // normalize name
  def norm(name: String): String = name.replaceAll("-", "")
}
