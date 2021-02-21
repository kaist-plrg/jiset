package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._

// Parse phase
case object Parse extends PhaseObj[Unit, ParseConfig, ECMAScript] {
  val name = "parse"
  val help = "Parses spec.html to Spec"

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: ParseConfig
  ): ECMAScript = {
    println(s"--------------------------------------------------")
    val spec = config.load match {
      case Some(filename) => readJson[ECMAScript](filename)
      case None =>
        val version = config.version.getOrElse("recent")
        val query = config.query.getOrElse("")
        println(s"version: $version (${getRawVersion(version)})")
        if (query != "") println(s"query: $query")
        println(s"parsing spec.html...")
        ECMAScriptParser(version, query, config.useCount, config.detail)
    }

    val ECMAScript(grammar, algos, intrinsics, symbols, aoids, section) = spec
    println(s"* grammar:")
    println(s"  - lexical production: ${grammar.lexProds.length}")
    println(s"  - non-lexical production: ${grammar.prods.length}")
    println(s"* algorithms:")
    println(s"  - incomplete: ${spec.incompletedAlgos.length}")
    println(s"  - complete: ${spec.completedAlgos.length}")
    println(s"  - total: ${algos.length}")
    println(s"* intrinsics: ${intrinsics.size}")
    println(s"* symbols: ${symbols.size}")
    println(s"* aoids: ${aoids.size}")
    println(s"* incompleted steps: ${spec.incompletedAlgos.map(_.todos.length).sum}")

    // make dir
    mkdir(PARSE_LOG_DIR)

    // Dump incomplete list
    dumpFile(
      spec.incompletedAlgos.map(_.name).mkString(LINE_SEP),
      s"$PARSE_LOG_DIR/incomplete_list.log"
    )

    // Dump incomplete steps
    dumpFile(
      spec.incompletedAlgos.map(algo => {
        "========================================" +
          LINE_SEP + s"${algo.name} ====>" + LINE_SEP +
          algo.todos.zipWithIndex.map {
            case (t, i) => s"  [$i] $t"
          }.mkString(LINE_SEP) + LINE_SEP
      }).mkString(LINE_SEP),
      s"$PARSE_LOG_DIR/incomplete_steps.log"
    )

    // Dump JSON
    config.json.foreach(dumpJson(spec, _, false))
    config.prettyJson.foreach(dumpJson(spec, _, true))

    spec
  }

  def defaultConfig: ParseConfig = ParseConfig()
  val options: List[PhaseOption[ParseConfig]] = List(
    ("version", StrOption((c, s) => c.version = Some(s)),
      "set the git version of ecma262."),
    ("query", StrOption((c, s) => c.query = Some(s)),
      "set target query."),
    ("useCount", BoolOption(c => c.useCount = true),
      "use compiler rule counter."),
    ("load", StrOption((c, s) => c.load = Some(s)),
      "load ECMAScript from JSON."),
    ("json", StrOption((c, s) => c.json = Some(s)),
      "dump ECMAScript in a JSON format."),
    ("pretty-json", StrOption((c, s) => c.prettyJson = Some(s)),
      "dump ECMAScript in a JSON pretty format."),
    ("detail", BoolOption(c => c.detail = true),
      "print log.")
  )
}

// Parse phase config
case class ParseConfig(
  var version: Option[String] = None,
  var query: Option[String] = None,
  var useCount: Boolean = false,
  var load: Option[String] = None,
  var json: Option[String] = None,
  var prettyJson: Option[String] = None,
  var detail: Boolean = false
) extends Config
