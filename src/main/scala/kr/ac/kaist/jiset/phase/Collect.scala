package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.{ Parser => JSParser, _ }
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.spec.NativeHelper._
import kr.ac.kaist.jiset.checker.NativeHelper._
import kr.ac.kaist.jiset.error.NotSupported
import kr.ac.kaist.jiset.js.Test262._
import scala.io.Source

// Collect phase
case object Collect extends Phase[Unit, CollectConfig, Unit] {
  val name = "collect"
  val help = "collects a JS state from concrete evaluation"

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: CollectConfig
  ): Unit = {
    val filename = getFirstFilename(jisetConfig, "parse")
    setSpec(loadSpec(s"$VERSION_DIR/generated"))
    val ast = parseFile(filename)
    val collector = Collector(ast)
    dumpFile(collector.toJson, s"$BASE_DIR/collect.json")
  }

  def defaultConfig: CollectConfig = CollectConfig()
  val options: List[PhaseOption[CollectConfig]] = List()
}

// Parse phase config
case class CollectConfig() extends Config
