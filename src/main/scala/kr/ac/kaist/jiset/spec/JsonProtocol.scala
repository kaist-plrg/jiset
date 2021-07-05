package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.ir.JsonProtocol._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.algorithm.token._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.spec.grammar.token.{ Token => GToken, _ }
import cats.syntax.functor._
import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._
import io.circe.syntax._

object JsonProtocol {
  //////////////////////////////////////////////////////////////////////////////
  // Grammar
  //////////////////////////////////////////////////////////////////////////////
  // grammar tokens
  implicit lazy val TerminalDecoder: Decoder[Terminal] = deriveDecoder
  implicit lazy val TerminalEncoder: Encoder[Terminal] = deriveEncoder
  implicit lazy val NonTerminalDecoder: Decoder[NonTerminal] = deriveDecoder
  implicit lazy val NonTerminalEncoder: Encoder[NonTerminal] = deriveEncoder
  implicit lazy val ButNotDecoder: Decoder[ButNot] = deriveDecoder
  implicit lazy val ButNotEncoder: Encoder[ButNot] = deriveEncoder
  implicit lazy val LookaheadDecoder: Decoder[Lookahead] = deriveDecoder
  implicit lazy val LookaheadEncoder: Encoder[Lookahead] = deriveEncoder
  implicit lazy val CharacterDecoder: Decoder[Character] = new Decoder[Character] {
    final def apply(c: HCursor): Decoder.Result[Character] = c.value.asString match {
      case None => Left(DecodingFailure(s"unknown Character: ${c.value}", c.history))
      case Some(name) => Right(Character.nameMap.getOrElse(name, Unicode(name)))
    }
  }
  implicit lazy val CharacterEncoder: Encoder[Character] = new Encoder[Character] {
    final def apply(ch: Character): Json = Json.fromString(ch.name)
  }
  implicit val GTokenDecoder: Decoder[GToken] = new Decoder[GToken] {
    final def apply(c: HCursor): Decoder.Result[GToken] = c.value.asString match {
      case Some(_) => CharacterDecoder(c)
      case None =>
        val obj = c.value.asObject.get
        val discrimator = List(
          "term", "name", "base", "contains", "empty", "nlt"
        ).map(obj.contains(_))
        discrimator.indexOf(true) match {
          case 0 => TerminalDecoder(c)
          case 1 => NonTerminalDecoder(c)
          case 2 => ButNotDecoder(c)
          case 3 => LookaheadDecoder(c)
          case 4 => Right(EmptyToken)
          case 5 => Right(NoLineTerminatorToken)
          case _ => Left(DecodingFailure(s"unknown Token: $obj", c.history))
        }
    }
  }
  implicit val GTokenEncoder: Encoder[GToken] = Encoder.instance {
    case t: Terminal => t.asJson
    case t: NonTerminal => t.asJson
    case t: ButNot => t.asJson
    case t: Lookahead => t.asJson
    case t: Character => t.asJson
    case EmptyToken => Json.obj(("empty", Json.Null))
    case NoLineTerminatorToken => Json.obj(("nlt", Json.Null))
  }
  implicit lazy val LhsDecoder: Decoder[Lhs] = deriveDecoder
  implicit lazy val LhsEncoder: Encoder[Lhs] = deriveEncoder
  implicit lazy val RhsCondDecoder: Decoder[RhsCond] = deriveDecoder
  implicit lazy val RhsCondEncoder: Encoder[RhsCond] = deriveEncoder
  implicit lazy val RhsDecoder: Decoder[Rhs] = deriveDecoder
  implicit lazy val RhsEncoder: Encoder[Rhs] = deriveEncoder
  implicit lazy val ProductionDecoder: Decoder[Production] = deriveDecoder
  implicit lazy val ProductionEncoder: Encoder[Production] = deriveEncoder
  implicit lazy val GrammarDecoder: Decoder[Grammar] = deriveDecoder
  implicit lazy val GrammarEncoder: Encoder[Grammar] = deriveEncoder

  //////////////////////////////////////////////////////////////////////////////
  // Algorithms
  //////////////////////////////////////////////////////////////////////////////
  // algorithms parameters
  implicit lazy val ParamKindDecoder: Decoder[Param.Kind] = Decoder.decodeEnumeration(Param.Kind)
  implicit lazy val ParamKindEncoder: Encoder[Param.Kind] = Encoder.encodeEnumeration(Param.Kind)
  implicit lazy val ParamDecoder: Decoder[Param] = deriveDecoder
  implicit lazy val ParamEncoder: Encoder[Param] = deriveEncoder

  // algorithm heads
  implicit lazy val HeadDecoder: Decoder[Head] = List[Decoder[Head]](
    Decoder[BuiltinHead].widen,
    Decoder[MethodHead].widen,
    Decoder[NormalHead].widen,
    Decoder[SyntaxDirectedHead].widen
  ).reduceLeft(_ or _)
  implicit val HeadEncoder: Encoder[Head] = Encoder.instance {
    case h: NormalHead => h.asJson
    case h: MethodHead => h.asJson
    case h: SyntaxDirectedHead => h.asJson
    case h: BuiltinHead => h.asJson
  }
  implicit lazy val SyntaxDirectedHeadDecoder: Decoder[SyntaxDirectedHead] = deriveDecoder
  implicit lazy val SyntaxDirectedHeadEncoder: Encoder[SyntaxDirectedHead] = deriveEncoder
  implicit lazy val NormalHeadDecoder: Decoder[NormalHead] = deriveDecoder
  implicit lazy val NormalHeadEncoder: Encoder[NormalHead] = deriveEncoder
  implicit lazy val MethodHeadDecoder: Decoder[MethodHead] = deriveDecoder
  implicit lazy val MethodHeadEncoder: Encoder[MethodHead] = deriveEncoder
  implicit lazy val BuiltinHeadDecoder: Decoder[BuiltinHead] = deriveDecoder
  implicit lazy val BuiltinHeadEncoder: Encoder[BuiltinHead] = deriveEncoder

  // algo
  implicit lazy val AlgoDecoder: Decoder[Algo] = deriveDecoder
  implicit lazy val AlgoEncoder: Encoder[Algo] = deriveEncoder

  // sections
  implicit lazy val SectionDecoder: Decoder[Section] = deriveDecoder
  implicit lazy val SectionEncoder: Encoder[Section] = deriveEncoder

  // tokens
  implicit lazy val TokenDecoder: Decoder[Token] = new Decoder[Token] {
    final def apply(c: HCursor): Decoder.Result[Token] = c.value.asString match {
      case Some(text) => Right(Text(text))
      case None =>
        val obj = c.value.asObject.get
        val discrimator = List(
          "const", "code", "value", "id", "nt", "sup",
          "link", "grammar", "sub", "k", "in", "out"
        ).map(obj.contains(_))
        discrimator.indexOf(true) match {
          case 0 => ConstDecoder(c)
          case 1 => CodeDecoder(c)
          case 2 => ValueDecoder(c)
          case 3 => IdDecoder(c)
          case 4 => NtDecoder(c)
          case 5 => SupDecoder(c)
          case 6 => LinkDecoder(c)
          case 7 => GrDecoder(c)
          case 8 => SubDecoder(c)
          case 9 => NextDecoder(c)
          case 10 => Right(In)
          case 11 => Right(Out)
          case _ => Left(DecodingFailure(s"unknown Token: $obj", c.history))
        }
    }
  }
  implicit lazy val TokenEncoder: Encoder[Token] = Encoder.instance {
    case Text(text) => Json.fromString(text)
    case t: Const => t.asJson
    case t: Code => t.asJson
    case t: Value => t.asJson
    case t: Id => t.asJson
    case t: Nt => t.asJson
    case t: Sup => t.asJson
    case t: Link => t.asJson
    case t: Gr => t.asJson
    case t: Sub => t.asJson
    case t: Next => t.asJson
    case In => Json.obj(("in", Json.Null))
    case Out => Json.obj(("out", Json.Null))
  }
  implicit lazy val ConstDecoder: Decoder[Const] = deriveDecoder
  implicit lazy val ConstEncoder: Encoder[Const] = deriveEncoder
  implicit lazy val CodeDecoder: Decoder[Code] = deriveDecoder
  implicit lazy val CodeEncoder: Encoder[Code] = deriveEncoder
  implicit lazy val ValueDecoder: Decoder[Value] = deriveDecoder
  implicit lazy val ValueEncoder: Encoder[Value] = deriveEncoder
  implicit lazy val IdDecoder: Decoder[Id] = deriveDecoder
  implicit lazy val IdEncoder: Encoder[Id] = deriveEncoder
  implicit lazy val NtDecoder: Decoder[Nt] = deriveDecoder
  implicit lazy val NtEncoder: Encoder[Nt] = deriveEncoder
  implicit lazy val SupDecoder: Decoder[Sup] = deriveDecoder
  implicit lazy val SupEncoder: Encoder[Sup] = deriveEncoder
  implicit lazy val LinkDecoder: Decoder[Link] = deriveDecoder
  implicit lazy val LinkEncoder: Encoder[Link] = deriveEncoder
  implicit lazy val GrDecoder: Decoder[Gr] = deriveDecoder
  implicit lazy val GrEncoder: Encoder[Gr] = deriveEncoder
  implicit lazy val SubDecoder: Decoder[Sub] = deriveDecoder
  implicit lazy val SubEncoder: Encoder[Sub] = deriveEncoder
  implicit lazy val NextDecoder: Decoder[Next] = deriveDecoder
  implicit lazy val NextEncoder: Encoder[Next] = deriveEncoder

  //////////////////////////////////////////////////////////////////////////////
  // ECMAScript
  //////////////////////////////////////////////////////////////////////////////
  implicit lazy val ECMAScriptDecoder: Decoder[ECMAScript] = deriveDecoder
  implicit lazy val ECMAScriptEncoder: Encoder[ECMAScript] = deriveEncoder
}
