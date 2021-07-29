package kr.ac.kaist.jiset.cfg

import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._
import io.circe.syntax._
import kr.ac.kaist.jiset.ir.JsonProtocol._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.BasicJsonProtocol

class JsonProtocol(cfg: CFG) extends BasicJsonProtocol {
  implicit lazy val FunctionDecoder: Decoder[Function] = UIdDecoder(cfg.fidGen)
  implicit lazy val FunctionEncoder: Encoder[Function] = UIdEncoder

  implicit lazy val NodeDecoder: Decoder[Node] = UIdDecoder(cfg.nidGen)
  implicit lazy val NodeEncoder: Encoder[Node] = UIdEncoder

  implicit lazy val LinearDecoder: Decoder[Linear] = UIdDecoder(cfg.nidGen)
  implicit lazy val LinearEncoder: Encoder[Linear] = UIdEncoder

  implicit lazy val EntryDecoder: Decoder[Entry] = UIdDecoder(cfg.nidGen)
  implicit lazy val EntryEncoder: Encoder[Entry] = UIdEncoder

  implicit lazy val NormalDecoder: Decoder[Normal] = UIdDecoder(cfg.nidGen)
  implicit lazy val NormalEncoder: Encoder[Normal] = UIdEncoder

  implicit lazy val CallDecoder: Decoder[Call] = UIdDecoder(cfg.nidGen)
  implicit lazy val CallEncoder: Encoder[Call] = UIdEncoder

  implicit lazy val BranchDecoder: Decoder[Branch] = UIdDecoder(cfg.nidGen)
  implicit lazy val BranchEncoder: Encoder[Branch] = UIdEncoder

  implicit lazy val ExitDecoder: Decoder[Exit] = UIdDecoder(cfg.nidGen)
  implicit lazy val ExitEncoder: Encoder[Exit] = UIdEncoder
}
