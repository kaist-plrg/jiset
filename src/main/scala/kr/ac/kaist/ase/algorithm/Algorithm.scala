package kr.ac.kaist.ase.algorithm

import kr.ac.kaist.ase.LINE_SEP
import kr.ac.kaist.ase.parser
import kr.ac.kaist.ase.util.Useful.readFile
import spray.json._

// algorithms
case class Algorithm(
    length: Int,
    params: List[String],
    kind: AlgoKind,
    steps: List[Step],
    filename: String
) {
  def getSteps(init: List[Step]): List[Step] = (init /: steps) {
    case (list, step) => step.getSteps(list)
  }

  private def getInfo = {
    var k = 0
    def next: Next = { val res = Next(k); k += 1; res }
    def T(tokens: List[Token], token: Token): List[Token] = token match {
      case StepList(steps) => Out :: ((In :: tokens) /: steps)(S(_, _))
      case t => t :: tokens
    }
    def S(tokens: List[Token], step: Step): List[Token] =
      next :: (tokens /: step.tokens)(T(_, _))
    ((List[Token]() /: steps)(S(_, _)).reverse, k)
  }
  def toTokenList = getInfo._1
  def lineCount = getInfo._2

  override def toString: String = {
    val sb = new StringBuilder
    sb.append(s"filename: ").append(filename).append(LINE_SEP)
    sb.append(s"params: ").append(params.mkString(", ")).append(LINE_SEP)
    sb.append(LINE_SEP)
    sb.append(Token.getString(toTokenList))
    sb.toString
  }
}
object Algorithm extends DefaultJsonProtocol {
  implicit object TokenFormat extends RootJsonFormat[Token] {
    override def read(json: JsValue): Token = json match {
      case JsString(text) => Text(text)
      case v =>
        val discrimator = List("const", "value", "id", "steps")
          .map(d => json.asJsObject.fields.contains(d))
        discrimator.indexOf(true) match {
          case 0 => ConstFormat.read(v)
          case 1 => ValueFormat.read(v)
          case 2 => IdFormat.read(v)
          case 3 => StepListFormat.read(v)
          case _ => deserializationError(s"unknown Token: $v")
        }
    }
    override def write(token: Token): JsValue = token match {
      case (t: Const) => ConstFormat.write(t)
      case (t: Value) => ValueFormat.write(t)
      case (t: Id) => IdFormat.write(t)
      case (t: StepList) => StepListFormat.write(t)
      case Text(text) => JsString(text)
    }
  }
  implicit object AlgoKindFormat extends RootJsonFormat[AlgoKind] {
    override def read(json: JsValue): AlgoKind = json match {
      case JsString("RuntimeSemantics") => RuntimeSemantics
      case JsString("StaticSemantics") => StaticSemantics
      case JsString("Method") => Method
      case v => deserializationError(s"unknown AlgoKind: $v")
    }
    override def write(kind: AlgoKind): JsValue = kind match {
      case RuntimeSemantics => JsString("RuntimeSemantics")
      case StaticSemantics => JsString("StaticSemantics")
      case Method => JsString("Method")
    }
  }
  implicit lazy val StepFormat = jsonFormat1(Step)
  implicit lazy val ConstFormat = jsonFormat1(Const)
  implicit lazy val ValueFormat = jsonFormat1(Value)
  implicit lazy val IdFormat = jsonFormat1(Id)
  implicit lazy val StepListFormat = jsonFormat1(StepList)
  implicit lazy val AlgorithmFormat = jsonFormat5(Algorithm.apply)

  def apply(filename: String): Algorithm = {
    readFile(filename).parseJson.convertTo[Algorithm]
  }
}
