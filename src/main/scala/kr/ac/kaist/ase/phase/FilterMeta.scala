package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase._
import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.parser.{ MetaParser, MetaData }
import kr.ac.kaist.ase.util.Useful._
import java.io._

// FilterMeta phase
case object FilterMeta extends PhaseObj[Unit, FilterMetaConfig, (List[Test262Config], List[Test262Config])] {
  val name = "extract-meta"
  val help = "Extracts metadata"

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: FilterMetaConfig
  ): (List[Test262Config], List[Test262Config]) = {

    val test262Dir = s"$TEST_DIR/test262"
    val dir = new File(test262Dir)
    walkTree(dir).toList.filter(
      (file) => jsFilter(file.getName)
    ).map((x) => MetaParser(x.toString)).filter((x) => filter(x)).map {
        case MetaData(name, n, _, i, _, _) => Test262Config(name, n, i)
      }.partition(_.negative.isEmpty)
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

case class Test262Config(name: String, negative: Option[String], includes: List[String])
