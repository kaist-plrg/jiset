package kr.ac.kaist.jiset.phase

import java.io.File
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.generator._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import spray.json._

// GenModel phase
case object GenModel extends PhaseObj[Unit, GenModelConfig, Unit] {

  val name: String = "gen-model"
  val help: String = "generates ECMAScript models."

  def apply(
    non: Unit,
    jisetConfig: JISETConfig,
    config: GenModelConfig
  ): Unit = {
    val spec = Spec(s"$RESOURCE_DIR/$VERSION/auto/spec.json")
    deleteFile(s"$MODEL_DIR/package.scala")
    ModelGenerator(spec)
  }

  def defaultConfig: GenModelConfig = GenModelConfig()
  val options: List[PhaseOption[GenModelConfig]] = List()
}

// GenModel phase config
case class GenModelConfig() extends Config
