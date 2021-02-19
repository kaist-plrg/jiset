package kr.ac.kaist.jiset.analyzer.domain.value

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

// value abstract domain
trait Domain extends AbsDomain[Value] {
  // constructor
  def apply(
    addr: AbsAddr = AbsAddr.Bot,
    clo: AbsClo = AbsClo.Bot,
    cont: AbsCont = AbsCont.Bot,
    ast: AbsAST = AbsAST.Bot,
    prim: AbsPrim = AbsPrim.Bot
  ): Elem

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
