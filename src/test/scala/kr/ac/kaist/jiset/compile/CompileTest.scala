package kr.ac.kaist.jiset.compile

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.parser.algorithm.{ Compiler, TokenParser }
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec.algorithm.Diff
import org.scalatest._

trait CompileTest extends JISETTest {
  val category: String = "compile"

  def difftest(filename: String, result: IRNode, answer: IRNode, deep: Boolean = false): Unit = {
    val diff = new Diff
    diff.deep = deep
    diff(result, answer) match {
      case Some(diff.Missing(missing)) =>
        println(s"==================================================")
        println(s"[$filename] MISS: ${missing.beautified}")
        println(s"--------------------------------------------------")
        val answerStr = answer.beautified(index = true, asite = true)
        val resultStr = result.beautified(index = true, asite = true)
        println(s"- result: $resultStr")
        println(s"- answer: $answerStr")
        fail(s"$answerStr is different with $resultStr")
      case None =>
    }
  }
}
