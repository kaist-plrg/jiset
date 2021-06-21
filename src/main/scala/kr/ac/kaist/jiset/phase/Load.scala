package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.{ Parser => JSParser, _ }
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Useful._
import scala.io.Source

// Load phase
case object Load extends PhaseObj[Script, LoadConfig, State] {
  val name = "load"
  val help = "read script object and using global, convert to State object."

  def apply(
    script: Script,
    jisetConfig: JISETConfig,
    config: LoadConfig
  ): State = this(script, getFirstFilename(jisetConfig, "load"))

  def apply(script: Script): State = this(script, "unknown")

  def apply(
    script: Script,
    filename: String
  ): State = script match {
    case Script0(Some(body), _, _) => Initialize(
      inst = Parser.parseInst(s"app $RESULT = (RunJobs)"),
      body = body,
      filename = filename,
    )
    case _ => State()
  }

  def defaultConfig: LoadConfig = LoadConfig()
  val options: List[PhaseOption[LoadConfig]] = List()
}

// Parse phase config
case class LoadConfig() extends Config
