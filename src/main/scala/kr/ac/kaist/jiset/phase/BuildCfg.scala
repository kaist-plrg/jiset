package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.util.Useful._

// BuildCfg phase
case object BuildCFG extends PhaseObj[ECMAScript, BuildCFGConfig, CFG] {
  val name = "build-cfg"
  val help = "build control flow graph (CFG)"

  def apply(
    spec: ECMAScript,
    jisetConfig: JISETConfig,
    config: BuildCFGConfig
  ): CFG = time("build CFG", CFG(spec))

  def defaultConfig: BuildCFGConfig = BuildCFGConfig()
  val options: List[PhaseOption[BuildCFGConfig]] = List()
}

// BuildCFG config
case class BuildCFGConfig() extends Config
