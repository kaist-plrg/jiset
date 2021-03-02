package kr.ac.kaist.jiset.phase

import java.io.File
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.parser.algorithm.Compiler
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.parser.algorithm.TokenParser
import spray.json._

// GenTest phase
case object GenTest extends PhaseObj[Unit, GenTestConfig, Unit] {
  val name: String = "gen-test"
  val help: String = "generates test answers."

  def apply(
    non: Unit,
    jisetConfig: JISETConfig,
    config: GenTestConfig
  ): Unit = {
    genGrammarTest
    genLegacyTest
    genBasicTest
  }

  // util
  val json2ir = changeExt("json", "ir")

  // gen grammar test
  def genGrammarTest: Unit = time("generate grammar tests", {
    mkdir(GRAMMAR_DIR)
    for (version <- VERSIONS) {
      val filename = s"$GRAMMAR_DIR/$version.grammar"
      val (grammar, _) = ECMAScriptParser.parseGrammar(version)
      dumpFile(grammar.toString, filename)
    }
  })

  // gen legacy test
  def genLegacyTest: Unit = time("generate legacy tests", {
    mkdir(LEGACY_COMPILE_DIR)
    for (file <- walkTree(LEGACY_COMPILE_DIR)) {
      val filename = file.getName
      if (jsonFilter(filename)) {
        val jsonName = file.toString
        val tokens = readJson[List[Token]](jsonName)
        val inst = Compiler(tokens)

        val irName = json2ir(jsonName)
        dumpFile(inst.beautified(index = false, exprId = false), irName)
      }
    }
  })

  // gen basic test
  def genBasicTest: Unit =
    for (version <- VERSIONS) time(s"generate $version tests", {
      val baseDir = s"$BASIC_COMPILE_DIR/$version"

      // get spec, document, grammar, secIds
      val spec = ECMAScriptParser(version, "", false, false)
      implicit val (lines, document, region) = ECMAScriptParser.preprocess(version)
      implicit val grammar = spec.grammar
      val secIds = ECMAScriptParser.parseHeads()._1

      mkdir(baseDir)
      spec.algos.foreach(algo => {
        val Algo(head, rawBody, code) = algo
        // flle name
        val filename = s"$baseDir/${algo.name}"
        // dump code
        dumpFile(algo.code.mkString(LINE_SEP), s"$filename.spec")
        // dump tokens of steps
        val tokens = TokenParser.getTokens(code, secIds)
        dumpJson(tokens, s"$filename.json")
        // dump ir
        dumpFile(rawBody.beautified(index = false, exprId = false), s"$filename.ir")
      })
    })

  def defaultConfig: GenTestConfig = GenTestConfig()
  val options: List[PhaseOption[GenTestConfig]] = Nil
}

// GenTest phase config
case class GenTestConfig() extends Config
