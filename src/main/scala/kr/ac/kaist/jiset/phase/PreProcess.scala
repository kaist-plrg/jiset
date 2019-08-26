package kr.ac.kaist.jiset.phase

import java.io.File
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.generator._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._

// PreProcess phase
case object PreProcess extends PhaseObj[Unit, PreProcessConfig, Unit] {

  val name: String = "preprocess"
  val help: String = "copy AlgoCompiler file to model."

  def apply(
    non: Unit,
    jisetConfig: JISETConfig,
    config: PreProcessConfig
  ): Unit = {
    deleteFile(s"$MODEL_DIR/package.scala")
    PreGenerator()
  }

  def defaultConfig: PreProcessConfig = PreProcessConfig()
  val options: List[PhaseOption[PreProcessConfig]] = List()
}

// PreProcess phase config
case class PreProcessConfig() extends Config
