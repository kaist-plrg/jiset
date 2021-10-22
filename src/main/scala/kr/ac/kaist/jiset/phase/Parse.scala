package kr.ac.kaist.jiset.phase

import io.circe._, io.circe.syntax._, io.circe.parser._
import kr.ac.kaist.jiset.BASE_DIR
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

    // parse
    val ast = parseJS(jisetConfig.args, config.esparse)
    val transformed = (config.test262, config.noAssert) match {
      case (true, na) => prependedTest262Harness(filename, ast, na)
      case (false, true) => Test262.AssertionRemover(ast)
      case (false, false) => ast
    }

    // dump json
    config.jsonFile.foreach(name => {
      val nf = getPrintWriter(name)
      nf.println(transformed.toJson.noSpaces)
      nf.close()
    })

    // pretty-print
    if (config.pprint) println(transformed.prettify.noSpaces)

    transformed
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
      val code = parse(executeCmd(s"$BASE_DIR/bin/esparse $filename"))
        .getOrElse(error("invalid AST"))
      Script(code)
    } else {
      Parser.parse(Parser.Script(Nil), fileReader(filename)).get
    }
  }

  // prepend harness.js for Test262
  def prependedTest262Harness(
    filename: String,
    script: Script,
    noAssert: Boolean = false
  ): Script = {
    import Test262._
    val meta = MetaParser(filename)
    loadTest262(script, meta.includes, noAssert)
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
    ("noAssert", BoolOption(c => c.noAssert = true),
      "remove test262 assertions"),
  )
}

// Parse phase config
case class ParseConfig(
  var jsonFile: Option[String] = None,
  var esparse: Boolean = false,
  var test262: Boolean = false,
  var pprint: Boolean = false,
  var noAssert: Boolean = false
) extends Config
