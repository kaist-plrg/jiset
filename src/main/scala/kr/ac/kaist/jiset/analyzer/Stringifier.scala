package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.domain._
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
    case comp: View => ViewApp(app, comp)
    case comp: AbsRefValue => AbsRefValueApp(app, comp)
  }

  // control points
  implicit lazy val ControlPointApp: App[ControlPoint] = (app, cp) => cp match {
    case NodePoint(node, view) => app >> view >> ":" >> node
    case ReturnPoint(func, view) => app >> view >> ":RET:" >> func.uidString
  }

  // asts
  implicit val AstApp: App[AST] = (app, ast) => {
    app >> "☊[" >> ast.span.toString >> "]"
  }

  // views
  implicit lazy val ViewApp: App[View] = (app, view) => {
    // js views
    view.jsViewOpt.map {
      case JSView(ast, calls) =>
        implicit lazy val l = ListApp[AST]("call(", ", ", ")")
        app >> ast >> ":" >> calls
    }

    // ir contexts
    if (detail) {
      app >> view.irCtxts.map {
        case CallCtxt(call) => call.uidString.mkString
        case LoopCtxt(loop, depth) => s"${loop.uidString}($depth)"
      }.mkString("[", ", ", "]")
    } else {
      app >> "[call: " >> view.calls.length
      app >> ", loop: " >> view.loops.length >> "]"
    }
  }

  // abstract reference values
  implicit lazy val AbsRefValueApp: App[AbsRefValue] = (app, ref) => ref match {
    case AbsRefId(id) => app >> id
    case AbsRefProp(base, prop) => app >> base >> "[" >> prop >> "]"
  }
}
