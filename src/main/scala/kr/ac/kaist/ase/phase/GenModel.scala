package kr.ac.kaist.ase.phase

import java.io.File
import kr.ac.kaist.ase._
import kr.ac.kaist.ase.generator._
import kr.ac.kaist.ase.core
import kr.ac.kaist.ase.spec._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.util._
import scala.io.Source
import spray.json._

// GenModel phase
case object GenModel extends PhaseObj[Unit, GenModelConfig, Unit] {

  val name: String = "gen-model"
  val help: String = "generates ECMAScript models."

  def apply(
    non: Unit,
    aseConfig: ASEConfig,
    config: GenModelConfig
  ): Unit = {
    import SpecProtocol._
    val version = getFirstFilename(aseConfig, "gen-model")
    val json = readFile(s"$RESOURCE_DIR/$version/spec.json")
    val spec = json.parseJson.convertTo[Spec]
    GlobalGenerator(version, spec)
  }

  def defaultConfig: GenModelConfig = GenModelConfig()
  val options: List[PhaseOption[GenModelConfig]] = List()
}

// GenModel phase config
case class GenModelConfig() extends Config
