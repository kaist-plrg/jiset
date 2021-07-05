package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.ir.Parser._
import kr.ac.kaist.jiset.util.BasicJsonProtocol
import kr.ac.kaist.jiset.util.Useful.beautify
import io.circe._, io.circe.syntax._

object JsonProtocol extends BasicJsonProtocol {
  val beautifier = new Beautifier(index = true, asite = true)
  import beautifier._

  implicit val (tyEncoder: Encoder[Ty], tyDecoder: Decoder[Ty]) =
    stringCodec[Ty](Ty.apply, beautify)
  implicit val (refEncoder: Encoder[Ref], refDecoder: Decoder[Ref]) =
    stringCodec[Ref](Ref.apply, beautify)
  implicit val (exprEncoder: Encoder[Expr], exprDecoder: Decoder[Expr]) =
    stringCodec[Expr](Expr.apply, beautify)
  implicit val (instEncoder: Encoder[Inst], instDecoder: Decoder[Inst]) =
    stringCodec[Inst](Inst.apply, beautify)
}
