package kr.ac.kaist.jiset.analyzer.domain.value

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

// value abstract domain
trait Domain extends AbsDomain[Value] { domain =>
  // abstract operators
  implicit class Ops(elem: Elem) {
    def addr: AbsAddr = domain.addr(elem)
    def clo: AbsClo = domain.clo(elem)
    def cont: AbsCont = domain.cont(elem)
    def ast: AbsAST = domain.ast(elem)
    def prim: AbsPrim = domain.prim(elem)
  }

  // address accessors
  def addr(elem: Elem): AbsAddr

  // function closure accessors
  def clo(elem: Elem): AbsClo

  // continuation accessors
  def cont(elem: Elem): AbsCont

  // AST accessors
  def ast(elem: Elem): AbsAST

  // primitive value accessors
  def prim(elem: Elem): AbsPrim
}
