package kr.ac.kaist.jiset.phase

import io.circe._, io.circe.syntax._, io.circe.parser._
import kr.ac.kaist.jiset.error.NotSupported
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.parser.{ MetaParser, MetaData }
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.{ LINE_SEP, JISETConfig }

// Parse phase
case object Parse extends Phase[Unit, ParseConfig, Script] {
  val name = "parse"
  val help = "parses a JavaScript file using the generated parser."

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: ParseConfig
  ): Script = {
    val filename = getFirstFilename(jisetConfig, "parse")
    val ast = parseJS(jisetConfig.args, config.esparse) match {
      case ast if config.test262 => prependedTest262Harness(filename, ast)
      case ast => ast
    }
    config.jsonFile match {
      case Some(name) =>
        val nf = getPrintWriter(name)
        nf.println(ast.toJson.noSpaces)
        nf.close()
      case None =>
    }
    if (config.pprint) println(ast.prettify.noSpaces)

    ast
  }

  // parse JavaScript files
  def parseJS(list: List[String], esparse: Boolean): Script = list match {
    case List(filename) => parseJS(filename, esparse)
    case _ => mergeStmt(for {
      filename <- list
      script = parseJS(filename, esparse)
      item <- flattenStmt(script)
    } yield item)
  }
  def parseJS(filename: String, esparse: Boolean): Script = {
    if (esparse) {
      val code = parse(executeCmd(s"bin/esparse $filename"))
        .getOrElse(error("invalid AST"))
      Script(code)
    } else {
      Parser.parse(Parser.Script(Nil), fileReader(filename)).get
    }
  }

  // prepend harness.js for Test262
  def prependedTest262Harness(filename: String, script: Script): Script = {
    import Test262._
    val meta = MetaParser(filename)
    val includes = meta.includes
    val includeStmts = includes.foldLeft(basicStmts) {
      case (li, s) => for {
        x <- li
        y <- getInclude(s)
      } yield x ++ y
    } match {
      case Right(l) => l
      case Left(msg) => throw NotSupported(msg)
    }
    val stmts = includeStmts ++ flattenStmt(script)
    mergeStmt(stmts)
  }

  def defaultConfig: ParseConfig = ParseConfig()
  val options: List[PhaseOption[ParseConfig]] = List(
    ("json", StrOption((c, s) => c.jsonFile = Some(s)),
      "dump JSON of AST tree into a file."),
    ("pprint", BoolOption(c => c.pprint = true),
      "pretty print AST tree"),
    ("esparse", BoolOption(c => c.esparse = true),
      "use `esparse` instead of the generated parser."),
    ("test262", BoolOption(c => c.test262 = true),
      "prepend test262 harness files based on metadata."),
  )
}

// Parse phase config
case class ParseConfig(
  var jsonFile: Option[String] = None,
  var esparse: Boolean = false,
  var test262: Boolean = false,
  var pprint: Boolean = false
) extends Config
