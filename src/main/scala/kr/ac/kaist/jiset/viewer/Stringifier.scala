package kr.ac.kaist.jiset.viewer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// Viewer Stringifier
class Stringifier(
  detail: Boolean = true,
  line: Boolean = false,
  asite: Boolean = false
) {
  // Viewer elements
  implicit lazy val ViewerElemApp: App[ViewerElem] = (app, elem) => elem match {
    case view: SyntacticView => SyntacticViewApp(app, view)
  }

  // TODO syntactic views
  implicit lazy val SyntacticViewApp: App[SyntacticView] = (app, view) => {
    ???
  }
}
