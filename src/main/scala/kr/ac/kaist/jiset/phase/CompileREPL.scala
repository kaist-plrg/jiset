package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.spec.Region
import kr.ac.kaist.jiset.parser.algorithm.{ CompileREPL => REPL }
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._

// CompileREPL phase
case object CompileREPL extends PhaseObj[Unit, CompileREPLConfig, Unit] {
  val name = "compile-repl"
  val help = "REPL for printing compile result of particular step"

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: CompileREPLConfig
  ): Unit = {
    println(s"--------------------------------------------------")
    val CompileREPLConfig(versionOpt, detail) = config
    val version = versionOpt.getOrElse("recent")
    println(s"version: $version (${getRawVersion(version)})")
    println(s"parsing spec.html...")
    implicit val (lines, document, region) = ECMAScriptParser.preprocess(version)
    implicit val (grammar, _) = ECMAScriptParser.parseGrammar(version)
    val secIds = ECMAScriptParser.parseHeads()._1

    println(s"* grammar:")
    println(s"  - lexical production: ${grammar.lexProds.length}")
    println(s"  - non-lexical production: ${grammar.prods.length}")

    REPL.run(secIds)
  }

  def defaultConfig: CompileREPLConfig = CompileREPLConfig()
  val options: List[PhaseOption[CompileREPLConfig]] = List(
    ("version", StrOption((c, s) => c.version = Some(s)),
      "set the git version of ecma262."),
    ("detail", BoolOption(c => c.detail = true),
      "print log")
  )
}

// CompileREPL phase config
case class CompileREPLConfig(
    var version: Option[String] = None,
    var detail: Boolean = false
) extends Config
