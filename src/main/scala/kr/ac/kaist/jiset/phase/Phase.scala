package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.util.ArgParser

abstract class Phase {
  val name: String
  val help: String
  def getOptShapes: List[String]
  def getOptDescs: List[(String, String)]
}
abstract class PhaseObj[Input, PhaseConfig <: Config, Output] extends Phase {
  val name: String
  val help: String
  def apply(
    in: Input,
    jisetConfig: JISETConfig,
    config: PhaseConfig = defaultConfig
  ): Output
  def defaultConfig: PhaseConfig
  val options: List[PhaseOption[PhaseConfig]]

  def getRunner(
    parser: ArgParser
  ): (Input, JISETConfig) => Output = {
    val config = defaultConfig
    parser.addRule(config, name, options)
    (in, jisetConfig) => apply(in, jisetConfig, config)
  }

  def getOptShapes: List[String] = options.map {
    case (opt, kind, _) => s"-$name:${opt}${kind.postfix}"
  }
  def getOptDescs: List[(String, String)] = options.map {
    case (opt, kind, desc) => (s"-$name:${opt}${kind.postfix}", desc)
  }
}

trait Config
