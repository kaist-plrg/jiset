package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.model._
import kr.ac.kaist.ase.util.Useful._
import scala.io.Source

// Parse phase
case object Parse extends PhaseObj[Unit, ParseConfig, Script] {
  val name = "parse"
  val help = "Parses AST files."

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: ParseConfig
  ): Script = {
    val filename = getFirstFilename(aseConfig, "parse")
    Parser(filename)
  }

  def defaultConfig: ParseConfig = ParseConfig()
  val options: List[PhaseOption[ParseConfig]] = List()
}

// Parse phase config
case class ParseConfig() extends Config
