package kr.ac.kaist.jiset.util

import spray.json._

trait BasicJsonProtocol extends DefaultJsonProtocol {
  // general enumerations
  def enumFormat[T <: Enumeration](enu: T): RootJsonFormat[T#Value] =
    new RootJsonFormat[T#Value] {
      def write(obj: T#Value): JsValue = JsString(obj.toString)
      def read(json: JsValue): T#Value = {
        json match {
          case JsString(txt) => enu.withName(txt)
          case v => deserializationError(s"Expected a value from enum $enu instead of $v")
        }
      }
    }

  // protocol based on parsers and beautifiers
  def stringFormat[T](
    parser: String => T,
    beautifier: T => String
  ): JsonFormat[T] = new RootJsonFormat[T] {
    def write(x: T): JsValue = JsString(beautifier(x))
    def read(json: JsValue): T = json match {
      case JsString(str) => parser(str)
      case v => deserializationError(s"Expected a string instead of $v")
    }
  }
}
