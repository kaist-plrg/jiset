package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.algorithm._
import kr.ac.kaist.ires.ir
import kr.ac.kaist.jiset.model.AlgoCompiler
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.{ LINE_SEP, JISETConfig }
import scala.io.Source

// CompileAlgo phase
case object CompileAlgo extends PhaseObj[Algorithm, CompileAlgoConfig, ir.Func] {
  val name = "compile-algo"
  val help = "Compiles algorithm files."

  def apply(
    algo: Algorithm,
    jisetConfig: JISETConfig,
    config: CompileAlgoConfig
  ): ir.Func = {
    val name = getScalaName(removedExt(getSimpleFilename(getFirstFilename(jisetConfig, "parse"))))
    val (func, failed) = AlgoCompiler(name, algo).result
    failed.toSeq.sortBy(_._1) foreach {
      case (k, tokens) => println(s"[FailedLine]:$k: ${Token.getString(tokens)}")
    }
    func
  }

  def defaultConfig: CompileAlgoConfig = CompileAlgoConfig()
  val options: List[PhaseOption[CompileAlgoConfig]] = List()
}

// CompileAlgo phase config
case class CompileAlgoConfig() extends Config
