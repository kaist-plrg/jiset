package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Useful.readFile
import spray.json._

// ECMASCript specifications
case class Spec(
    globalMethods: List[String],
    consts: List[String],
    grammar: Grammar,
    symbols: Map[String, String],
    intrinsics: Map[String, String],
    tys: Map[String, Map[String, String]]
)
object Spec extends DefaultJsonProtocol {
  implicit object TokenFormat extends RootJsonFormat[Token] {
    override def read(json: JsValue): Token = json match {
      case JsString("EmptyToken") => EmptyToken
      case JsString("NoLineTerminatorToken") => NoLineTerminatorToken
      case JsString("UnicodeAny") => UnicodeAny
      case JsString("UnicodeIdStart") => UnicodeIdStart
      case JsString("UnicodeIdContinue") => UnicodeIdContinue
      case v =>
        val discrimator = List("term", "name", "base", "contains", "code")
          .map(d => json.asJsObject.fields.contains(d))
        discrimator.indexOf(true) match {
          case 0 => TerminalFormat.read(v)
          case 1 => NonTerminalFormat.read(v)
          case 2 => ButNotFormat.read(v)
          case 3 => LookaheadFormat.read(v)
          case 4 => UnicodeFormat.read(v)
          case _ => deserializationError(s"unknown Token: $v")
        }
    }
    override def write(token: Token): JsValue = token match {
      case (t: Terminal) => TerminalFormat.write(t)
      case (t: NonTerminal) => NonTerminalFormat.write(t)
      case (t: ButNot) => ButNotFormat.write(t)
      case (t: Lookahead) => LookaheadFormat.write(t)
      case (t: Unicode) => UnicodeFormat.write(t)
      case _ => JsString(token.toString)
    }
  }
  implicit val TerminalFormat = jsonFormat1(Terminal.apply)
  implicit val NonTerminalFormat = jsonFormat3(NonTerminal.apply)
  implicit val ButNotFormat = jsonFormat2(ButNot.apply)
  implicit val LookaheadFormat = jsonFormat2(Lookahead.apply)
  implicit val UnicodeFormat = jsonFormat1(Unicode.apply)
  implicit val RhsCondFormat = jsonFormat2(RhsCond.apply)
  implicit val RhsFormat = jsonFormat2(Rhs.apply)
  implicit val LhsFormat = jsonFormat2(Lhs.apply)
  implicit val ProductionFormat = jsonFormat2(Production.apply)
  implicit val GrammarFormat = jsonFormat2(Grammar.apply)
  implicit val SpecFormat = jsonFormat6(Spec.apply)

  def apply(filename: String): Spec = readFile(filename).parseJson.convertTo[Spec]
}
