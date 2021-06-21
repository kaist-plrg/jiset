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
  ): Unit = time(s"generating models", {
    ModelGenerator(spec, !config.noParser)
  })

  def defaultConfig: GenModelConfig = GenModelConfig()
  val options: List[PhaseOption[GenModelConfig]] = List(
    ("no-parser", BoolOption(c => c.noParser = true),
      "do not generate parser."),
  )
}

// GenModel phase config
case class GenModelConfig(
  var noParser: Boolean = false
) extends Config
