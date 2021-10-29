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
    case jsprog: JsProgram => JsProgramApp(app, jsprog)
  }

  // JsProgram
  implicit lazy val JsProgramApp: App[JsProgram] = (app, elem) => {
    app >> "[uid: " >> elem.uid >> "]" >> elem.name
  }

  // TODO syntactic views
  implicit lazy val SyntacticViewApp: App[SyntacticView] = (app, view) => {
    ???
  }
}
