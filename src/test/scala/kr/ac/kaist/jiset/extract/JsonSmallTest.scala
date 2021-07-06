package kr.ac.kaist.jiset.extract

import kr.ac.kaist.jiset.JISETTest
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.extractor.ECMAScriptParser
import kr.ac.kaist.jiset.spec.JsonProtocol._
import org.scalatest._
import kr.ac.kaist.jiset.spec.algorithm.Diff
import io.circe._, io.circe.syntax._

class JsonSmallTest extends ExtractTest {
  val name: String = "extractJsonTest"

  // registration
  def init: Unit = {
    check("ECMAScript (recent)", {
      val spec = JISETTest.spec
      val json = spec.asJson
      for (loaded <- json.as[ECMAScript]) {
        val diff = new Diff
        diff.deep = true
        assert(spec == loaded)
        (spec.algos zip loaded.algos).foreach {
          case (l, r) => {
            assert(diff.compare(l.rawBody, r.rawBody))
          }
        }
      }
    })
  }
  init
}
