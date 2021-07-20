package kr.ac.kaist.jiset.analyzer

import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._
import io.circe.syntax._

object JsonProtocol {
  //////////////////////////////////////////////////////////////////////////////
  // AbsSemantics
  //////////////////////////////////////////////////////////////////////////////
  implicit lazy val AbsSemanticsDecoder: Decoder[AbsSemantics] =
    new Decoder[AbsSemantics] {
      final def apply(c: HCursor): Decoder.Result[AbsSemantics] = Right(null)
    }
  implicit lazy val AbsSemanticsEncoder: Encoder[AbsSemantics] =
    new Encoder[AbsSemantics] {
      final def apply(result: AbsSemantics): Json = Json.fromString("")
    }
}
