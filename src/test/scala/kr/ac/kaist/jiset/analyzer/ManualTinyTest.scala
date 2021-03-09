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
  val name: String = "analyzerManualTest"

  val answerMap: Map[(String, View), (AbsHeap, AbsValue)] = Map(
    ("PrimaryExpression[0,0].IsIdentifierRef", View(List(AstT("PrimaryExpression"))))
      -> ((AbsHeap.Bot, AbsComp(CompNormal -> (AF: AbsPure, emptyConst))))
  )

  def getFunctionByName(cfg: CFG, fname: String): Option[Function] =
    cfg.algo2fid.get(fname).flatMap(uid => cfg.fidMap.get(uid))

  def getString(h: AbsHeap, v: AbsValue): String = beautify(v) + (
    if (h.isBottom) ""
    else s" @ ${beautify(h)}"
  )

  def init: Unit = {
    // analyze results in sem: AbsSemantics
    val spec = getSpec("recent")
    val cfg = new CFG(spec)
    val sem = new AbsSemantics(cfg)
    val transfer = new AbsTransfer(sem, false)
    transfer.compute

    // find testcases
    for (((fname, view), (ansH, ansV)) <- answerMap) check(fname, {
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

