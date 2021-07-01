package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.JSTest
import kr.ac.kaist.jiset.util.Useful._

trait Test262Test extends JSTest {
  override val category: String = "test262"

  // directory name
  val logDir = s"$LOG_DIR/$dateStr"
}
