package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.{ LINE_SEP, JISETConfig }
import kr.ac.kaist.jiset.model.{ Parser => JSParser, _ }
import kr.ac.kaist.jiset.core._
import kr.ac.kaist.jiset.util.Useful._
import scala.io.Source

// Load phase
case object Load extends PhaseObj[Script, LoadConfig, State] {
  val name = "load"
  val help = "read script object and using global, convert to State object."

  def apply(
    script: Script,
    jisetConfig: JISETConfig,
    config: LoadConfig
  ): State = Model.initState.copy(
    context = Model.initState.context.copy(insts = List(Parser.parseInst("""{
      app __x0__ = (RunJobs)
      return __x0__
    }"""))),
    globals = Model.initState.globals + (Id("script") -> ASTVal(script))
  )

  def defaultConfig: LoadConfig = LoadConfig()
  val options: List[PhaseOption[LoadConfig]] = List()
}

// Parse phase config
case class LoadConfig() extends Config
