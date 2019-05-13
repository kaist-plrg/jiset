package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.error.NoFileError
import kr.ac.kaist.ase.node.algorithm._
import kr.ac.kaist.ase.util.Useful.fileReader
import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.util._
import scala.io.Source

// AlgoParse phase
case object AlgoParse extends PhaseObj[Unit, AlgoParseConfig, List[Algorithm]] {
  val name = "algo-parse"
  val help = "Parses algorithm files."

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: AlgoParseConfig
  ): List[Algorithm] = aseConfig.fileNames match {
    case Nil => throw NoFileError("parse")
    case filename :: _ =>
      val algos = Algorithm.getList(fileReader(filename))

      if (config.showFailed) {
        val steps = (List[Step]() /: algos) { case (list, algo) => algo.getSteps(list) }
        val failedSteps = steps.filter(_ match {
          case RawStep(_) => true
          case _ => false
        })
        failedSteps.map(_.shortBeautify).sorted.foreach(println _)
        val total = steps.length
        val fail = failedSteps.length
        val succ = total - fail
        println(s"$succ/$total")
      }

      algos
  }

  def defaultConfig: AlgoParseConfig = AlgoParseConfig()
  val options: List[PhaseOption[AlgoParseConfig]] = List(
    ("showFailed", BoolOption(c => c.showFailed = true),
      "show failed steps.")
  )
}

// AlgoParse phase config
case class AlgoParseConfig(
  var showFailed: Boolean = false
) extends Config
