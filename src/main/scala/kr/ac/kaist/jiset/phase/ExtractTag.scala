package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._
import org.jsoup.nodes._

// ExtractTag phase
case object ExtractTag extends PhaseObj[Unit, ExtractTagConfig, List[Element]] {
  val name = "extract-tag"
  val help = "Extract the content of the tag to stdout"

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: ExtractTagConfig
  ): List[Element] = {
    val version = config.version.getOrElse("recent")
    println(s"version: $version (${getRawVersion(version)})")

    implicit val (_, document, _) = time("preprocess", {
      ECMAScriptParser.preprocess(version)
    })

    val elems = time("extract contents of tags from spec.html", for {
      tag <- jisetConfig.args
      elem <- toArray(document.getElementsByTag(tag))
    } yield elem)

    elems
  }

  def defaultConfig: ExtractTagConfig = ExtractTagConfig()

  val options: List[PhaseOption[ExtractTagConfig]] = List(
    ("version", StrOption((c, s) => c.version = Some(s)),
      "set the git version of ecma262."),
  )
}

// ExtractTag phase config
case class ExtractTagConfig(
  var version: Option[String] = None
) extends Config
