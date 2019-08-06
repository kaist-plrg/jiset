package kr.ac.kaist.ase.phase

import java.io.File
import kr.ac.kaist.ase._
import kr.ac.kaist.ase.generator._
import kr.ac.kaist.ase.spec._
import kr.ac.kaist.ase.util.Useful._

// PreProcess phase
case object PreProcess extends PhaseObj[Unit, PreProcessConfig, Unit] {

  val name: String = "preprocess"
  val help: String = "copy AlgoCompiler file to model."

  def apply(
    non: Unit,
    aseConfig: ASEConfig,
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
