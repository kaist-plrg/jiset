package kr.ac.kaist.jiset.analyzer.domain.state

import kr.ac.kaist.jiset.analyzer.State
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.ops._

// state abstract domain
trait Domain extends AbsDomain[State] with EmptyValue {
  // context accessors
  def ctxt(elem: Elem): AbsCtxt

  // heap accessors
  def heap(elem: Elem): AbsHeap

  // return value
  def doReturn(elem: Elem, value: AbsValue): Elem
}
