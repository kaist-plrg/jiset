package kr.ac.kaist.jiset.util

import kr.ac.kaist.jiset.phase.{ Config, ArgRegex }
import kr.ac.kaist.jiset.error._

sealed abstract class OptionKind[PhaseConfig <: Config] {
  def argRegexList(name: String): List[ArgRegex[PhaseConfig]]
  def postfix: String
}

case class BoolOption[PhaseConfig <: Config](
    assign: PhaseConfig => Unit
) extends OptionKind[PhaseConfig] {
  def argRegexList(name: String): List[ArgRegex[PhaseConfig]] = List(
    (("-" + name).r, "".r, (c, _) => assign(c)),
    (("-" + name + "=").r, ".*".r, (c, _) => throw ExtraArgError(name))
  )
  def postfix: String = ""
}

case class NumOption[PhaseConfig <: Config](
    assign: (PhaseConfig, Int) => Unit
) extends OptionKind[PhaseConfig] {
  def argRegexList(name: String): List[ArgRegex[PhaseConfig]] = List(
    (("-" + name + "=").r, "[0-9]+".r, (c, s) => assign(c, s.toInt)),
    (("-" + name + "=").r, ".*".r, (_, _) => throw NoNumArgError(name)),
    (("-" + name).r, "".r, (_, _) => throw NoNumArgError(name))
  )
  def postfix: String = "={number}"
}

case class StrOption[PhaseConfig <: Config](
    assign: (PhaseConfig, String) => Unit
) extends OptionKind[PhaseConfig] {
  def argRegexList(name: String): List[ArgRegex[PhaseConfig]] = List(
    (("-" + name + "=").r, ".+".r, (c, s) => assign(c, s)),
    (("-" + name + "=").r, ".*".r, (_, _) => throw NoStrArgError(name)),
    (("-" + name).r, "".r, (_, _) => throw NoStrArgError(name))
  )
  def postfix: String = "={string}"
}

case class ListOption[PhaseConfig <: Config](
    assign: (PhaseConfig, List[String]) => Unit
) extends OptionKind[PhaseConfig] {
  def argRegexList(name: String): List[ArgRegex[PhaseConfig]] = List(
    (("-" + name + "=").r, "[^,]+[,[^,]+]*".r, (c, s) => assign(c, s.split(",").toList)),
    (("-" + name + "=").r, ".*".r, (_, _) => throw NoListArgError(name)),
    (("-" + name).r, "".r, (_, _) => throw NoListArgError(name))
  )
  def postfix: String = "={string1},{string2},..."
}

