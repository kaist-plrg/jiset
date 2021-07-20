package kr.ac.kaist.jiset.analyzer

import io.circe._, io.circe.syntax._
import kr.ac.kaist.jiset.analyzer.JsonProtocol._
import kr.ac.kaist.jiset.cfg.CFG
import kr.ac.kaist.jiset.util.Useful.{ time => showTime }
import kr.ac.kaist.jiset.{ JISETTest, VERSION }
import org.scalatest._

class JsonMiddleTest extends AnalyzerTest {
  val name: String = "analyzerJsonTest"

  // registration
  def init: Unit = check(VERSION, {
    val spec = JISETTest.spec
    val (_, cfg) = showTime("build CFG", {
      new CFG(spec)
    })
    showTime("perform type analysis", {
      performTypeAnalysis(cfg)
    })
    val (_, json) = showTime("encode abstract semantics in a JSON format", {
      sem.asJson
    })
    val (_, newSem) = showTime("decode abstract semantics in a JSON format", {
      json.as[AbsSemantics].getOrElse(AbsSemantics())
    })
    // TODO assert(sem == newSem)
  })
  init
}
