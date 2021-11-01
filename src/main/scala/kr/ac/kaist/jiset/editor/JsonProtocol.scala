package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.util.BasicJsonProtocol
import kr.ac.kaist.jiset.util.Useful.stringify
import kr.ac.kaist.jiset.js.ast.Script
import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._
import io.circe.syntax._

object JsonProtocol extends BasicJsonProtocol {
  implicit lazy val JsProgramDecoder = new Decoder[JsProgram] {
    final def apply(c: HCursor): Decoder.Result[JsProgram] = {
      val obj = c.value.asObject.get.toMap
      val uid = obj("uid").asNumber.get.toInt.get
      val script = Script(obj("script"))
      val touchedFile = obj("touchedFile").asString
      Right(JsProgram(uid, script, touchedFile))
    }
  }
  implicit lazy val JsProgramEncoder: Encoder[JsProgram] = new Encoder[JsProgram] {
    final def apply(p: JsProgram) = Json.obj(
      "uid" -> p.uid.asJson,
      "script" -> p.getScript.toJson,
      "touchedFile" -> p.touchedFile.asJson
    )
  }
}
