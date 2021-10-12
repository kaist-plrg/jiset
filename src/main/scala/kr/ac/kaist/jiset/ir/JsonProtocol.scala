package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.ir.Parser._
import kr.ac.kaist.jiset.util.BasicJsonProtocol
import kr.ac.kaist.jiset.util.Useful.stringify
import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._
import io.circe.syntax._

object JsonProtocol extends BasicJsonProtocol {
  val stringifier = new Stringifier(line = true, asite = true)
  import stringifier._

  implicit val (tyDecoder: Decoder[Ty], tyEncoder: Encoder[Ty]) =
    stringCodec[Ty](Ty.apply, stringify)
  implicit val (refDecoder: Decoder[Ref], refEncoder: Encoder[Ref]) =
    stringCodec[Ref](Ref.apply, stringify)
  implicit val (exprDecoder: Decoder[Expr], exprEncoder: Encoder[Expr]) =
    stringCodec[Expr](Expr.apply, stringify)
  implicit val (instDecoder: Decoder[Inst], instEncoder: Encoder[Inst]) =
    stringCodec[Inst](Inst.apply, stringify)

  // debugger breakpoint
  implicit lazy val AlgoBreakpointDecoder: Decoder[AlgoBreakpoint] = deriveDecoder
  implicit lazy val AlgoBreakpointEncoder: Encoder[AlgoBreakpoint] = deriveEncoder
  implicit lazy val JSBreakpointDecoder: Decoder[JSBreakpoint] = deriveDecoder
  implicit lazy val JSBreakpointEncoder: Encoder[JSBreakpoint] = deriveEncoder
  implicit lazy val BreakpointDecoder: Decoder[Breakpoint] = new Decoder[Breakpoint] {
    final def apply(c: HCursor): Decoder.Result[Breakpoint] = {
      val obj = c.value.asObject.get
      val discrimator = List("name", "line").map(obj.contains(_))
      discrimator.indexOf(true) match {
        case 0 => AlgoBreakpointDecoder(c)
        case 1 => JSBreakpointDecoder(c)
        case _ => decodeFail(s"unknown Breakpoint: $obj", c)
      }
    }
  }
  implicit lazy val BreakpointEncoder: Encoder[Breakpoint] = Encoder.instance {
    case bp: AlgoBreakpoint => bp.asJson
    case bp: JSBreakpoint => bp.asJson
  }
}
