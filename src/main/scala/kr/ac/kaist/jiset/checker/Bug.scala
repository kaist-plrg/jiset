package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.ires.ir

abstract class Bug(name: String, msg: String) {
  // conversion to string
  override def toString: String = s"[$name] $msg"
}

// reference errors
case class ReferenceError(algo: Algo, names: Set[String]) extends Bug(
  "ReferenceError",
  s"${algo.name}: ${names.mkString(", ")}"
)

// duplicated variables
case class DuplicatedVariable(algo: Algo, names: Set[String]) extends Bug(
  "DuplicatedVariable",
  s"${algo.name}: ${names.mkString(", ")}"
)
