package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.{ LINE_SEP, JISETConfig }
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import scala.io.Source
import spray.json._

// Parse phase
case object Parse extends PhaseObj[Unit, ParseConfig, Script] {
  val name = "parse"
  val help = "Parses AST files."

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: ParseConfig
  ): Script = {
    val filename = getFirstFilename(jisetConfig, "parse")
    val ast = Parser.parse(Parser.Script(Nil), fileReader(filename)).get
    config.jsonFile match {
      case Some(name) =>
        val nf = getPrintWriter(name)
        nf.println(ast.toJson.prettyPrint)
        nf.close()
      case None =>
    }
    ast
  }

  def defaultConfig: ParseConfig = ParseConfig()
  val options: List[PhaseOption[ParseConfig]] = List(
    ("json", StrOption((c, s) => c.jsonFile = Some(s)),
      "dump JSON of AST tree into a file."),
    ("esparse", BoolOption(c => c.esparse = true),
      "use `esparse` instead of the generated parser."),
  )
}

// Parse phase config
case class ParseConfig(
  var jsonFile: Option[String] = None,
  var esparse: Boolean = false
) extends Config
