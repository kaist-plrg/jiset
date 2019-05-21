package kr.ac.kaist.ase.algorithm

import kr.ac.kaist.ase.util.Useful.readFile
import spray.json._

// algorithms
case class Algorithm(params: List[String], steps: List[Step], filename: String) {
  def getSteps(init: List[Step]): List[Step] = (init /: steps) {
    case (list, step) => step.getSteps(list)
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

