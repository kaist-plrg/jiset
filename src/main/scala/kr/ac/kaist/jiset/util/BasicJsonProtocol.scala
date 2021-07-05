package kr.ac.kaist.jiset.util

import io.circe._, io.circe.syntax._

trait BasicJsonProtocol {
  // JSON format based on parsers and beautifiers
  def stringCodec[T](
    parser: String => T,
    beautifier: T => String
  ): (Encoder[T], Decoder[T]) = {
    val decoder = new Decoder[T] {
      final def apply(c: HCursor): Decoder.Result[T] = c.value.asString match {
        case None => Left(
          DecodingFailure(s"Expected a string instead of ${c.value}", c.history)
        )
        case Some(str) => Right(parser(str))
      }
    }
    val encoder = new Encoder[T] {
      final def apply(x: T): Json = Json.fromString(beautifier(x))
    }
    (encoder, decoder)
  }
}
