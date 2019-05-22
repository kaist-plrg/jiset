package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.model._
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.util.Useful._
import scala.io.Source

// Load phase
case object Load extends PhaseObj[Script, LoadConfig, State] {
  val name = "load"
  val help = "read script object and using global, convert to State object."

  def apply(
    script: Script,
    aseConfig: ASEConfig,
    config: LoadConfig
  ): State = State(
    retValue = None,
    insts = List(ILet(Id("_"), ERun(RefId(Id("script")), "Evalutation", Nil))),
    globals = Global.initGlobal,
    locals = Map(Id("script") -> ASTVal(script)),
    heap = Heap()
  )

  def defaultConfig: LoadConfig = LoadConfig()
  val options: List[PhaseOption[LoadConfig]] = List()
}

// Parse phase config
case class LoadConfig() extends Config
