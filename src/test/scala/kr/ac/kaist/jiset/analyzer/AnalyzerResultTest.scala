package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.JISETTest
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.cfg.{ CFG, Function }
import kr.ac.kaist.jiset.util.Useful.{ error }
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.ir._

class AnalyzerResultTest extends AnalyzerTest {
  val answerMap: Map[(String, View), (Option[AbsHeap], Option[AbsValue])] = Map(
    ("ObjectEnvironmentRecord.HasThisBinding", View(List(NullT))) -> (None, Some(AbsPrim(Null)))
  )

  def compareAbsResult(result: (AbsHeap, AbsValue), answer: (Option[AbsHeap], Option[AbsValue])): Boolean = answer match {
    case (Some(h), None) => result._1 == h
    case (None, Some(v)) => result._2 == v
    case (Some(h), Some(v)) => result._1 == h && result._2 == v
    case (None, None) => true
  }

  def getFunctionByName(cfg: CFG, fname: String): Option[Function] =
    cfg.algo2fid.get(fname).flatMap(uid => cfg.fidMap.get(uid))

  def init: Unit = {
    val pre = JISETTest.specInputs("recent")
    val spec: ECMAScript = ECMAScriptParser(pre, "", false, false)
    val cfg = new CFG(spec)
    val sem = new AbsSemantics(cfg)
    val transfer = new AbsTransfer(sem, false)
    transfer.compute
    // analyze results in sem: AbsSemantics

    // find testcases
    check("Compare analyze results", for (((fname, view), answer) <- answerMap) {
      val func = getFunctionByName(cfg, fname).getOrElse(error("Answer Function not found"))
      val rp = ReturnPoint(func, view)
      val result = sem(rp)
      val comparison = compareAbsResult(result, answer)
      if (!comparison) {
        println(s"FAILED: Function $fname, View $view")
        assert(comparison)
      }
    })
  }
}

