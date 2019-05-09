package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.parser._
import kr.ac.kaist.ase.util.Useful.fileReader
import kr.ac.kaist.ase.error.NoFileError
import scala.io.Source

// Parse phase
case object Parse extends PhaseObj[Unit, ParseConfig, List[Step]] {
  val name = "parse"
  val help = "Parses files." + LINE_SEP +
    "If multiple files are given, they are concatenated in the given order before being parsed."

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: ParseConfig
  ): List[Step] = aseConfig.fileNames match {
    case Nil => throw NoFileError("parse")
    case filename :: _ =>
      val sls = Parser.getList(Algorithm, fileReader(filename))
      val ss = (List[Step]() /: sls) {
        case (list, sl) => (list /: sl.steps) {
          case (list, s) => s.getSteps(list)
        }
      }
      ss.map(_.toBriefString).sorted.foreach(println _)
      Nil
  }

  def defaultConfig: ParseConfig = ParseConfig()
  val options: List[PhaseOption[ParseConfig]] = List()
}

// Parse phase config
case class ParseConfig() extends Config
