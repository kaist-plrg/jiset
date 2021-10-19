package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.{ Parser => JSParser, _ }
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.spec.NativeHelper._
import kr.ac.kaist.jiset.checker.NativeHelper._
import scala.io.Source

// Load phase
case object Load extends Phase[Script, LoadConfig, State] {
  val name = "load"
  val help = "loads a JavaScript AST to the initial IR states."

  def apply(
    script: Script,
    jisetConfig: JISETConfig,
    config: LoadConfig
  ): State = {
    val filename = getFirstFilename(jisetConfig, "load")
    setSpec(loadSpec(s"$VERSION_DIR/generated"))
    Initialize(script, Some(filename), config.cursorGen)
  }

  def defaultConfig: LoadConfig = LoadConfig()
  val options: List[PhaseOption[LoadConfig]] = List(
    ("cursor", StrOption((c, s) => s match {
      case "inst" => c.cursorGen = InstCursor
      case "node" => c.cursorGen = NodeCursor
      case _ => c.cursorGen = NodeCursor
    }), "set the type of evaluation cursors (default: node)."),
  )
}

// Parse phase config
case class LoadConfig(
  var cursorGen: CursorGen[_ <: Cursor] = NodeCursor
) extends Config
