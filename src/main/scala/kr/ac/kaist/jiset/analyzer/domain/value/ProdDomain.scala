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

    // abstract equality
    def =^=(that: Elem): AbsBool =
      (this.getSingle, that.getSingle) match {
        case (Zero, _) | (_, Zero) => AbsBool.Bot
        case (One(x), One(y)) => AbsBool(x == y)
        case _ => AbsBool.Top
      }

    // accessors for primitive values
    def num: AbsNum = prim.num
    def int: AbsINum = prim.int
    def bigint: AbsBigINum = prim.bigint
    def str: AbsStr = prim.str
    def bool: AbsBool = prim.bool
    def undef: AbsUndef = prim.undef
    def nullval: AbsNull = prim.nullval
    def absent: AbsAbsent = prim.absent
  }
}
