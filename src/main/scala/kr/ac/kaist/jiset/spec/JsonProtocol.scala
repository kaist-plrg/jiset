package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.spec.algorithm._
import spray.json._

object JsonProtocol extends DefaultJsonProtocol {
  // sections
  implicit lazy val SectionFormat: JsonFormat[Section] = lazyFormat(jsonFormat2(Section.apply))

  // tokens
  implicit object TokenFormat extends RootJsonFormat[Token] {
    override def read(json: JsValue): Token = json match {
      case JsString(text) => Text(text)
      case v =>
        val discrimator = List(
          "const", "code", "value", "id", "nt", "sup",
          "link", "grammar", "sub", "k", "in", "out"
        ).map(d => json.asJsObject.fields.contains(d))
        discrimator.indexOf(true) match {
          case 0 => ConstFormat.read(v)
          case 1 => CodeFormat.read(v)
          case 2 => ValueFormat.read(v)
          case 3 => IdFormat.read(v)
          case 4 => NtFormat.read(v)
          case 5 => SupFormat.read(v)
          case 6 => LinkFormat.read(v)
          case 7 => GrFormat.read(v)
          case 8 => SubFormat.read(v)
          case 9 => NextFormat.read(v)
          case 10 => In
          case 11 => Out
          case _ => deserializationError(s"unknown Token: $v")
        }
    }

    override def write(token: Token): JsValue = token match {
      case (t: Const) => ConstFormat.write(t)
      case (t: Code) => CodeFormat.write(t)
      case (t: Value) => ValueFormat.write(t)
      case (t: Id) => IdFormat.write(t)
      case (t: Nt) => NtFormat.write(t)
      case (t: Sup) => SupFormat.write(t)
      case (t: Link) => LinkFormat.write(t)
      case (t: Gr) => GrFormat.write(t)
      case (t: Sub) => SubFormat.write(t)
      case (t: Next) => NextFormat.write(t)
      case In => JsObject("in" -> JsNull)
      case Out => JsObject("out" -> JsNull)
      case Text(text) => JsString(text)
    }
  }

  implicit lazy val ConstFormat = jsonFormat1(Const)
  implicit lazy val CodeFormat = jsonFormat1(Code)
  implicit lazy val ValueFormat = jsonFormat1(Value)
  implicit lazy val IdFormat = jsonFormat1(Id)
  implicit lazy val NtFormat = jsonFormat1(Nt)
  implicit lazy val SupFormat = jsonFormat1(Sup)
  implicit lazy val LinkFormat = jsonFormat1(Link)
  implicit lazy val GrFormat = jsonFormat2(Gr)
  implicit lazy val SubFormat = jsonFormat1(Sub)
  implicit lazy val NextFormat = jsonFormat1(Next)
}
