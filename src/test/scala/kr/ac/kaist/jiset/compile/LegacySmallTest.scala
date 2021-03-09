package kr.ac.kaist.jiset.compile

import java.io._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.parser.algorithm.Compiler
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.util.Useful._
import org.scalatest._

class LegacySmallTest extends CompileTest {
  val name: String = "compileLegacyTest"

  // helper
  val json2ir = changeExt("json", "ir")

  // registration
  def init: Unit = check("legacy", {
    for (file <- walkTree(LEGACY_COMPILE_DIR)) {
      val filename = file.getName
      if (jsonFilter(filename)) {
        lazy val jsonName = file.toString
        lazy val irName = json2ir(jsonName)

        lazy val tokens = readJson[List[Token]](jsonName)
        lazy val answer = Parser.parseInst(readFile(irName))
        lazy val result = Compiler(tokens)

        difftest(filename, result, answer)
      }
    }
  })
  init
}
