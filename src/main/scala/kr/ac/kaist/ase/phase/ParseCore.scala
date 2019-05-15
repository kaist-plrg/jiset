package kr.ac.kaist.ase.phase
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.util._
import kr.ac.kaist.ase.error.NoFileError

// ParseCore phase
case object ParseCore extends PhaseObj[Unit, ParseCoreConfig, Program] {
  val name = "parse-core"
  val help = "Parse Core files."

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: ParseCoreConfig
  ): Program = aseConfig.fileNames match {
    case Nil => throw NoFileError("core-parser")
    case filename :: _ => Parser.fileToProgram(filename)
  }

  def defaultConfig: ParseCoreConfig = ParseCoreConfig()
  val options: List[PhaseOption[ParseCoreConfig]] = List()
}

// ParseCore phase config
case class ParseCoreConfig() extends Config
