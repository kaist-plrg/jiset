package kr.ac.kaist.jiset.analyzer.domain.value

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.ires.ir.Value

// value abstract domain
trait Domain extends AbsDomain[Value] { domain =>
  // abstract operators
  implicit class Ops(elem: Elem) {
    def addr: AbsAddr = domain.addr(elem)
    def func: AbsFunc = domain.func(elem)
    def cont: AbsCont = domain.cont(elem)
    def ast: AbsAST = domain.ast(elem)
    def prim: AbsPrim = domain.prim(elem)
  }

  // address accessors
  def addr(elem: Elem): AbsAddr

  // function accessors
  def func(elem: Elem): AbsFunc

  // continuation accessors
  def cont(elem: Elem): AbsCont

  // AST accessors
  def ast(elem: Elem): AbsAST

  // primitive value accessors
  def prim(elem: Elem): AbsPrim
}
