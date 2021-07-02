package kr.ac.kaist.jiset.js

import scala.scalajs.js.annotation.JSExportTopLevel

object Export {
  @JSExportTopLevel("parseJS")
  def parseJS(str: String): Unit = {
    val ast = Parser.parse(Parser.Script(Nil), str).get
    println(ast)
  }
}
