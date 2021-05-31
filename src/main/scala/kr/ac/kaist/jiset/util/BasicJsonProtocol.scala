package kr.ac.kaist.jiset.util

import spray.json._

trait BasicJsonProtocol extends DefaultJsonProtocol {
  // JSON format for enumerations
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

  // JSON format based on parsers and beautifiers
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

  // JSON format for pairs
  def pairFormat[T, U](
    implicit
    tFormat: JsonFormat[T],
    uFormat: JsonFormat[U]
  ): JsonFormat[(T, U)] = new RootJsonFormat[(T, U)] {
    def write(pair: (T, U)): JsValue = {
      val (l, r) = pair
      JsArray(tFormat.write(l), uFormat.write(r))
    }
    def read(json: JsValue): (T, U) = json match {
      case JsArray(v) if v.length == 2 => (tFormat.read(v(0)), uFormat.read(v(1)))
      case v => deserializationError(s"Expected a 2-length array intead of $v")
    }
  }
}
