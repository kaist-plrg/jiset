package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.node.algorithm._
import kr.ac.kaist.ase.util.Useful.fileReader
import kr.ac.kaist.ase.error.NoFileError
import scala.io.Source

// Parse phase
case object Parse extends PhaseObj[Unit, ParseConfig, List[Algorithm]] {
  val name = "parse"
  val help = "Parses files." + LINE_SEP +
    "If multiple files are given, they are concatenated in the given order before being parsed."

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: ParseConfig
  ): List[Algorithm] = aseConfig.fileNames match {
    case Nil => throw NoFileError("parse")
    case filename :: _ =>
      val algos = Algorithm.getList(fileReader(filename))
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
      algos
  }

  def defaultConfig: ParseConfig = ParseConfig()
  val options: List[PhaseOption[ParseConfig]] = List()
}

// Parse phase config
case class ParseConfig() extends Config
