package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.parser.{ MetaParser, MetaData }
import kr.ac.kaist.ase.util.Useful._
import scala.io.Source

// Parse phase
case object ExtractMeta extends PhaseObj[Unit, ExtractMetaConfig, MetaData] {
  val name = "extract-meta"
  val help = "Extracts metadata"

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: ExtractMetaConfig
  ): MetaData = {
    val filename = getFirstFilename(aseConfig, "extract-meta")
    MetaParser(filename)
  }

  def defaultConfig: ExtractMetaConfig = ExtractMetaConfig()
  val options: List[PhaseOption[ExtractMetaConfig]] = List()
}

// ExtractMeta phase config
case class ExtractMetaConfig() extends Config
