package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.{ LINE_SEP, JISETConfig }
import kr.ac.kaist.jiset.model._
import kr.ac.kaist.jiset.util._
import scala.io.Source
import Useful._

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
        nf.println(ast.toJson)
        nf.close()
      case None =>
    }
    ast
  }

  def defaultConfig: ParseConfig = ParseConfig()
  val options: List[PhaseOption[ParseConfig]] = List(
    ("json", StrOption((c, s) => c.jsonFile = Some(s)),
      "dump JSON of AST tree into a file.")
  )
}

// Parse phase config
case class ParseConfig(
  var jsonFile: Option[String] = None
) extends Config
