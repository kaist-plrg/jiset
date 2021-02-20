package kr.ac.kaist.jiset.analyzer.domain.value

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

object ProdDomain extends value.Domain {
  // abstraction functions
  def alpha(v: Value): Elem = v match {
    case addr: Addr => Elem(addr = AbsAddr(addr))
    case clo: Clo => Elem(clo = AbsClo.Top)
    case cont: Cont => Elem(cont = AbsCont.Top)
    case ast: ASTVal => Elem(ast = AbsAST(ast))
    case prim: Prim => Elem(prim = AbsPrim(prim))
  }

  // bottom value
  val Bot: Elem = Elem()

  // top value
  val Top: Elem = Elem(
    AbsAddr.Top,
    AbsClo.Top,
    AbsCont.Top,
    AbsAST.Top,
    AbsPrim.Top
  )

  // constructor
  def apply(
    addr: AbsAddr = AbsAddr.Bot,
    clo: AbsClo = AbsClo.Bot,
    cont: AbsCont = AbsCont.Bot,
    ast: AbsAST = AbsAST.Bot,
    prim: AbsPrim = AbsPrim.Bot
  ): Elem = Elem(addr, clo, cont, ast, prim)

  case class Elem(
    addr: AbsAddr = AbsAddr.Bot,
    clo: AbsClo = AbsClo.Bot,
    cont: AbsCont = AbsCont.Bot,
    ast: AbsAST = AbsAST.Bot,
    prim: AbsPrim = AbsPrim.Bot
  ) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (
      this.addr ⊑ that.addr &&
      this.clo ⊑ that.clo &&
      this.cont ⊑ that.cont &&
      this.ast ⊑ that.ast &&
      this.prim ⊑ that.prim
    )

    // join operator
    def ⊔(that: Elem): Elem = Elem(
      this.addr ⊔ that.addr,
      this.clo ⊔ that.clo,
      this.cont ⊔ that.cont,
      this.ast ⊔ that.ast,
      this.prim ⊔ that.prim
    )

    // meet operator
    def ⊓(that: Elem): Elem = Elem(
      this.addr ⊓ that.addr,
      this.clo ⊓ that.clo,
      this.cont ⊓ that.cont,
      this.ast ⊓ that.ast,
      this.prim ⊓ that.prim
    )

    // concretization clotion
    def gamma: concrete.Set[Value] = (
      this.addr.gamma ++
      this.clo.gamma ++
      this.cont.gamma ++
      this.ast.gamma ++
      this.prim.gamma
    )

    // conversion to flat domain
    def getSingle: concrete.Flat[Value] = (
      this.addr.getSingle ++
      this.clo.getSingle ++
      this.cont.getSingle ++
      this.ast.getSingle ++
      this.prim.getSingle
    )
  }

  // Members declared in prim.Domain
  def addr(elem: Elem): AbsAddr = elem.addr
  def clo(elem: Elem): AbsClo = elem.clo
  def cont(elem: Elem): AbsCont = elem.cont
  def ast(elem: Elem): AbsAST = elem.ast
  def prim(elem: Elem): AbsPrim = elem.prim
}
