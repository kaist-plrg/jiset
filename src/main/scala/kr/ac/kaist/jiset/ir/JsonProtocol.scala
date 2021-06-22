package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.ir.Parser._
import kr.ac.kaist.jiset.util.BasicJsonProtocol
import kr.ac.kaist.jiset.util.Useful.beautify
import spray.json._

object JsonProtocol extends BasicJsonProtocol {
  val beautifier = new Beautifier(index = true, asite = true)
  import beautifier._

  implicit lazy val TyFormat = stringFormat[Ty](Ty.apply, beautify)
  implicit lazy val RefFormat = stringFormat[Ref](Ref.apply, beautify)
  implicit lazy val ExprFormat = stringFormat[Expr](Expr.apply, beautify)
  implicit lazy val InstFormat = stringFormat[Inst](Inst.apply, beautify)
}
