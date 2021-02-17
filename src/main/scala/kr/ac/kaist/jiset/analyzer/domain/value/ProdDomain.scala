package kr.ac.kaist.jiset.analyzer.domain.value

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object ProdDomain extends value.Domain {
  // abstraction functions
  def alpha(v: Value): Elem = v match {
    case addr: Addr => Elem(_addr = AbsAddr(addr))
    case func: Func => Elem(_func = AbsFunc.Top)
    case cont: Cont => Elem(_cont = AbsCont.Top)
    case ast: ASTVal => Elem(_ast = AbsAST.Top)
    case prim: Const => Elem(_prim = AbsPrim(prim))
  }

  // Members declared in Domain
  val Bot: Elem = Elem()
  val Top: Elem = Elem(
    AbsAddr.Top,
    AbsFunc.Top,
    AbsCont.Top,
    AbsAST.Top,
    AbsPrim.Top
  )

  case class Elem(
      _addr: AbsAddr = AbsAddr.Bot,
      _func: AbsFunc = AbsFunc.Bot,
      _cont: AbsCont = AbsCont.Bot,
      _ast: AbsAST = AbsAST.Bot,
      _prim: AbsPrim = AbsPrim.Bot
  ) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (
      this._addr ⊑ that.addr &&
      this._func ⊑ that.func &&
      this._cont ⊑ that.cont &&
      this._ast ⊑ that.ast &&
      this._prim ⊑ that.prim
    )

    // join operator
    def ⊔(that: Elem): Elem = Elem(
      this._addr ⊔ that.addr,
      this._func ⊔ that.func,
      this._cont ⊔ that.cont,
      this._ast ⊔ that.ast,
      this._prim ⊔ that.prim
    )

    // meet operator
    def ⊓(that: Elem): Elem = Elem(
      this._addr ⊓ that.addr,
      this._func ⊓ that.func,
      this._cont ⊓ that.cont,
      this._ast ⊓ that.ast,
      this._prim ⊓ that.prim
    )

    // concretization function
    def gamma: concrete.Set[Value] = (
      this._addr.gamma ++
      this._func.gamma ++
      this._cont.gamma ++
      this._ast.gamma ++
      this._prim.gamma
    )

    // conversion to flat domain
    def getSingle: concrete.Flat[Value] = (
      this._addr.getSingle ++
      this._func.getSingle ++
      this._cont.getSingle ++
      this._ast.getSingle ++
      this._prim.getSingle
    )
  }

  // Members declared in prim.Domain
  def addr(elem: Elem): AbsAddr = elem._addr
  def func(elem: Elem): AbsFunc = elem._func
  def cont(elem: Elem): AbsCont = elem._cont
  def ast(elem: Elem): AbsAST = elem._ast
  def prim(elem: Elem): AbsPrim = elem._prim
}
