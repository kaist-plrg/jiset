package kr.ac.kaist.jiset.compile

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.parser.algorithm.{ Compiler, TokenParser }
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec.algorithm.Diff
import org.scalatest._

trait CompileTest extends JISETTest {
  val category: String = "compile"

  def difftest(filename: String, result: IRNode, answer: IRNode): Unit =
    Diff(result, answer) match {
      case Some(Diff.Missing(missing)) =>
        println(s"==================================================")
        println(s"[$filename] MISS: ${beautify(missing)}")
        println(s"--------------------------------------------------")
        val answerStr = beautify(answer)
        val resultStr = beautify(result)
        println(s"- result: $resultStr")
        println(s"- answer: $answerStr")
        fail(s"$answerStr is different with $resultStr")
      case None =>
    }
}
