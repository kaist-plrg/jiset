package kr.ac.kaist.jiset.phase

import java.io.File
import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.algorithm.JsonProtocol._
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
    // change extension from .json to .ir
    val json2ir = changeExt("json", "ir")

    for (file <- walkTree(LARGE_DIR)) {
      val filename = file.getName
      if (jsonFilter(filename)) {
        val jsonName = file.toString
        val steps = readJson[List[Step]](jsonName)
        val tokens = Step.toTokens(steps)
        val inst = GeneralAlgoCompiler(tokens)

        val irName = json2ir(jsonName)
        dumpFile(beautify(inst), irName)
      }
    }
  }

  def defaultConfig: GenTestConfig = GenTestConfig()
  val options: List[PhaseOption[GenTestConfig]] = Nil
}

// GenTest phase config
case class GenTestConfig() extends Config
