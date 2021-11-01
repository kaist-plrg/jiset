package kr.ac.kaist.jiset.editor.analyzer

import kr.ac.kaist.jiset.editor.analyzer.domain._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.cfg
import kr.ac.kaist.jiset.cfg.{ Stringifier => _, _ }
import kr.ac.kaist.jiset.js.ast.AST
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
    case comp: AbsRefValue => AbsRefValueApp(app, comp)
  }

  // control points
  implicit lazy val ControlPointApp: App[ControlPoint] = (app, cp) => cp match {
    case NodePoint(node) => app >> node
    case ReturnPoint(func) => app >> "RET:" >> func.uidString
  }

  // abstract reference values
  implicit lazy val AbsRefValueApp: App[AbsRefValue] = (app, ref) => ref match {
    case AbsRefId(id) => app >> id
    case AbsRefProp(base, prop) => app >> base >> "[" >> prop >> "]"
  }
}
