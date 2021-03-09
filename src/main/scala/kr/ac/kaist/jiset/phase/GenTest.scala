package kr.ac.kaist.jiset.phase

import java.io.File
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.parser.algorithm.Compiler
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.parser.algorithm.TokenParser
import org.jsoup.nodes._
import spray.json._

// GenTest phase
case object GenTest extends PhaseObj[Unit, GenTestConfig, Unit] {
  val name: String = "gen-test"
  val help: String = "generates test answers."

  type Parsed = ((Array[String], Document, Region), ECMAScript)
  def apply(
    non: Unit,
    jisetConfig: JISETConfig,
    config: GenTestConfig
  ): Unit = {
    val parsedMap = (for (version <- VERSIONS) yield time(s"parse $version", {
      val input = ECMAScriptParser.preprocess(version)
      val spec = ECMAScriptParser(input, "", false, false)
      version -> (input, spec)
    })).toMap
    genGrammarTest(parsedMap)
    genCFGTest(parsedMap)
    genLegacyTest
    genBasicTest(parsedMap)
  }

  // util
  val json2ir = changeExt("json", "ir")

  // generate grammar test
  def genGrammarTest(parsedMap: Map[String, Parsed]): Unit =
    time("generate grammar tests", {
      mkdir(GRAMMAR_DIR)
      for ((version, (_, spec)) <- parsedMap) {
        val filename = s"$GRAMMAR_DIR/$version.grammar"
        dumpFile(spec.grammar.toString, filename)
      }
    })

  // generate cfg test
  def genCFGTest(parsedMap: Map[String, Parsed]): Unit = time("generate cfg tests", {
    mkdir(CFG_TEST_DIR)
    for ((version, (_, spec)) <- parsedMap) {
      val baseDir = s"$CFG_TEST_DIR/$version"

      mkdir(baseDir)
      spec.algos.foreach(algo => {
        val func = Translator(algo).toDot
        dumpFile(func, s"$baseDir/${algo.name}.dot")
      })
    }
  })

  // generate legacy test
  def genLegacyTest: Unit = time("generate legacy tests", {
    mkdir(LEGACY_COMPILE_DIR)
    for (file <- walkTree(LEGACY_COMPILE_DIR)) {
      val filename = file.getName
      if (jsonFilter(filename)) {
        val jsonName = file.toString
        val tokens = readJson[List[Token]](jsonName)
        val inst = Compiler(tokens)

        val irName = json2ir(jsonName)
        dumpFile(inst.beautified(index = false, asite = false), irName)
      }
    }
  })

  // generate basic test
  def genBasicTest(parsedMap: Map[String, Parsed]): Unit =
    for ((version, (input, spec)) <- parsedMap) time(s"generate $version tests", {
      val baseDir = s"$BASIC_COMPILE_DIR/$version"

      // get spec, document, grammar, secIds
      implicit val (lines, document, region) = input
      implicit val grammar = spec.grammar
      val (secIds, _) = ECMAScriptParser.parseHeads()

      mkdir(baseDir)
      spec.algos.foreach(algo => {
        val Algo(head, rawBody, code) = algo
        // file name
        val filename = s"$baseDir/${algo.name}"
        // dump code
        dumpFile(algo.code.mkString(LINE_SEP), s"$filename.spec")
        // dump tokens of steps
        val tokens = TokenParser.getTokens(code, secIds)
        dumpJson(tokens, s"$filename.json")
        // dump ir
        dumpFile(rawBody.beautified(index = false, asite = false), s"$filename.ir")
      })
    })

  def defaultConfig: GenTestConfig = GenTestConfig()
  val options: List[PhaseOption[GenTestConfig]] = Nil
}

// GenTest phase config
case class GenTestConfig() extends Config
