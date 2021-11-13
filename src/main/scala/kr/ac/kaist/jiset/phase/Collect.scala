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
    setSpec(loadSpec(s"$VERSION_DIR/generated"))
    mkdir(s"$LOG_DIR/collect")

    // read test262 list
    val tests = readFile(s"$BASE_DIR/tests/analyze-test262").split(LINE_SEP).toList
    val targets = Test262.config.normal.filter(tests contains _.name)

    // collect
    ProgressBar("collect", targets.zipWithIndex).foreach(config => {
      // parse test262
      val (NormalTestConfig(name, includes), idx) = config
      val jsName = s"$TEST262_TEST_DIR/$name"
      val includeStmts = includes.foldLeft(basicStmts) {
        case (li, s) => for {
          x <- li
          y <- getInclude(s)
        } yield x ++ y
      } match {
        case Right(l) => l
        case Left(msg) => throw NotSupported(msg)
      }
      val stmts = includeStmts ++ flattenStmt(parseFile(jsName))
      val merged = mergeStmt(stmts)

      // collect js states
      val collector = Collector(merged)

      // dump js states
      dumpFile(collector.toJson, s"$LOG_DIR/collect/$idx.json")
    })
  }

  def defaultConfig: CollectConfig = CollectConfig()
  val options: List[PhaseOption[CollectConfig]] = List()
}

// Parse phase config
case class CollectConfig() extends Config
