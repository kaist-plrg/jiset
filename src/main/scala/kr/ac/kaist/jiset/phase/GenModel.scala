package kr.ac.kaist.jiset.phase

import java.io.File
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.generator._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util._
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
    ModelGenerator(
      config.packageName.getOrElse("kr.ac.kaist.jiset"),
      config.out.getOrElse(MODEL_DIR),
      spec
    )
  }

  def defaultConfig: GenModelConfig = GenModelConfig()
  val options: List[PhaseOption[GenModelConfig]] = List(
    ("packageName", StrOption((c, s) => c.packageName = Some(s)),
      "set the base package name for models (default: kr.ac.kaist.jiset)."),
    ("out", StrOption((c, s) => c.out = Some(s)),
      "set the output directory (default: $JISET_HOME/src/main/scala/kr/ac/kaist/jiset/model).")
  )
}

// GenModel phase config
case class GenModelConfig(
  var packageName: Option[String] = None,
  var out: Option[String] = None
) extends Config
