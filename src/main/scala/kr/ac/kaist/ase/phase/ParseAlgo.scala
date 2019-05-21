package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.algorithm._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.util._
import scala.io.Source

// ParseAlgo phase
case object ParseAlgo extends PhaseObj[Unit, ParseAlgoConfig, Algorithm] {
  val name = "parse-algo"
  val help = "Parses algorithm files."

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: ParseAlgoConfig
  ): Algorithm = {
    val filename = getFirstFilename(aseConfig, "parse-algo")
    Algorithm(filename)
  }

  def defaultConfig: ParseAlgoConfig = ParseAlgoConfig()
  val options: List[PhaseOption[ParseAlgoConfig]] = List()
}

// ParseAlgo phase config
case class ParseAlgoConfig() extends Config
