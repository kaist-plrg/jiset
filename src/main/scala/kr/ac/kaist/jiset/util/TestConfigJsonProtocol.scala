package kr.ac.kaist.jiset.util

import kr.ac.kaist.jiset.parser.{ MetaParser, MetaData }
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

case class TestList(list: List[MetaData]) {
  def length: Int = list.length
  def remove(desc: String, f: MetaData => Boolean): TestList = {
    val (filtered, removed) = list.foldLeft(List[MetaData](), 0) {
      case ((l, count), meta) =>
        if (f(meta)) (l, count + 1)
        else (meta :: l, count)
    }
    println(f"$desc%-30s: $removed tests are removed")
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
