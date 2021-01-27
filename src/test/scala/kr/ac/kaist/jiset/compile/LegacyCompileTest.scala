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
        lazy val jsonName = file.toString
        lazy val irName = json2ir(jsonName)

        lazy val steps = readJson[List[Step]](jsonName)
        lazy val tokens = Step.toTokens(steps)
        lazy val answer = Parser.parseInst(readFile(irName))
        lazy val result = Compiler(tokens)

        check(tag, filename, {
          val pass = compare(result, answer)
          if (!pass) {
            println(s"- answer: ${beautify(answer)}")
            println(s"- result: ${beautify(result)}")
          }
          assert(pass)
        })
      }
    }
  }
}
