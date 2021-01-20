package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.ires.ir

trait Bug {
  // bug name
  val name: String

  // bug message
  val msg: String

  // conversion to string
  override def toString: String = s"[$name] $msg"
}
