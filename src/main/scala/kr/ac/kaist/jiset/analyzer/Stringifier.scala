package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.cfg
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// Analyzer Stringifier
class Stringifier(
  detail: Boolean = true,
  line: Boolean = false,
  asite: Boolean = false
) {
  // load other stringifiers
  val cfgStringifier = new cfg.Stringifier(detail, line, asite)
  import cfgStringifier._, irStringifier._

  // analyzer components
  implicit lazy val AnalyzerElemApp: App[AnalyzerElem] = (app, comp) => comp match {
    case comp: ControlPoint => ControlPointApp(app, comp)
    case comp: View => ViewApp(app, comp)
  }

  // control points
  implicit lazy val ControlPointApp: App[ControlPoint] = (app, cp) => cp match {
    case NodePoint(node, view) => app >> view >> ":" >> node
    case ReturnPoint(func, view) => app >> "RETURN:" >> view
  }

  // views
  implicit lazy val ViewApp: App[View] = (app, view) => view match {
    case BaseView => app >> "I"
  }
}
