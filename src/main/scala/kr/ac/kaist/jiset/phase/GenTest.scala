package kr.ac.kaist.jiset.phase

import java.io.File
import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset._
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
    // util
    val json2ir = changeExt("json", "ir")

    // gen grammar test
    def genGrammarTest: Unit = {
      mkdir(GRAMMAR_DIR)
      for (version <- VERSIONS) {
        val filename = s"$GRAMMAR_DIR/$version.grammar"
        val (grammar, _) = ECMAScriptParser.parseGrammar(version)
        dumpFile(grammar.toString, filename)
      }
    }

    // gen legacy test
    def genLegacyTest: Unit = {
      mkdir(LEGACY_COMPILE_DIR)
      for (file <- walkTree(LEGACY_COMPILE_DIR)) {
        val filename = file.getName
        if (jsonFilter(filename)) {
          val jsonName = file.toString
          val steps = readJson[List[Step]](jsonName)
          val tokens = Step.toTokens(steps)
          val inst = Compiler(tokens)

          val irName = json2ir(jsonName)
          dumpFile(beautify(inst), irName)
        }
      }
    }

    // gen basic test
    def genBasicTest: Unit = {
      for (version <- VERSIONS) {
        val baseDir = s"$COMPILE_DIR/$version"

        // get spec, document, grammar
        val spec = ECMAScriptParser(version, "", false)
        implicit val document = ECMAScriptParser.preprocess(version)._2
        implicit val grammar = spec.grammar

        mkdir(baseDir)
        spec.algos.foreach(algo => {
          val Algo(head, body, code) = algo
          // flle name
          val filename = s"$baseDir/${algo.name}"
          // dump code
          dumpFile(algo.code.mkString(LINE_SEP), s"$filename.spec")
          // dump tokens of steps
          dumpJson[List[Step]](TokenParser.getSteps(code), s"$filename.json")
          // dump ir
          dumpFile(beautify(body), s"$filename.ir")
        })
      }
    }

    genGrammarTest
    // genLegacyTest
    genBasicTest
  }

  def defaultConfig: GenTestConfig = GenTestConfig()
  val options: List[PhaseOption[GenTestConfig]] = Nil
}

// GenTest phase config
case class GenTestConfig() extends Config
