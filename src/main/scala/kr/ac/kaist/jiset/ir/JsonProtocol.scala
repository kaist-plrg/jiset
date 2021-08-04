package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.ir.Parser._
import kr.ac.kaist.jiset.util.BasicJsonProtocol
import kr.ac.kaist.jiset.util.Useful.beautify
import io.circe._, io.circe.syntax._

object JsonProtocol extends BasicJsonProtocol {
  val beautifier = new Beautifier(line = true, asite = true)
  import beautifier._

  implicit val (tyDecoder: Decoder[Ty], tyEncoder: Encoder[Ty]) =
    stringCodec[Ty](Ty.apply, beautify)
  implicit val (refDecoder: Decoder[Ref], refEncoder: Encoder[Ref]) =
    stringCodec[Ref](Ref.apply, beautify)
  implicit val (exprDecoder: Decoder[Expr], exprEncoder: Encoder[Expr]) =
    stringCodec[Expr](Expr.apply, beautify)
  implicit val (instDecoder: Decoder[Inst], instEncoder: Encoder[Inst]) =
    stringCodec[Inst](Inst.apply, beautify)
}
