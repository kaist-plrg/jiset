package kr.ac.kaist.jiset.phase
import kr.ac.kaist.jiset.core._
import kr.ac.kaist.jiset.{ LINE_SEP, JISETConfig }
import kr.ac.kaist.jiset.util.Useful._

// ParseCore phase
case object ParseCore extends PhaseObj[Unit, ParseCoreConfig, Program] {
  val name = "parse-core"
  val help = "Parse Core files."

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: ParseCoreConfig
  ): Program = {
    val filename = getFirstFilename(jisetConfig, "parse-core")
    Parser.fileToProgram(filename)
  }

  def defaultConfig: ParseCoreConfig = ParseCoreConfig()
  val options: List[PhaseOption[ParseCoreConfig]] = List()
}

// ParseCore phase config
case class ParseCoreConfig() extends Config
