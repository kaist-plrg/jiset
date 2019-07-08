package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.parser.{ MetaParser, MetaData }
import kr.ac.kaist.ase.util.Useful._
import scala.io.Source

// Parse phase
case object ParseMeta extends PhaseObj[Unit, ParseMetaConfig, MetaData] {
  val name = "parse-meta"
  val help = "Parses metadata"

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: ParseMetaConfig
  ): MetaData = {
    val filename = getFirstFilename(aseConfig, "parse-meta")
    MetaParser(filename)
  }

  def defaultConfig: ParseMetaConfig = ParseMetaConfig()
  val options: List[PhaseOption[ParseMetaConfig]] = List()
}

// ParseMeta phase config
case class ParseMetaConfig() extends Config
