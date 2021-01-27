package kr.ac.kaist.jiset

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.parser.algorithm.{ Compiler, TokenParser }
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.util.Useful._
import org.scalatest._
import kr.ac.kaist.jiset.spec.algorithm.Step
import kr.ac.kaist.jiset.parser.ECMAScriptParser

abstract class CompileTest extends JISETTest {
  // tag name
  override def tag = "compile"

  // TODO registration
  // override def executeTests: Unit = {
  //   val spec2json = changeExt("spec", "json")
  //   val spec2ir = changeExt("spec", "ir")
  //   for (version <- VERSIONS) {
  //     println(s"Testing $version...")
  //     val baseDir = s"$COMPILE_DIR/$version"

  //     // get grammar and document
  //     implicit val (grammar, document) = ECMAScriptParser.parseGrammar(version)

  //     for (file <- walkTree(baseDir)) {
  //       val filename = file.getName
  //       if (specFilter(filename)) {
  //         val specName = file.toString
  //         val checkName = s"$filename @ $version"
  //         val (jsonName, irName) = (spec2json(specName), spec2ir(specName))

  //         // check token parsing
  //         val steps = readJson[List[Step]](jsonName)
  //         val tokens = Step.toTokens(steps)

  //         val code = readFile(specName).split(LINE_SEP)
  //         val resultTokens = TokenParser.getTokens(code)

  //         check(s"$tag(token)", checkName, tokens == resultTokens)

  //         // check compile
  //         val inst = Parser.fileToInst(irName)
  //         check(s"$tag(compile)", checkName, {
  //           diffTest(filename, Compiler(tokens), inst)
  //         })
  //       }
  //     }
  //   }
  // }
}
