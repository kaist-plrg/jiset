package kr.ac.kaist.ase.phase
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.util.Useful._

// ParseCore phase
case object ParseCore extends PhaseObj[Unit, ParseCoreConfig, Program] {
  val name = "parse-core"
  val help = "Parse Core files."

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: ParseCoreConfig
  ): Program = {
    val filename = getFirstFilename(aseConfig, "parse-core")
    Parser.fileToProgram(filename)
  }

  def defaultConfig: ParseCoreConfig = ParseCoreConfig()
  val options: List[PhaseOption[ParseCoreConfig]] = List()
}

// ParseCore phase config
case class ParseCoreConfig() extends Config
