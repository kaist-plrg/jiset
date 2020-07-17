package kr.ac.kaist.jiset.phase

import java.io.File
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.generator._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import spray.json._

// GenTestModule phase
case object GenTestModule extends PhaseObj[Unit, GenTestModuleConfig, Unit] {
  val name: String = "gen-test-module"
  val help: String = "generates modules for n+1 version testing."

  def apply(
    non: Unit,
    jisetConfig: JISETConfig,
    config: GenTestModuleConfig
  ): Unit = {
    val packageName = config.packageName.getOrElse("kr.ac.kaist.ires")
    val modelDir = config.out.getOrElse("../ires/src/main/scala/kr/ac/kaist/ires/model")
    val resourceDir = "../ires/src/main/resources"
    val spec = Spec(s"$RESOURCE_DIR/$VERSION/auto/spec.json")
    val grammar = spec.grammar
    TestGenerator(packageName, modelDir, resourceDir, grammar)
  }

  def defaultConfig: GenTestModuleConfig = GenTestModuleConfig()
  val options: List[PhaseOption[GenTestModuleConfig]] = List(
    ("packageName", StrOption((c, s) => c.packageName = Some(s)),
      "set the base package name for models (default: kr.ac.kaist.ires)."),
    ("out", StrOption((c, s) => c.out = Some(s)),
      "set the output directory (default: ../ires/src/main/scala/kr/ac/kaist/ires/model).")
  )
}

// GenTestModule phase config
case class GenTestModuleConfig(
    var packageName: Option[String] = None,
    var out: Option[String] = None
) extends Config
