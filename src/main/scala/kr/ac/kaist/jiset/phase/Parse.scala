package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._

import kr.ac.kaist.jiset.spec.algorithm.Name

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
    val ParseConfig(versionOpt, queryOpt, detail) = config
    val version = versionOpt.getOrElse("recent")
    val query = queryOpt.getOrElse("")
    println(s"version: $version (${getRawVersion(version)})")
    if (query != "") println(s"query: $query")
    println(s"parsing spec.html...")
    val spec = ECMAScriptParser(version, query, detail)

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

    spec
  }

  def defaultConfig: ParseConfig = ParseConfig()
  val options: List[PhaseOption[ParseConfig]] = List(
    ("version", StrOption((c, s) => c.version = Some(s)),
      "set the git version of ecma262."),
    ("query", StrOption((c, s) => c.query = Some(s)),
      "set target query"),
    ("detail", BoolOption(c => c.detail = true),
      "print log")
  )
}

// Parse phase config
case class ParseConfig(
  var version: Option[String] = None,
  var query: Option[String] = None,
  var detail: Boolean = false
) extends Config
