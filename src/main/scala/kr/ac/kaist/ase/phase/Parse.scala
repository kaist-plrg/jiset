package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.node.ast._
import kr.ac.kaist.ase.parser.ASTParser
import kr.ac.kaist.ase.util.Useful.fileReader
import kr.ac.kaist.ase.error.NoFileError
import scala.io.Source

// Parse phase
case object Parse extends PhaseObj[Unit, ParseConfig, Script] {
  val name = "parse"
  val help = "Parses AST files."

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: ParseConfig
  ): Script = aseConfig.fileNames match {
    case Nil => throw NoFileError("parse")
    case filename :: _ =>
      ASTParser.parseAll(ASTParser.Script, fileReader(filename)).get
  }

  def defaultConfig: ParseConfig = ParseConfig()
  val options: List[PhaseOption[ParseConfig]] = List()
}

// Parse phase config
case class ParseConfig() extends Config
