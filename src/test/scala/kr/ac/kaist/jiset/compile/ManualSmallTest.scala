package kr.ac.kaist.jiset.compile

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.parser.algorithm._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.spec.Region
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._
import org.scalatest._

class ManualSmallTest extends CompileTest {
  val name: String = "compileManualTest"

  implicit val (lines, grammar, document, region): (Array[String], Grammar, Document, Region) = {
    implicit val ((lines, document, region), _) = getSpec("recent")
    (lines, ECMAScriptParser.parseGrammar, document, region)
  }

  val secIds = ECMAScriptParser.parseHeads()._1

  def test(desc: String, target: CompileTarget)(cases: (String, String)*) = check(desc, {
    cases.zipWithIndex.foreach {
      case ((spec, answer), i) => {
        val code = unescapeHtml(spec).split(LINE_SEP).toList
        val resultInst = target.parse(code, secIds)._2.getOrElse(fail(s"`$spec` cannot be parsed."))
        val answerInst = target.parseIR(answer)
        difftest(s"$desc#$i", resultInst, answerInst, false)
      }
    }
  })

  // registration
  def init: Unit = {
    test("Basic Return Statement", InstsTarget)(
      "Return _value_" -> "return value"
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
        [! __x0__]
      } else {}""",
      "Optionally, set _F_.[[InitialName]] to _name_." ->
        """if randBool F.InitialName = name else {}"""
    )

    test("Return `value` if `cond`", InstsTarget)(
      "Return `1n` if _prim_ is *true*" -> """
        if (= prim true) return 1n
        else return undefined
      """,
      "Return `1n` if _prim_ is *true* and `0n` if _prim_ is *false*." -> """
        if (= prim true) return 1n
        else if (= prim false) return 0n
        else return undefined
      """,
      "Return *true* if _x_ and _y_ are the same Object value. Otherwise, return *false*." -> """
        if (= x y) return true
        else return false
      """
    )

    test("Internal Method Condition", InstsTarget)(
      """If _p_.[[GetPrototypeOf]] is not the ordinary object internal method defined in <emu-xref href="#sec-ordinary-object-internal-methods-and-internal-slots-getprototypeof"></emu-xref>, set _done_ to *true*.""" -> """if (! (= p.GetPrototypeOf OrdinaryObjectDOTGetPrototypeOf)) done = true else {}"""
    )

    test("Newly Created Object", InstsTarget)(
      """Let _obj_ be a newly created object with an internal slot for each name in _internalSlotsList_.""" -> """{
        let obj = (new OrdinaryObject("SubMap" -> (new SubMap())))
        let __x0__ = internalSlotsList
        let __x1__ = 0i
        while (< __x1__ __x0__.length) {
          let __x2__ = __x0__[__x1__]
          obj[__x2__] = undefined
          __x1__ = (+ __x1__ 1i)
        }
      }"""
    )

    test("Complex Condition Operation", CondTarget)(
      "_a_ = 0, _b_ = 10, and _c_ = 100" -> "(&& (&& (== a 0i) (== b 10i)) (== c 100i))",
      "_a_ &ne; 0 or _b_ = 0 and  _c_ &ne; 0 or _d_ &ne; 0" ->
        "(|| (! (== a 0i)) (&& (== b 0i) (|| (! (== c 0i)) (! (== d 0i)))))"
    )

    test("ReturnIfAbrupt", ExprTarget)(
      "? _x_" -> "[? x]",
      "! _x_" -> "[! x]",
    )
  }
  init
}
