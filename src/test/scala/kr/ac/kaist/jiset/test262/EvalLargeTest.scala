package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.Test262
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._

class EvalLargeTest extends Test262Test {
  val name: String = "test262EvalTest"

  // logging with view information
  LOG = true
  VIEW = true
  BUGTRIGGER = optional(readFile(s"$BASE_DIR/tests/.bugtrigger").trim)
  BUGTRIGGER match {
    case Some(str) if exists(s"$VERSION_DIR/bugtrigger/$str/algo") =>
      println(s"'$str' is successfully triggered.")
    case Some(str) => println(s"'$str' is an invalid bug name. No bugs are triggered.")
    case None => println("Since the .bugtrigger file does not exist, no bugs are triggered.")
  }

  // registration
  def init: Unit = check(name, {
    test262EvalTest(Test262.config.normal, "eval")
  })
  init
}
