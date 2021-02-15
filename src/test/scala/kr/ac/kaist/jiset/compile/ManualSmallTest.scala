package kr.ac.kaist.jiset.compile

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.parser.algorithm._
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

  def test(desc: String, target: CompileTarget)(cases: (String, String)*) = check(desc, {
    cases.zipWithIndex.foreach {
      case ((spec, answer), i) => {
        val code = unescapeHtml(spec).split(LINE_SEP).toList
        val resultInst = target.parse(code)._2.getOrElse(fail(s"`$spec` cannot be parsed."))
        val answerInst = target.parseIR(answer)
        difftest(s"$desc#$i", resultInst, answerInst)
      }
    }
  })

  // registration
  def init: Unit = {
    test("Basic Return Statement", InstsTarget)(
      """Return _value_""" -> """{
        app __x0__ = (WrapCompletion value)
        return __x0__
      }"""
    )

    test("Intrinsics", ExprTarget)(
      """%Object%""" -> """INTRINSIC_Object""",
      """%Object.prototype%""" -> """INTRINSIC_Object_prototype""",
      """%Object.prototype.prototype%""" -> """INTRINSIC_Object_prototype_prototype"""
    )

    test("String Value", ExprTarget)(
      """&laquo; *"\*default\*"* &raquo;""" -> """(new ["*default*"])""",
      """*42*""" -> """42i""",
      """*"A B C D"*""" -> """"A B C D""""
    )

    test("Optionally, ~", InstsTarget)(
      "Optionally, perform ! HostEnqueueFinalizationRegistryCleanupJob(_fg_)." -> """if randBool {
        app __x0__ = (HostEnqueueFinalizationRegistryCleanupJob fg)
        if (is-completion __x0__) if (= __x0__.Type CONST_normal) __x0__ = __x0__.Value else return __x0__ else {}
        __x0__
      } else {}""",
      "Optionally, set _F_.[[InitialName]] to _name_." ->
        """if randBool F.InitialName = name else {}"""
    )

    test("Return `value` if `cond`", InstsTarget)(
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
