package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.generator._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._

// GenTsModel phase
case object GenTsModel extends PhaseObj[ECMAScript, GenTsModelConfig, Unit] {
  val name: String = "gen-tsmodel"
  val help: String = "generates ECMAScript models."

  def apply(
    spec: ECMAScript,
    jisetConfig: JISETConfig,
    config: GenTsModelConfig
  ): Unit = time(s"generating models in TypeScript", {
    TsModelGenerator(spec)
  })

  def defaultConfig: GenTsModelConfig = GenTsModelConfig()
  val options: List[PhaseOption[GenTsModelConfig]] = List()
}

// GenTsModel phase config
case class GenTsModelConfig() extends Config
