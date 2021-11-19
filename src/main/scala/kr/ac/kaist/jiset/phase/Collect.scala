package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.setSpec
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.error._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.spec.NativeHelper._
import kr.ac.kaist.jiset.checker.NativeHelper._
import kr.ac.kaist.jiset.error.NotSupported
import kr.ac.kaist.jiset.js.{ Collector => JsCollector, _ }
import kr.ac.kaist.jiset.js.Test262._
import scala.io.Source

// Collect phase
case object Collect extends Phase[Unit, CollectConfig, Unit] {
  val name = "collect"
  val help = "collects a JS state from concrete/abstract evaluation"

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: CollectConfig
  ): Unit = {
    setSpec(loadSpec(s"$VERSION_DIR/generated"))

    // base directory
    val baseDir =
      if (config.concrete) s"$LOG_DIR/collect/concrete"
      else s"$LOG_DIR/collect/jsaver"
    val errorDir = s"$LOG_DIR/collect/error"
    mkdir(s"$LOG_DIR/collect")
    mkdir(baseDir)
    mkdir(errorDir)

    // read test262 list
    val tests = readFile(s"$BASE_DIR/tests/analyze-test262").split(LINE_SEP).toList
    val targets = Test262.config.normal.filter(tests contains _.name).slice(config.start, config.end)

    // load harness declaration
    val harnessCache = s"$LOG_DIR/collect/harness.json"
    val harnessBases =
      if (exists(harnessCache)) readJson[Set[String]](harnessCache)
      else {
        val harnessSet = targets.foldLeft(Set("assert.js", "sta.js")) {
          case (acc, NormalTestConfig(_, includes)) => acc ++ includes
          case (acc, _) => acc
        }
        val data = harnessSet.flatMap(jsName => {
          JsCollector(parseFile(s"$TEST262_DIR/harness/$jsName")).createdVars
        })
        dumpJson(data, harnessCache, true)
        data
      }

    // // XXX for debug
    // val filename = getFirstFilename(jisetConfig, "parse")
    // val ast = parseFile(filename)
    // println(if (config.concrete) js.Collector(ast, harnessBases).toJson else analyzer.Collector(ast, 0, 0).toJson)

    println(s"Collecting [${config.start}, ${config.end})")

    // collect
    ProgressBar("collect", targets.zipWithIndex).foreach(target => {
      // parse test262
      val (NormalTestConfig(name, includes), offset) = target
      val idx = config.start + offset
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

      val __JSAVER_START_TIME__ = System.currentTimeMillis // start to measure time
      val stmts = includeStmts ++ flattenStmt(parseFile(jsName))
      val merged = mergeStmt(stmts)

      // start to measure parse time
      __INIT_PARSE_TIME__

      // dump js states
      dumpFile(
        if (config.concrete) JsCollector(merged, harnessBases).toJson
        else {
          try { analyzer.Collector(merged, idx, __JSAVER_START_TIME__).toJson }
          catch {
            case e: AnalysisImprecise =>
              analyzer.Collector.toImpreciseJson(idx, __JSAVER_START_TIME__)
            case e: Throwable =>
              println(e)
              dumpFile(e.toString, s"$errorDir/$idx")
              analyzer.Collector.toErrorJson(idx, __JSAVER_START_TIME__)
          }
        },
        s"$baseDir/$idx.json"
      )
    })
  }

  def defaultConfig: CollectConfig = CollectConfig()
  val options: List[PhaseOption[CollectConfig]] = List(
    ("concrete", BoolOption(c => c.concrete = true),
      "collect concrete state"),
    ("start", NumOption((c, i) => c.start = i), ""),
    ("end", NumOption((c, i) => c.end = i), ""),
  )
}

// Parse phase config
case class CollectConfig(
  var concrete: Boolean = false,
  var start: Int = 0,
  var end: Int = 18556
) extends Config
