package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg
import kr.ac.kaist.jiset.cfg.{ DotPrinter => _, _ }
import kr.ac.kaist.jiset.util.Appender

trait DotPrinter extends cfg.DotPrinter {
  val SELECTED = """"gray""""

  // normalize strings for view
  private val normPattern = """[-\[\](),\s~?"]""".r
  protected def norm(view: View): String = {
    normPattern.replaceAllIn(view.toString, "")
  }
}
