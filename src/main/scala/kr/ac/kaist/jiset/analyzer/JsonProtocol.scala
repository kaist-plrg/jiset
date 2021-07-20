package kr.ac.kaist.jiset.analyzer

import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._
import io.circe.syntax._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir.JsonProtocol._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.BasicJsonProtocol

object JsonProtocol extends BasicJsonProtocol {
  import cfg.jsonProtocol._

  implicit lazy val AbsSemanticsDecoder: Decoder[AbsSemantics] = deriveDecoder
  implicit lazy val AbsSemanticsEncoder: Encoder[AbsSemantics] = deriveEncoder

  implicit lazy val ControlPointDecoder: Decoder[ControlPoint] = new Decoder[ControlPoint] {
    final def apply(c: HCursor): Decoder.Result[ControlPoint] = {
      val obj = c.value.asObject.get
      val discrimator = List("node", "func").map(obj.contains(_))
      discrimator.indexOf(true) match {
        case 0 => NodePointDecoder(NodeDecoder)(c)
        case 1 => ReturnPointDecoder(c)
        case _ => decodeFail(s"invalid control point: $obj", c)
      }
    }
  }
  implicit lazy val ControlPointEncoder: Encoder[ControlPoint] = Encoder.instance {
    case t: NodePoint[_] => NodePointEncoder(NodeEncoder)(t)
    case t: ReturnPoint => ReturnPointEncoder(t)
  }

  implicit def NodePointDecoder[T <: Node](
    implicit
    TDecoder: Decoder[T]
  ): Decoder[NodePoint[T]] = deriveDecoder
  implicit def NodePointEncoder[T <: Node](
    implicit
    TEncoder: Encoder[T]
  ): Encoder[NodePoint[T]] = deriveEncoder

  implicit lazy val ReturnPointDecoder: Decoder[ReturnPoint] = deriveDecoder
  implicit lazy val ReturnPointEncoder: Encoder[ReturnPoint] = deriveEncoder

  implicit lazy val ViewDecoder: Decoder[View] = deriveDecoder
  implicit lazy val ViewEncoder: Encoder[View] = deriveEncoder

  implicit lazy val AbsTypeDecoder: Decoder[AbsType] = deriveDecoder
  implicit lazy val AbsTypeEncoder: Encoder[AbsType] = deriveEncoder

  implicit lazy val TypeDecoder: Decoder[Type] = deriveDecoder
  implicit lazy val TypeEncoder: Encoder[Type] = deriveEncoder

  implicit lazy val AbsStateDecoder: Decoder[AbsState] = deriveDecoder
  implicit lazy val AbsStateEncoder: Encoder[AbsState] = deriveEncoder
}
