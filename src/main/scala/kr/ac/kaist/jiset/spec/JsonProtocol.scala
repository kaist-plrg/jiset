package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.ir.JsonProtocol._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.algorithm.token._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.spec.grammar.token.{ Token => GToken, _ }
import kr.ac.kaist.jiset.util.BasicJsonProtocol
import spray.json._

object JsonProtocol extends BasicJsonProtocol {
  //////////////////////////////////////////////////////////////////////////////
  // Grammar
  //////////////////////////////////////////////////////////////////////////////
  // grammar tokens
  implicit object GTokenFormat extends RootJsonFormat[GToken] {
    override def read(json: JsValue): GToken = json match {
      case (v: JsString) => CharacterFormat.read(v)
      case v =>
        val discrimator = List(
          "term", "name", "base", "contains", "empty", "nlt"
        ).map(d => json.asJsObject.fields.contains(d))
        discrimator.indexOf(true) match {
          case 0 => TerminalFormat.read(v)
          case 1 => NonTerminalFormat.read(v)
          case 2 => ButNotFormat.read(v)
          case 3 => LookaheadFormat.read(v)
          case 4 => EmptyToken
          case 5 => NoLineTerminatorToken
          case _ => deserializationError(s"unknown Token: $v")
        }
    }

    override def write(token: GToken): JsValue = token match {
      case (t: Terminal) => TerminalFormat.write(t)
      case (t: NonTerminal) => NonTerminalFormat.write(t)
      case (t: ButNot) => ButNotFormat.write(t)
      case (t: Lookahead) => LookaheadFormat.write(t)
      case (t: Character) => CharacterFormat.write(t)
      case EmptyToken => JsObject("empty" -> JsNull)
      case NoLineTerminatorToken => JsObject("nlt" -> JsNull)
    }
  }

  implicit lazy val TerminalFormat = jsonFormat1(Terminal.apply)
  implicit lazy val NonTerminalFormat = jsonFormat3(NonTerminal.apply)
  implicit lazy val ButNotFormat = jsonFormat2(ButNot.apply)
  implicit lazy val LookaheadFormat = jsonFormat2(Lookahead.apply)

  implicit object CharacterFormat extends RootJsonFormat[Character] {
    override def read(json: JsValue): Character = json match {
      case JsString(name) => Character.nameMap.getOrElse(name, Unicode(name))
      case _ => deserializationError(s"unknown Character: $json")
    }

    override def write(ch: Character): JsValue = JsString(ch.name)
  }

  implicit lazy val LhsFormat = jsonFormat2(Lhs.apply)
  implicit lazy val RhsCondFormat = jsonFormat2(RhsCond.apply)
  implicit lazy val RhsFormat = jsonFormat2(Rhs.apply)
  implicit lazy val ProductionFormat = jsonFormat2(Production.apply)
  implicit lazy val GrammarFormat = jsonFormat(Grammar.apply, "lexProds", "prods")

  //////////////////////////////////////////////////////////////////////////////
  // Algorithms
  //////////////////////////////////////////////////////////////////////////////
  // algorithms parameters
  implicit lazy val ParamKindFormat = enumFormat(Param.Kind)
  implicit lazy val ParamFormat = jsonFormat(Param.apply, "name", "kind")

  // algorithm heads
  implicit object HeadFormat extends RootJsonFormat[Head] {
    override def read(v: JsValue): Head = {
      val discrimator = List("ref", "base", "name", "prod")
        .map(d => v.asJsObject.fields.contains(d))
      discrimator.indexOf(true) match {
        case 0 => BuiltinHeadFormat.read(v)
        case 1 => MethodHeadFormat.read(v)
        case 2 => NormalHeadFormat.read(v)
        case 3 => SyntaxDirectedHeadFormat.read(v)
        case _ => deserializationError(s"unknown Token: $v")
      }
    }

    override def write(head: Head): JsValue = head match {
      case (h: BuiltinHead) => BuiltinHeadFormat.write(h)
      case (h: MethodHead) => MethodHeadFormat.write(h)
      case (h: NormalHead) => NormalHeadFormat.write(h)
      case (h: SyntaxDirectedHead) => SyntaxDirectedHeadFormat.write(h)
    }
  }
  implicit lazy val SyntaxDirectedHeadFormat = jsonFormat(
    SyntaxDirectedHead.apply,
    "prod", "idx", "subIdx", "rhs", "methodName", "withParams",
  )
  implicit lazy val NormalHeadFormat =
    jsonFormat(NormalHead.apply, "name", "params")
  implicit lazy val MethodHeadFormat =
    jsonFormat(MethodHead.apply, "base", "methodName", "receiverParam", "origParams")
  implicit lazy val BuiltinHeadFormat =
    jsonFormat(BuiltinHead.apply, "ref", "origParams")
  implicit lazy val AlgoFormat =
    jsonFormat(Algo.apply, "head", "ids", "rawBody", "code")

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

  //////////////////////////////////////////////////////////////////////////////
  // ECMAScript
  //////////////////////////////////////////////////////////////////////////////
  implicit lazy val ECMAScriptFormat = jsonFormat(
    ECMAScript.apply,
    "grammar", "algos", "consts", "intrinsics", "symbols", "aoids", "section"
  )
}
