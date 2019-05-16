package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.model._
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.util.Useful._
import scala.io.Source

// ConvertToCore phase
case object ConvertToCore extends PhaseObj[Script, ConvertToCoreConfig, State] {
  val name = "convertToCore"
  val help = "read script object and using global, convert to State object."

  def apply(
    script: Script,
    aseConfig: ASEConfig,
    config: ConvertToCoreConfig
  ): State = {
    val (initialLocals, initialHeap) = Heap().allocLocals()
    val initialEnv: Env = Env(locals = initialLocals)
    State(
      insts = List(IApp(LhsLet(Id("result")), ERun(script, "Evalutation"), Nil)),
      globals = Global.initGlobal,
      env = initialEnv,
      heap = initialHeap
    )
  }

  def defaultConfig: ConvertToCoreConfig = ConvertToCoreConfig()
  val options: List[PhaseOption[ConvertToCoreConfig]] = List()
}

// Parse phase config
case class ConvertToCoreConfig() extends Config
