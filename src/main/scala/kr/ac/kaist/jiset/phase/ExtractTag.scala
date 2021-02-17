package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.parser.ECMAScriptParser.preprocess
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._
import org.jsoup.nodes._

// ExtractTag phase
case object ExtractTag extends PhaseObj[Unit, ExtractTagConfig, Unit] {
  val name = "extract-tag"
  val help = "Extract the content of the tag to stdout"

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: ExtractTagConfig
  ): Unit = {
    println(s"--------------------------------------------------")
    val ExtractTagConfig(versionOpt, tagOpt) = config
    val version = versionOpt.getOrElse("recent")
    println(s"version: $version (${getRawVersion(version)})")
    println(s"extracting tag from spec.html...")
    lazy val noTargetTag = {
      println("[No Name Input] Pass the target tag name!")
      "_NoTargetTag_"
    }
    val tag = tagOpt.getOrElse(noTargetTag)

    implicit val (_, document, _) = preprocess(version)

    val elems = document.getElementsByTag(tag)
    for (elem <- elems.toArray) println(elem)
  }

  def defaultConfig: ExtractTagConfig = ExtractTagConfig()

  val options: List[PhaseOption[ExtractTagConfig]] = List(
    ("version", StrOption((c, s) => c.version = Some(s)),
      "set the git version of ecma262."),
    ("name", StrOption((c, s) => c.name = Some(s)),
      "set target tag name")
  )
}

// ExtractTag phase config
case class ExtractTagConfig(
  var version: Option[String] = None,
  var name: Option[String] = None
) extends Config
