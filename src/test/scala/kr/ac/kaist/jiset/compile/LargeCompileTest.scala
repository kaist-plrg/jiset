package kr.ac.kaist.jiset

import java.io._
import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.algorithm.Algorithm._
import kr.ac.kaist.jiset.algorithm._
import kr.ac.kaist.jiset.util.Useful._
import org.scalatest._

class LargeCompileTest extends CompileTest {
  // tag name
  override val tag = "largeCompile"

  // change extension from .json to .ir
  val json2ir = changeExt("json", "ir")

  // registration
  def init: Unit = {
    for (file <- walkTree(LARGE_DIR)) {
      val filename = file.getName
      if (jsonFilter(filename)) {
        val jsonName = file.toString
        val irName = json2ir(jsonName)

        val steps = readJson[List[Step]](jsonName)
        val tokens = Step.toTokens(steps)
        val inst = Parser.parseInst(readFile(irName))

        check(tag, filename, {
          diffTest(filename, GeneralAlgoCompiler(tokens), inst)
        })
      }
    }
  }

  init
}
