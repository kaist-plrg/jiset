package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.generator._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import spray.json._

// GenModel phase
case object GenModel extends PhaseObj[ECMAScript, GenModelConfig, Unit] {
  val name: String = "gen-model"
  val help: String = "generate ECMAScript models."

  def apply(
    spec: ECMAScript,
    jisetConfig: JISETConfig,
    config: GenModelConfig
  ): Unit = {
    val GenModelConfig(dirname, noParser) = config
    time(s"generating models to $dirname", {
      ModelGenerator(spec, dirname, !noParser)
    })
  }

  def defaultConfig: GenModelConfig = GenModelConfig()
  val options: List[PhaseOption[GenModelConfig]] = List(
    ("dir", StrOption((c, s) => c.dirname = s),
      "set the output directory (default: model)."),
    ("no-parser", BoolOption(c => c.noParser = true),
      "do not generate parser."),
  )
}

// GenModel phase config
case class GenModelConfig(
  var dirname: String = "model",
  var noParser: Boolean = false
) extends Config
