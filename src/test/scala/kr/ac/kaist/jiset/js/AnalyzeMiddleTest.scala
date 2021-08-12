package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset.JS_DIR
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.phase._
import kr.ac.kaist.jiset.util.JvmUseful._

class AnalyzeMiddleTest extends JSTest {
  val name: String = "jsAnalyzeTest"

  // registration
  def init: Unit = for (file <- walkTree(JS_DIR)) {
    val filename = file.getName
    if (jsFilter(filename)) check(filename, {
      val name = removedExt(filename)
      // analyze a JS file
      val jsName = file.toString
      analyzeFile(jsName)
    })
  }
  init
}
