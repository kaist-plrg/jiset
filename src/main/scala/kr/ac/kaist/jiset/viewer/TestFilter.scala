package kr.ac.kaist.jiset.viewer

import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast._

// filtering JavaScript programs using a given syntactic view
object JSFilter {
  def apply(ast: AST, view: SyntacticView): Boolean =
    ast.contains(view.ast)
}
