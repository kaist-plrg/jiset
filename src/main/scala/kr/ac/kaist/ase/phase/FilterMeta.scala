package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase._
import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.parser.{ MetaParser, MetaData }
import kr.ac.kaist.ase.util.Useful._
import java.io._
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
import TestConfigJsonProtocol._

// FilterMeta phase
case object FilterMeta extends PhaseObj[Unit, FilterMetaConfig, Unit] {
  val name = "extract-meta"
  val help = "Extracts metadata"

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: FilterMetaConfig
  ): Unit = {

    val test262Dir = s"$TEST_DIR/test262"
    val dir = new File(test262Dir)
    val (normalL, errorL) = walkTree(dir).toList.filter(
      (file) => jsFilter(file.getName)
    ).map((x) => MetaParser(x.toString, dir.toString)).filter((x) => filter(x)).map {
        case MetaData(name, n, _, i, _, _) => Test262Config(name, n, i)
      }.partition(_.negative.isEmpty)
    val summary = Test262ConfigSummary(
      normalL.map((x) => NormalTestConfig(x.name, x.includes)),
      errorL.collect { case Test262Config(name, Some(n), in) => ErrorTestConfig(name, n, in) }
    )
    val pw = new PrintWriter(new File(s"$TEST_DIR/test262.json"))
    pw.println(summary.toJson.prettyPrint)
    pw.close()
  }

  def filter(meta: MetaData) = !(
    (meta.flags contains "onlyStrict") ||
    (meta.flags contains "raw") ||
    (meta.flags contains "module") ||
    (meta.flags contains "CanBlockIsFalse") ||
    (meta.flags contains "CanBlockIsTrue")
  ) &&
    (meta.locales.isEmpty) &&
    ((meta.name contains "language/") ||
      (meta.name contains "built-ins/"))
  def defaultConfig: FilterMetaConfig = FilterMetaConfig()
  val options: List[PhaseOption[FilterMetaConfig]] = List()
}

// FilterMeta phase config
case class FilterMetaConfig() extends Config

