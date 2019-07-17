package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.model._
import kr.ac.kaist.ase.util._
import scala.io.Source
import Useful._

// Parse phase
case object Parse extends PhaseObj[Unit, ParseConfig, Script] {
  val name = "parse"
  val help = "Parses AST files."

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: ParseConfig
  ): Script = {
    val filename = getFirstFilename(aseConfig, "parse")
    val ast = Parser(filename)
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
