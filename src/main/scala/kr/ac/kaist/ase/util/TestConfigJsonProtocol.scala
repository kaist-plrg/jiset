package kr.ac.kaist.ase.util
import spray.json._

case class Test262Config(name: String, negative: Option[String], includes: List[String])

case class NormalTestConfig(name: String, includes: List[String])

case class ErrorTestConfig(name: String, errorName: String, includes: List[String])

case class Test262ConfigSummary(normal: List[NormalTestConfig], error: List[ErrorTestConfig])

object TestConfigJsonProtocol extends DefaultJsonProtocol {
  implicit val normalTestConfigFormat = jsonFormat2(NormalTestConfig)
  implicit val errorTestConfigFormat = jsonFormat3(ErrorTestConfig)
  implicit val test262ConfigFormat = jsonFormat2(Test262ConfigSummary)
}
