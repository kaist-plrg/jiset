package kr.ac.kaist.jiset.phase

import java.io.File
import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.algorithm._
import kr.ac.kaist.jiset.algorithm.Algorithm._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import spray.json._

// GenTest phase
case object GenTest extends PhaseObj[Unit, GenTestConfig, Unit] {

  val name: String = "gen-test"
  val help: String = "generates tests."

  def apply(
    non: Unit,
    jisetConfig: JISETConfig,
    config: GenTestConfig
  ): Unit = {
    val spec = Spec(s"$RESOURCE_DIR/$VERSION/auto/spec.json")
    val packageName = "kr.ac.kaist.ires"
    val methods = spec.globalMethods
    methods.foreach(name => {
      val scalaName = getScalaName(name)
      val algo = Algorithm(s"$RESOURCE_DIR/$VERSION/auto/algorithm/$name.json")
      algo.kind = RuntimeSemantics
      val (func, _) = GeneralAlgoCompiler(name, algo).result

      // steps
      val steps = algo.steps
      dumpJson(steps, s"$LARGE_DIR/$name.json")

      // inst
      val inst = func.body
      dumpFile(beautify(inst), s"$LARGE_DIR/$name.ir")
    })
  }

  def defaultConfig: GenTestConfig = GenTestConfig()
  val options: List[PhaseOption[GenTestConfig]] = Nil
}

// GenTest phase config
case class GenTestConfig() extends Config
