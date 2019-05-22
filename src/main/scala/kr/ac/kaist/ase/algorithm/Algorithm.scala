package kr.ac.kaist.ase.algorithm

import kr.ac.kaist.ase.LINE_SEP
import kr.ac.kaist.ase.error.UnexpectedToken
import kr.ac.kaist.ase.parser
import kr.ac.kaist.ase.util.Useful.readFile
import spray.json._
// algorithms
case class Algorithm(params: List[String], steps: List[Step], filename: String) {
  def getSteps(init: List[Step]): List[Step] = (init /: steps) {
    case (list, step) => step.getSteps(list)
  }

  def toTokenList: List[Token] = {
    def T(tokens: List[Token], token: Token): List[Token] = token match {
      case StepList(steps) => Out :: ((In :: tokens) /: steps)(S(_, _))
      case t => t :: tokens
    }
    def S(tokens: List[Token], step: Step): List[Token] =
      Next :: (tokens /: step.tokens)(T(_, _))
    (List[Token]() /: steps)(S(_, _)).reverse
  }

  override def toString: String = {
    val sb = new StringBuilder
    var TAB = 2
    var indent = 0
    def newline: Unit = sb.append(LINE_SEP).append(" " * indent)
    def t(token: Token): Unit = token match {
      case Value(value) => sb.append("value:").append(value).append(" ")
      case Id(id) => sb.append("id:").append(id).append(" ")
      case Text(text) => sb.append(text).append(" ")
      case Next => newline
      case In =>
        indent += TAB; newline
      case _ => throw UnexpectedToken(token)
    }
    def ts(tokens: List[Token]): Unit = tokens match {
      case Next :: Out :: Next :: rest =>
        indent -= TAB; newline; ts(rest)
      case v :: rest =>
        t(v); ts(rest)
      case Nil =>
    }
    sb.append(s"$filename:")
    newline
    sb.append(s"(${params.mkString(", ")}) =>")
    indent += TAB; newline
    ts(toTokenList)
    sb.toString
  }
}
object Algorithm extends DefaultJsonProtocol {
  implicit object TokenFormat extends RootJsonFormat[Token] {
    override def read(json: JsValue): Token = json match {
      case JsString(text) => Text(text)
      case v =>
        val discrimator = List("value", "id", "steps")
          .map(d => json.asJsObject.fields.contains(d))
        discrimator.indexOf(true) match {
          case 0 => ValueFormat.read(v)
          case 1 => IdFormat.read(v)
          case 2 => StepListFormat.read(v)
          case _ => deserializationError(s"unknown Token: $v")
        }
    }
    override def write(token: Token): JsValue = token match {
      case (t: Value) => ValueFormat.write(t)
      case (t: Id) => IdFormat.write(t)
      case (t: StepList) => StepListFormat.write(t)
      case Text(text) => JsString(text)
    }
  }
  implicit lazy val StepFormat = jsonFormat1(Step)
  implicit lazy val ValueFormat = jsonFormat1(Value)
  implicit lazy val IdFormat = jsonFormat1(Id)
  implicit lazy val StepListFormat = jsonFormat1(StepList)
  implicit lazy val AlgorithmFormat = jsonFormat3(Algorithm.apply)

  def apply(filename: String): Algorithm = {
    readFile(filename).parseJson.convertTo[Algorithm]
  }
}
