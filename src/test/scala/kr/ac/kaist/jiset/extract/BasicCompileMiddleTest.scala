package kr.ac.kaist.jiset.extract

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.extractor.algorithm.{ Compiler, TokenParser }
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.util.Useful._
import org.scalatest._
import kr.ac.kaist.jiset.spec.algorithm.token.Token
import kr.ac.kaist.jiset.extractor.ECMAScriptParser

class BasicCompileMiddleTest extends ExtractTest {
  val name: String = "extractBasicCompileTest"

  // helper
  val spec2json = changeExt("spec", "json")
  val spec2ir = changeExt("spec", "ir")

  // registration
  def init: Unit = check(VERSION, {
    val baseDir = s"$BASIC_COMPILE_DIR/$VERSION"

    // get grammar and document
    implicit val (lines, document, region) = JISETTest.info
    implicit val spec = ECMAScriptParser(VERSION, JISETTest.info, "", false)
    implicit val grammar = spec.grammar
    val (secIds, _) = ECMAScriptParser.parseHeads()

    for (file <- walkTree(baseDir)) {
      val filename = file.getName
      if (specFilter(filename)) {
        val name = s"${removedExt(filename)} @ $VERSION"
        val specName = file.toString
        val (jsonName, irName) = (spec2json(specName), spec2ir(specName))

        // check token parsing
        val tokens = readJson[List[Token]](jsonName)

        val code = readFile(specName).split(LINE_SEP)
        val resultTokens = TokenParser.getTokens(code)
        assert(tokens == resultTokens, "tokens are changed")

        // check compile
        val answer = Inst.fromFile(irName)
        val result = Compiler(VERSION, secIds)(tokens)
        diffTest(name, result, answer)
      }
    }
  })
  init
}
