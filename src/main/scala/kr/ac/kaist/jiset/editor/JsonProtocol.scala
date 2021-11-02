package kr.ac.kaist.jiset.editor

import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._
import io.circe.syntax._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.util.BasicJsonProtocol
import kr.ac.kaist.jiset.util.Useful.stringify

object JsonProtocol extends BasicJsonProtocol {
  implicit lazy val JsProgramDecoder: Decoder[JsProgram] =
    new Decoder[JsProgram] {
      final def apply(c: HCursor): Decoder.Result[JsProgram] = {
        val obj = c.value.asObject.get.toMap
        val uid = obj("uid").asNumber.get.toInt.get
        val execTime = obj("execTime").asNumber.get.toInt.get
        val script = Script(obj("script"))
        val nids =
          obj("touched").asArray.get.toArray.map(_.asNumber.get.toInt.get)
        val touched = Array.fill(cfg.nodes.size)(false)
        for { nid <- nids } { touched(nid) = true }
        Right(JsProgram(script, touched, execTime).setUId(uid))
      }
    }
  implicit lazy val JsProgramEncoder: Encoder[JsProgram] =
    new Encoder[JsProgram] {
      final def apply(p: JsProgram) = Json.obj(
        "uid" -> p.uid.asJson,
        "execTime" -> p.execTime.asJson,
        "script" -> p.script.toJson,
        "touched" -> p.touchedNIds.asJson
      )
    }
}
