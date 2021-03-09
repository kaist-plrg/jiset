package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.JISETTest
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.cfg.{ CFG, Function }
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.ir._
import org.scalatest._

class ManualTinyTest extends AnalyzerTest {
  val answerMap: Map[(String, View), (AbsHeap, AbsValue)] = Map(
    ("PrimaryExpression[0,0].IsIdentifierRef", View(List(AstT("PrimaryExpression"))))
      -> ((AbsHeap.Bot, AbsComp(CompNormal -> (AF: AbsPure, emptyConst))))
  )

  def compareAbsResult(result: (AbsHeap, AbsValue), answer: (AbsHeap, AbsValue)): Boolean = {
    val (resH, resV) = result
    val (ansH, ansV) = answer
    resH === ansH && resV === ansV
  }

  def getFunctionByName(cfg: CFG, fname: String): Option[Function] =
    cfg.algo2fid.get(fname).flatMap(uid => cfg.fidMap.get(uid))

  def getString(h: AbsHeap, v: AbsValue): String = beautify(v) + (
    if (h.isBottom) ""
    else s" @ ${beautify(h)}"
  )

  def init: Unit = {
    val pre = JISETTest.specInputs("recent")
    val spec: ECMAScript = ECMAScriptParser(pre, "", false, false)
    val cfg = new CFG(spec)
    val sem = new AbsSemantics(cfg)
    val transfer = new AbsTransfer(sem, false)
    transfer.compute
    // analyze results in sem: AbsSemantics

    // find testcases
    check("Compare analyze results", for (((fname, view), (ansH, ansV)) <- answerMap) {
      val func = getFunctionByName(cfg, fname).getOrElse(error("Answer Function not found"))
      val rp = ReturnPoint(func, view)
      val (resH, resV) = sem(rp)
      val comparison = resH === ansH && resV === ansV
      if (!comparison) {
        println(s"FAILED: Function $fname, View $view")
        println(s"expected: ${getString(ansH, ansV)}")
        println(s"result: ${getString(resH, resV)}")
        assert(comparison)
      }
    })
  }
  init
}

