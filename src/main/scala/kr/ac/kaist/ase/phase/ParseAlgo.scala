package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.algorithm._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.util._
import scala.io.Source

// ParseAlgo phase
case object ParseAlgo extends PhaseObj[Unit, ParseAlgoConfig, List[Algorithm]] {
  val name = "parse-algo"
  val help = "Parses algorithm files."

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: ParseAlgoConfig
  ): List[Algorithm] = {
    val filename = getFirstFilename(aseConfig, "parse-algo")
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

  def defaultConfig: ParseAlgoConfig = ParseAlgoConfig()
  val options: List[PhaseOption[ParseAlgoConfig]] = List(
    ("showFailed", BoolOption(c => c.showFailed = true),
      "show failed steps.")
  )
}

// ParseAlgo phase config
case class ParseAlgoConfig(
  var showFailed: Boolean = false
) extends Config
