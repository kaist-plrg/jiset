package kr.ac.kaist.jiset.util

import kr.ac.kaist.jiset.DEBUG
import kr.ac.kaist.jiset.parser.{ MetaParser, MetaData }
import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._

case class Test262Config(name: String, negative: Option[String], includes: List[String])

case class NormalTestConfig(name: String, includes: List[String])

case class ErrorTestConfig(name: String, errorName: String, includes: List[String])

case class Test262ConfigSummary(normal: List[NormalTestConfig], error: List[ErrorTestConfig])

object TestConfigJsonProtocol {
  implicit val normalTestConfigDecoder: Decoder[NormalTestConfig] = deriveDecoder
  implicit val normalTestConfigEncoder: Encoder[NormalTestConfig] = deriveEncoder
  implicit val errorTestConfigDecoder: Decoder[ErrorTestConfig] = deriveDecoder
  implicit val errorTestConfigEncoder: Encoder[ErrorTestConfig] = deriveEncoder
  implicit val test262ConfigDecoder: Decoder[Test262Config] = deriveDecoder
  implicit val test262ConfigEncoder: Encoder[Test262Config] = deriveEncoder
  implicit val test262ConfigSummaryDecoder: Decoder[Test262ConfigSummary] = deriveDecoder
  implicit val test262ConfigSummaryEncoder: Encoder[Test262ConfigSummary] = deriveEncoder
}

case class TestList(list: List[MetaData]) {
  def length: Int = list.length
  def remove(desc: String, f: MetaData => Boolean): TestList = {
    val (filtered, removed) = list.foldLeft(List[MetaData](), 0) {
      case ((l, count), meta) =>
        if (f(meta)) (l, count + 1)
        else (meta :: l, count)
    }
    if (DEBUG) println(f"$desc%-30s: $removed tests are removed")
    TestList(filtered.reverse)
  }
  def getSummary: Test262ConfigSummary = {
    val (normalL, errorL) = list.map {
      case MetaData(name, n, _, i, _, _) => Test262Config(name, n, i)
    }.partition(_.negative.isEmpty)
    Test262ConfigSummary(
      normalL.map(x => NormalTestConfig(x.name, x.includes)),
      errorL.collect { case Test262Config(name, Some(n), in) => ErrorTestConfig(name, n, in) }
    )
  }
}
