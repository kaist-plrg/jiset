package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// Editor Stringifier
class Stringifier(
  detail: Boolean = true,
  line: Boolean = false,
  asite: Boolean = false
) {
  // Editor elements
  implicit lazy val EditorElemApp: App[EditorElem] = (app, elem) => elem match {
    case view: SyntacticView => SyntacticViewApp(app, view)
  }

  // TODO syntactic views
  implicit lazy val SyntacticViewApp: App[SyntacticView] = (app, view) => {
    ???
  }
}
