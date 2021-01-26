package kr.ac.kaist.jiset

import java.io._
import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.parser.algorithm.Compiler
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.util.Useful._
import org.scalatest._

class LegacyCompileTest extends CompileTest {
  // tag name
  override def tag = "legacyCompile"

  // registration
  override def executeTests: Unit = {
    val json2ir = changeExt("json", "ir")
    for (file <- walkTree(LEGACY_COMPILE_DIR)) {
      val filename = file.getName
      if (jsonFilter(filename)) {
        val jsonName = file.toString
        val irName = json2ir(jsonName)

        val steps = readJson[List[Step]](jsonName)
        val tokens = Step.toTokens(steps)
        val inst = Parser.parseInst(readFile(irName))

        check(tag, filename, {
          diffTest(filename, Compiler(tokens), inst)
        })
      }
    }
  }
}
