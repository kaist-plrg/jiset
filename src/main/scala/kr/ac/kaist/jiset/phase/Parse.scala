package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._

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
    println(s"parsing spec.html...")
    ECMAScript(
      version = config.version.getOrElse(""),
      query = config.query.getOrElse(""),
      detail = config.detail
    )
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
