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

    test("String Value")(
      """return &laquo; *"\*default\*"* &raquo;""" -> """{
        app __x0__ = (WrapCompletion (new ["*default*"]))
        return __x0__
      }""",
      """return *42*""" -> """{
        app __x0__ = (WrapCompletion 42i)
        return __x0__
      }""",
      """return *"A B C D"*""" -> """{
        app __x0__ = (WrapCompletion "A B C D")
        return __x0__
      }"""
    )

    test("Optionally, ~")(
      "Optionally, perform ! HostEnqueueFinalizationRegistryCleanupJob(_fg_)." -> """if randBool {
        app __x0__ = (HostEnqueueFinalizationRegistryCleanupJob fg)
        if (is-completion __x0__) if (= __x0__.Type CONST_normal) __x0__ = __x0__.Value else return __x0__ else {}
        __x0__
      } else {}""",
      "Optionally, set _F_.[[InitialName]] to _name_." ->
        """if (! (= F.InitialName absent)) if randBool F.InitialName = name else {} else {}"""
    )

    test("Return `value` if `cond`")(
      "Return `1n` if _prim_ is *true*" -> """if (= prim true) {
        app __x0__ = (WrapCompletion 1n)
        return __x0__
      } else {
        app __x1__ = (NormalCompletion undefined)
        return __x1__
      }""",
      "Return `1n` if _prim_ is *true* and `0n` if _prim_ is *false*." -> """if (= prim true) {
        app __x0__ = (WrapCompletion 1n)
        return __x0__
      } else if (= prim false) {
        app __x1__ = (WrapCompletion 0n)
        return __x1__
      } else {
        app __x2__ = (NormalCompletion undefined)
        return __x2__
      }""",
      "Return *true* if _x_ and _y_ are the same Object value. Otherwise, return *false*." -> """if (= x y) {
        app __x0__ = (WrapCompletion true)
        return __x0__
      } else {
        app __x1__ = (WrapCompletion false)
        return __x1__
      }"""
    )
  }
  init
}
