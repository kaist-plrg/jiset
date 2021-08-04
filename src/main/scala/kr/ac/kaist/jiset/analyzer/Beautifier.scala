package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// Analyzer Beautifier
class Beautifier(
  detail: Boolean = true,
  index: Boolean = false,
  asite: Boolean = false
) {
  val irBeautifier = new ir.Beautifier(detail, index, asite)
  import irBeautifier._

  // analyzer  components
  implicit lazy val AnalyzerElemApp: App[AnalyzerElem] = (app, comp) => comp match {
    case comp: ControlPoint => ControlPointApp(app, comp)
    case comp: View => ViewApp(app, comp)
  }

  // control points
  implicit lazy val ControlPointApp: App[ControlPoint] = (app, cp) => cp match {
    case NodePoint(node, view) => app >> node.toString >> ":" >> view
    case ReturnPoint(func, view) => app >> "RETURN:" >> view
  }

  // views
  implicit lazy val ViewApp: App[View] = (app, view) => view match {
    case BaseView => app >> "I"
  }
}
