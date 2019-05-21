package kr.ac.kaist.ase.spec

import kr.ac.kaist.ase.util.Useful.readFile
import spray.json._

// ECMASCript specifications
case class Spec(
  globalMethods: List[String],
  grammar: Grammar,
  tys: List[Ty]
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
  implicit val TyFormat = jsonFormat2(Ty)
  implicit val TerminalFormat = jsonFormat1(Terminal)
  implicit val NonTerminalFormat = jsonFormat3(NonTerminal)
  implicit val ButNotFormat = jsonFormat2(ButNot)
  implicit val LookaheadFormat = jsonFormat2(Lookahead)
  implicit val UnicodeFormat = jsonFormat1(Unicode)
  implicit val RhsFormat = jsonFormat3(Rhs)
  implicit val LhsFormat = jsonFormat2(Lhs)
  implicit val ProductionFormat = jsonFormat2(Production)
  implicit val GrammarFormat = jsonFormat2(Grammar)
  implicit val SpecFormat = jsonFormat3(Spec.apply)

  def apply(filename: String): Spec = {
    readFile(filename).parseJson.convertTo[Spec]
  }
}
