package kr.ac.kaist.jiset.phase
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.{ LINE_SEP, JISETConfig }
import kr.ac.kaist.jiset.util.JvmUseful._

// IRParse phase
case object IRParse extends Phase[Unit, IRParseConfig, Program] {
  val name = "parse-ir"
  val help = "parses an IR file."

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: IRParseConfig
  ): Program = {
    val filename = getFirstFilename(jisetConfig, "parse-ir")
    Program.fromFile(filename)
  }

  def defaultConfig: IRParseConfig = IRParseConfig()
  val options: List[PhaseOption[IRParseConfig]] = List()
}

// IRParse phase config
case class IRParseConfig() extends Config
