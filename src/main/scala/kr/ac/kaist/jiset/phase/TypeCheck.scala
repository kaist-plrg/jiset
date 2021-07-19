package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.JsonProtocol._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset._

// TypeCheck phase
case object TypeCheck extends Phase[CFG, TypeCheckConfig, Unit] {
  val name = "type-check"
  val help = "performs type anaysis for specifications."

  def apply(
    cfg: CFG,
    jisetConfig: JISETConfig,
    config: TypeCheckConfig
  ): Unit = {
    init(cfg)

    val result = config.load match {
      case Some(filename) =>
        val (_, result) = time(
          s"loading type anaysis results from $filename",
          readJson[AnalysisResult](filename)
        )
        AbsSemantics.load(result)
        result
      case None =>
        AnalysisStat.analysisStartTime = System.currentTimeMillis
        AbsTransfer.compute
        AbsSemantics.toResult
    }

    PARTIAL_MODEL.map(dirname => time(s"dump models to $dirname", {
      mkdir(dirname)
      for (algo <- cfg.spec.algos) {
        val name = algo.name
        val filename = s"$dirname/$name.model"
        dumpFile(PartialModel.getString(algo), filename)
      }
    }))

    // dump in a JSON fomrat
    config.json.map(dumpJson("type analysis results", result, _))
  }

  def defaultConfig: TypeCheckConfig = TypeCheckConfig()
  val options: List[PhaseOption[TypeCheckConfig]] = List(
    ("dot", BoolOption(c => DOT = true),
      "dump the analyzed cfg in a dot format."),
    ("pdf", BoolOption(c => { DOT = true; PDF = true }),
      "dump the analyze cfg in a dot and pdf format."),
    ("no-prune", BoolOption(c => PRUNE = false),
      "no abstract state pruning."),
    ("insens", BoolOption(c => USE_VIEW = false),
      "not use type sensitivity for parameters."),
    ("check-alarm", BoolOption(c => CHECK_ALARM = true),
      "check alarms."),
    ("target", StrOption((c, s) => TARGET = Some(s)),
      "set the target of analysis."),
    ("repl", BoolOption(c => REPL = true),
      "use analyze-repl."),
    ("partial-model", StrOption((c, s) => PARTIAL_MODEL = Some(s)),
      "dump partial models using type check results."),
    ("load", StrOption((c, s) => c.load = Some(s)),
      "load type analysis results from JSON."),
    ("json", StrOption((c, s) => c.json = Some(s)),
      "dump type analysis results in a JSON format."),
  )
}

// TypeCheck phase config
case class TypeCheckConfig(
  var load: Option[String] = None,
  var json: Option[String] = None
) extends Config
