package kr.ac.kaist.jiset.compile

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.parser.algorithm.{ Compiler, TokenParser }
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.util.Useful._
import org.scalatest._
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.spec.grammar.Grammar
import org.jsoup.nodes._

class ManualSmallTest extends CompileTest {
  implicit val (grammar, document): (Grammar, Document) = {
    implicit val (lines, document) = getInput("recent")
    (ECMAScriptParser.parseGrammar, document)
  }

  private def compile(spec: String): (List[Token], Inst) = {
    val code = unescapeHtml(spec).split(LINE_SEP)
    val tokens = TokenParser.getTokens(code)
    (tokens, Compiler(tokens))
  }

  def test(desc: String)(cases: (String, String)*) = check(desc, {
    cases.zipWithIndex.foreach {
      case ((spec, answer), i) => {
        val (_, resultInst) = compile(spec)
        val answerInst = Parser.parseInst(answer)
        difftest(s"$desc#$i", resultInst, answerInst)
      }
    }
  })

  // registration
  def init: Unit = {
    test("Basic Return Statement")(
      "Return ToNumber(_o_)" -> """{
        app __x0__ = (ToNumber o)
        app __x1__ = (WrapCompletion __x0__)
        return __x1__ 
      }"""
    )

    test("Intrinsics")(
      """Call ToString(%Object%)""" -> """{
        app __x0__ = (ToString INTRINSIC_Object)
        __x0__
      }""",
      """Call ToString(%Object.prototype%)""" -> """{
        app __x0__ = (ToString INTRINSIC_Object_prototype)
        __x0__
      }""",
      """Call ToString(%Object.prototype.prototype%)""" -> """{
        app __x0__ = (ToString INTRINSIC_Object_prototype_prototype)
        __x0__
      }"""
    )
  }
  init
}
