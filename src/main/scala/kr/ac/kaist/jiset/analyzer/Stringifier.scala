package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.cfg
import kr.ac.kaist.jiset.cfg._
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

  // views
  implicit lazy val ViewApp: App[View] = (app, view) => view match {
    case View(calls, loops) =>
      if (detail) {
        app >> "call" >> calls.map(_.uidString).mkString("[", ", ", "]")
        app >> ":"
        app >> "loop" >> loops
          .map { case (loop, k) => s"${loop.uidString}($k)" }
          .mkString("[", ", ", "]")
      } else {
        app >> "call[depth:" >> calls.length >> "]"
        app >> ":"
        app >> "loop" >> loops.map(_._2).mkString("[", ", ", "]")
      }
  }

  // abstract reference values
  implicit lazy val AbsRefValueApp: App[AbsRefValue] = (app, ref) => ref match {
    case AbsRefId(id) => app >> id
    case AbsRefProp(base, prop) => app >> base >> "[" >> prop >> "]"
  }
}
