package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.util.Useful._
import org.jsoup._
import org.jsoup.nodes._

// region of spec.html
case class Region(envMethod: (Int, Int), builtin: Int)

object Region {
  val ENV_REC_METHOD = "sec-environment-records"
  val BUILTIN = "sec-ecmascript-standard-built-in-objects"
  def apply(document: Document): Region = {
    def getRangeHelper(id: String): (Int, Int) =
      getRange(document.getElementById(id)).get

    // get environment record internal method scope
    val envMethod = getRangeHelper(ENV_REC_METHOD)

    // get built-in libraries scope
    val (builtin, _) = getRangeHelper(BUILTIN)

    Region(envMethod, builtin)
  }
}
