package kr.ac.kaist.ase.phase

import java.io.File
import kr.ac.kaist.ase._
import kr.ac.kaist.ase.generator._
import kr.ac.kaist.ase.spec._
import kr.ac.kaist.ase.util.Useful._
import spray.json._

// GenModel phase
case object GenModel extends PhaseObj[Unit, GenModelConfig, Unit] {

  val name: String = "gen-model"
  val help: String = "generates ECMAScript models."

  def apply(
    non: Unit,
    aseConfig: ASEConfig,
    config: GenModelConfig
  ): Unit = {
    val version = getFirstFilename(aseConfig, "gen-model")
    val spec = Spec(s"$RESOURCE_DIR/$version/spec.json")
    deleteFile(s"$MODEL_DIR/package.scala")
    ModelGenerator(version, spec)
  }

  def defaultConfig: GenModelConfig = GenModelConfig()
  val options: List[PhaseOption[GenModelConfig]] = List()
}

// GenModel phase config
case class GenModelConfig() extends Config
