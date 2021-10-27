package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.{ LOG_DIR, EDITOR_LOG_DIR, LINE_SEP }
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir.Logger
import kr.ac.kaist.jiset.ir.JsonProtocol._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.{ UIdGen, UId }
import scala.math.log
import io.circe._, io.circe.syntax._, io.circe.parser._

// filtering JavaScript programs using a given syntactic view
object JSFilter {
  def apply(ast: AST, view: SyntacticView): Boolean =
    ast.contains(view.ast)
}
