package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.util.BasicJsonProtocol
import kr.ac.kaist.jiset.util.Useful.stringify
import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._
import io.circe.syntax._

object JsonProtocol extends BasicJsonProtocol {
  val stringifier = new Stringifier(line = true, asite = true)
  import stringifier._

  // JsProgram
  implicit val (jsProgDecoder: Decoder[JsProgram], jsProgEncoder: Encoder[JsProgram]) =
    stringCodec[JsProgram](JsProgramParser.apply, stringify)
}
