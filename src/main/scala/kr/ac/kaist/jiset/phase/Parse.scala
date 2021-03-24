package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._

// Parse phase
case object Parse extends PhaseObj[Unit, ParseConfig, ECMAScript] {
  val name = "parse"
  val help = "Parses spec.html to an ECMAScript object"

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: ParseConfig
  ): ECMAScript = {
    val spec = config.load match {
      case Some(filename) =>
        time(s"loading ECMAScript from $filename", {
          readJson[ECMAScript](filename)
        })
      case None =>
        val version = config.version.getOrElse("recent")
        val query = config.query.getOrElse("")
        println(s"version: $version (${getRawVersion(version)})")
        if (query != "") println(s"query: $query")
        time(s"parsing spec.html", {
          ECMAScriptParser(version, query, config.detail)
        })
    }

    // logging
    if (LOG) dumpIncompleteAlgos(spec.incompletedAlgos)

    // Dump JSON
    config.json.map(dumpJson("specification", spec, _))

    spec
  }

  // dump incompleted algorithms and their names
  def dumpIncompleteAlgos(algos: List[Algo]): Unit = {
    // create directory
    val dir = s"$PARSE_LOG_DIR/incompleted"
    mkdir(dir)

    // dump incompleted algorithms names
    val names = algos.map(_.name).mkString(LINE_SEP)
    dumpFile("incomplete algorithm names", names, s"$dir/names.log")

    // dump incompleted algorithms
    val app = new Appender
    for (algo <- algos) {
      app >> "========================================" >> LINE_SEP
      app >> algo.name >> " ====>" >> LINE_SEP
      for ((t, i) <- algo.todos.zipWithIndex)
        app >> "[" >> i >> "]" >> t >> LINE_SEP
    }
    dumpFile(
      "incomplete algorithms",
      app.toString,
      s"$dir/algorithms.log"
    )
  }

  def defaultConfig: ParseConfig = ParseConfig()
  val options: List[PhaseOption[ParseConfig]] = List(
    ("version", StrOption((c, s) => c.version = Some(s)),
      "set the git version of ecma262."),
    ("query", StrOption((c, s) => c.query = Some(s)),
      "set target query."),
    ("load", StrOption((c, s) => c.load = Some(s)),
      "load ECMAScript from JSON."),
    ("json", StrOption((c, s) => c.json = Some(s)),
      "dump ECMAScript in a JSON format."),
    ("detail", BoolOption(c => c.detail = true),
      "print log.")
  )
}

// Parse phase config
case class ParseConfig(
  var version: Option[String] = None,
  var query: Option[String] = None,
  var load: Option[String] = None,
  var json: Option[String] = None,
  var detail: Boolean = false
) extends Config
