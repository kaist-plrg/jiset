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

    // compile Test262 programs using Babel
    if (config.compiled) {
      println("install a wrapper of babel...")
      executeCmd("npm install", s"$BASE_DIR/lib/compiler")
      println("compile Test262 programs using Babel...")
      executeCmd("npm run start", s"$BASE_DIR/lib/compiler")
    }

    // base directory
    val baseDir =
      if (config.concrete) s"$LOG_DIR/collect/concrete"
      else if (config.compiled) s"$LOG_DIR/collect/compiled"
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
    val harnessDir =
      if (config.compiled) s"$LOG_DIR/compile/harness"
      else s"$TEST262_DIR/harness"
    val harnessBases =
      if (exists(harnessCache)) readJson[Set[String]](harnessCache)
      else {
        val harnessSet = targets.foldLeft(Set("assert.js", "sta.js")) {
          case (acc, NormalTestConfig(_, includes)) => acc ++ includes
          case (acc, _) => acc
        }
        val data = harnessSet.flatMap(jsName => {
          JsCollector(parseFile(s"$harnessDir/$jsName")).createdVars
        })
        dumpJson(data, harnessCache, true)
        data
      }

    // // XXX for debug
    // val filename = getFirstFilename(jisetConfig, "parse")
    // val ast = parseFile(filename)
    // println(if (config.concrete) js.Collector(ast, harnessBases).toJson else analyzer.Collector(ast, 0, 0).toJson)

    println(s"Collecting [${config.start}, ${config.end})")

    // dump ijk
    val nfIJK = getPrintWriter(s"$LOG_DIR/collect/ijk.csv")
    def dumpIJK(id: Int, name: String, absSem: AbsSemantics): Unit = {
      val ijkInfo = absSem.irIJK.getList ++ absSem.jsIJK.getList
      nfIJK.println(s"$id,$name,${absSem.getIter},${ijkInfo.mkString(",")}")
      nfIJK.flush
    }

    // dump config
    dumpJson(
      List(LOOP_ITER, LOOP_DEPTH, JS_CALL_DEPTH, IR_CALL_DEPTH),
      s"$LOG_DIR/collect/ijk-info"
    )

    if (LOG)
      nfIJK.println(s"id,name,#iter,ir-i,ir-j,ir-k,js-i,js-j,js-k")

    // collect
    ProgressBar("collect", targets.zipWithIndex).foreach(target => {
      // parse test262
      val (NormalTestConfig(name, includes), offset) = target
      val idx = config.start + offset
      val jsName =
        if (config.compiled) s"$LOG_DIR/compile/test262/$name"
        else s"$TEST262_TEST_DIR/$name"

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

      // get the result
      val result = try {
        if (config.concrete) JsCollector(merged, harnessBases).toJson
        else {
          val absSem = AbsSemantics(merged, 0, timeLimit = Some(ANALYSIS_TIMEOUT))
          val jsonStr = analyzer.Collector(absSem, idx, __JSAVER_START_TIME__).toJson
          if (LOG) dumpIJK(idx, name, absSem) // dump ijk info
          jsonStr
        }
      } catch {
        case e: AnalysisImprecise =>
          analyzer.Collector.toImpreciseJson(idx, __JSAVER_START_TIME__)
        case e: Throwable =>
          println(e)
          dumpFile(e.toString, s"$errorDir/$idx")
          analyzer.Collector.toErrorJson(idx, __JSAVER_START_TIME__)
      }

      // dump js states
      dumpFile(result, s"$baseDir/$idx.json")
    })

    // close file handle
    nfIJK.close
  }

  def defaultConfig: CollectConfig = CollectConfig()
  val options: List[PhaseOption[CollectConfig]] = List(
    ("concrete", BoolOption(c => c.concrete = true),
      "collect concrete state."),
    ("compiled", BoolOption(c => c.compiled = true),
      "collect final states using compiled programs."),
    ("start", NumOption((c, i) => c.start = i), ""),
    ("end", NumOption((c, i) => c.end = i), ""),
    ("inf-sens", BoolOption(c => INF_SENS = true),
      "use infinite sensitivity."),
    ("loop-iter", NumOption((c, i) => LOOP_ITER = i),
      "set maximum loop iteration."),
    ("loop-depth", NumOption((c, i) => LOOP_DEPTH = i),
      "set maximum loop depth."),
    ("js-k-cfa", NumOption((c, i) => JS_CALL_DEPTH = i),
      "set k for JavaScrpt callsite sensitivity."),
    ("ir-k-cfa", NumOption((c, i) => IR_CALL_DEPTH = i),
      "set k for IRES callsite sensitivity."),
  )
}

// Parse phase config
case class CollectConfig(
  var concrete: Boolean = false,
  var compiled: Boolean = false,
  var start: Int = 0,
  var end: Int = 18556
) extends Config
