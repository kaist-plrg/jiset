package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.ASEConfig
import kr.ac.kaist.ase.util.ArgParser

sealed abstract class PhaseList[Result] {
  def getRunner(
    parser: ArgParser
  ): ASEConfig => Result

  def >>[C <: Config, R](phase: PhaseObj[Result, C, R]): PhaseList[R] = PhaseCons(this, phase)

  val nameList: List[String]
  override def toString: String = nameList.reverse.mkString(" >> ")
}

case object PhaseNil extends PhaseList[Unit] {
  def getRunner(
    parser: ArgParser
  ): ASEConfig => Unit = x => {}

  val nameList: List[String] = Nil
}

case class PhaseCons[P, C <: Config, R](
    prev: PhaseList[P],
    phase: PhaseObj[P, C, R]
) extends PhaseList[R] {
  def getRunner(
    parser: ArgParser
  ): ASEConfig => R = {
    val prevRunner = prev.getRunner(parser)
    val phaseRunner = phase.getRunner(parser)
    aseConfig => phaseRunner(prevRunner(aseConfig), aseConfig)
  }

  val nameList: List[String] = phase.name :: prev.nameList
}

