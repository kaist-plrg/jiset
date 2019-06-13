package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.algorithm._
import kr.ac.kaist.ase.core
import kr.ac.kaist.ase.model.AlgoCompiler
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import scala.io.Source

// CompileAlgo phase
case object CompileAlgo extends PhaseObj[Algorithm, CompileAlgoConfig, core.Func] {
  val name = "compile-algo"
  val help = "Compiles algorithm files."

  def apply(
    algo: Algorithm,
    aseConfig: ASEConfig,
    config: CompileAlgoConfig
  ): core.Func = AlgoCompiler("<empty>", algo)

  def defaultConfig: CompileAlgoConfig = CompileAlgoConfig()
  val options: List[PhaseOption[CompileAlgoConfig]] = List()
}

// CompileAlgo phase config
case class CompileAlgoConfig() extends Config
