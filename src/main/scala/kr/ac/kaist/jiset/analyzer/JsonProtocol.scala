package kr.ac.kaist.jiset.analyzer

import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._
import io.circe.syntax._

object JsonProtocol {
  //////////////////////////////////////////////////////////////////////////////
  // AnalysisResult
  //////////////////////////////////////////////////////////////////////////////
  implicit lazy val AnalysisResultDecoder: Decoder[AnalysisResult] =
    new Decoder[AnalysisResult] {
      final def apply(c: HCursor): Decoder.Result[AnalysisResult] = Right(null)
    }
  implicit lazy val AnalysisResultEncoder: Encoder[AnalysisResult] =
    new Encoder[AnalysisResult] {
      final def apply(result: AnalysisResult): Json = Json.fromString("")
    }
}
