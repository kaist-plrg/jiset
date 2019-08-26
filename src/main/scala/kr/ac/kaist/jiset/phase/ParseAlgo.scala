package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.algorithm._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.{ LINE_SEP, JISETConfig }
import kr.ac.kaist.jiset.util._
import scala.io.Source

// ParseAlgo phase
case object ParseAlgo extends PhaseObj[Unit, ParseAlgoConfig, Algorithm] {
  val name = "parse-algo"
  val help = "Parses algorithm files."

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: ParseAlgoConfig
  ): Algorithm = Algorithm(getFirstFilename(jisetConfig, "parse-algo"))

  def defaultConfig: ParseAlgoConfig = ParseAlgoConfig()
  val options: List[PhaseOption[ParseAlgoConfig]] = List()
}

// ParseAlgo phase config
case class ParseAlgoConfig() extends Config
