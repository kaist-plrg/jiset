package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.util.ArgParser

sealed abstract class PhaseList[Result] {
  def getRunner(
    parser: ArgParser
  ): JISETConfig => Result

  def >>[C <: Config, R](phase: Phase[Result, C, R]): PhaseList[R] = PhaseCons(this, phase)

  val nameList: List[String]
  override def toString: String = nameList.reverse.mkString(" >> ")
}

case object PhaseNil extends PhaseList[Unit] {
  def getRunner(
    parser: ArgParser
  ): JISETConfig => Unit = x => {}

  val nameList: List[String] = Nil
}

case class PhaseCons[P, C <: Config, R](
  prev: PhaseList[P],
  phase: Phase[P, C, R]
) extends PhaseList[R] {
  def getRunner(
    parser: ArgParser
  ): JISETConfig => R = {
    val prevRunner = prev.getRunner(parser)
    val phaseRunner = phase.getRunner(parser)
    jisetConfig => phaseRunner(prevRunner(jisetConfig), jisetConfig)
  }

  val nameList: List[String] = phase.name :: prev.nameList
}

