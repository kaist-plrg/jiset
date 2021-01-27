package kr.ac.kaist.jiset

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.parser.algorithm.{ Compiler, TokenParser }
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.util.Useful._
import org.scalatest._
import kr.ac.kaist.jiset.spec.algorithm.Diff
import kr.ac.kaist.jiset.parser.ECMAScriptParser

abstract class CompileTest extends JISETTest {
  def difftest(filename: String, result: Inst, answer: Inst): Unit =
    Diff(result, answer) match {
      case Some(Diff.Missing(missing)) =>
        println(s"==================================================")
        println(s"[$filename] MISS: ${beautify(missing)}")
        println(s"--------------------------------------------------")
        val answerStr = beautify(answer)
        val resultStr = beautify(result)
        println(s"- answer: $answerStr")
        println(s"- result: $resultStr")
        fail(s"$answerStr is different with $resultStr")
      case None =>
    }
}
