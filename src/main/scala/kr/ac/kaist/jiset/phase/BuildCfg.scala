package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.spec.ECMAScript

// BuildCfg phase
case object BuildCFG extends PhaseObj[ECMAScript, BuildCFGConfig, CFG] {
  val name = "build-cfg"
  val help = "build Context Flow Graph"

  def apply(
    spec: ECMAScript,
    jisetConfig: JISETConfig,
    config: BuildCFGConfig
  ): CFG = {
    println(s"--------------------------------------------------")
    val cfg = CFG(spec)
    cfg
  }

  def defaultConfig: BuildCFGConfig = BuildCFGConfig()
  val options: List[PhaseOption[BuildCFGConfig]] = List()
}

// BuildCFG config
case class BuildCFGConfig() extends Config
