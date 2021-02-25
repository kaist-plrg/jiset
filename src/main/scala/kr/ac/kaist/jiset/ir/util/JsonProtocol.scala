package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.ir.Parser._
import kr.ac.kaist.jiset.ir.Beautifier._
import kr.ac.kaist.jiset.util.BasicJsonProtocol
import spray.json._

object JsonProtocol extends BasicJsonProtocol {
  def beautifier[T <: IRNode](x: T): String = beautify(x, index = true, exprId = true)

  implicit lazy val TyFormat = stringFormat[Ty](parseTy, beautifier)
  implicit lazy val RefFormat = stringFormat[Ref](parseRef, beautifier)
  implicit lazy val ExprFormat = stringFormat[Expr](parseExpr, beautifier)
  implicit lazy val InstFormat = stringFormat[Inst](parseInst, beautifier)
}
