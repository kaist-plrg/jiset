package kr.ac.kaist.jiset.js

import scala.scalajs.js.annotation.JSExportTopLevel
import kr.ac.kaist.jiset.js.ast._

object Export {
  @JSExportTopLevel("parseJS")
  def parseJS(str: String): Script =
    Parser.parse(Parser.Script(Nil), str).get
}
