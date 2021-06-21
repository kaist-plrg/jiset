package kr.ac.kaist.jiset.parse

import kr.ac.kaist.jiset.JISETTest
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.extractor.ECMAScriptParser
import kr.ac.kaist.jiset.spec.JsonProtocol._
import spray.json._
import org.scalatest._
import kr.ac.kaist.jiset.spec.algorithm.Diff

class JsonSmallTest extends ParseTest {
  val name: String = "parseJsonTest"

  // registration
  def init: Unit = {
    check("ECMAScript (recent)", {
      val spec = JISETTest.spec
      val json = spec.toJson
      val loaded = json.convertTo[ECMAScript]
      val diff = new Diff
      diff.deep = true
      assert(spec == loaded)
      (spec.algos zip loaded.algos).foreach {
        case (l, r) => {
          assert(diff.compare(l.rawBody, r.rawBody))
        }
      }
    })
  }
  init
}
