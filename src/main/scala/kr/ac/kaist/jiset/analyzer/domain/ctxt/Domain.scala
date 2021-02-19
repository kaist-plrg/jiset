package kr.ac.kaist.jiset.analyzer.domain.ctxt

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.ops._

// heap abstract domain
trait Domain extends AbsDomain[Ctxt] with EmptyValue {
  // globals accessors
  def globals(elem: Elem): AbsEnv

  // locals accessors
  def locals(elem: Elem): AbsEnv

  // retVal accessors
  def retVal(elem: Elem): AbsValue

  // return value
  def doReturn(elem: Elem, value: AbsValue): Elem
}
