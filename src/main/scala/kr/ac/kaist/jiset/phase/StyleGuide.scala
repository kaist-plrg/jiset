package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._

// StyleGuide phase
case object StyleGuide extends PhaseObj[ECMAScript, StyleGuideConfig, Unit] {
  val name: String = "style-guide"
  val help: String = "guide notation style of ECMAScript"

  def apply(
    spec: ECMAScript,
    jisetConfig: JISETConfig,
    config: StyleGuideConfig
  ): Unit = {
    println("TODO")
  }

  def defaultConfig: StyleGuideConfig = StyleGuideConfig()
  val options: List[PhaseOption[StyleGuideConfig]] = Nil
}

// StyleGuide phase config
case class StyleGuideConfig() extends Config
