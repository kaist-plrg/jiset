package kr.ac.kaist.jiset.phase

import java.io.File
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.parser.algorithm.Compiler
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.UIdGen
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.parser.algorithm.TokenParser
import org.jsoup.nodes._
import spray.json._

// GenTest phase
case object GenTest extends PhaseObj[Unit, GenTestConfig, Unit] {
  val name: String = "gen-test"
  val help: String = "generate test answers."

  type Parsed = ((Array[String], Document, Region), ECMAScript)
  def apply(
    non: Unit,
    jisetConfig: JISETConfig,
    config: GenTestConfig
  ): Unit = {
    TEST_MODE = true
    val parsedMap = (for (version <- VERSIONS) yield time(s"parse $version", {
      val input = ECMAScriptParser.preprocess(version)
      val spec = ECMAScriptParser(version, input, "", false)
      version -> (input, spec)
    })._2).toMap
    genGrammarTest(parsedMap)
    genBasicTest(parsedMap)
  }

  // util
  val json2ir = changeExt("json", "ir")

  // generate grammar test
  def genGrammarTest(parsedMap: Map[String, Parsed]): Unit =
    time("generate grammar tests", {
      mkdir(GRAMMAR_DIR)
      for (version <- VERSIONS) {
        val (_, spec) = parsedMap(version)
        val filename = s"$GRAMMAR_DIR/$version.grammar"
        dumpFile(spec.grammar.toString, filename)
      }
    })

  // generate basic test
  def genBasicTest(parsedMap: Map[String, Parsed]): Unit =
    for (version <- VERSIONS) {
      val (input, spec) = parsedMap(version)
      time(s"generate $version tests", {
        val baseDir = s"$BASIC_COMPILE_DIR/$version"

        // get spec, document, grammar, secIds
        implicit val (lines, document, region) = input
        implicit val grammar = spec.grammar
        val (secIds, _) = ECMAScriptParser.parseHeads()

        mkdir(baseDir)
        for {
          algo <- spec.algos
          if algo.code != Nil
        } {
          val Algo(head, ids, rawBody, code) = algo
          // file name
          val filename = s"$baseDir/${algo.name}"
          // dump code
          dumpFile(algo.code.mkString(LINE_SEP), s"$filename.spec")
          // dump tokens of steps
          val tokens = TokenParser.getTokens(code)
          dumpJson(tokens, s"$filename.json")
          // dump ir
          dumpFile(rawBody.beautified(index = false, asite = false), s"$filename.ir")
        }
      })
    }

  def defaultConfig: GenTestConfig = GenTestConfig()
  val options: List[PhaseOption[GenTestConfig]] = Nil
}

// GenTest phase config
case class GenTestConfig() extends Config
