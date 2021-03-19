package kr.ac.kaist.jiset.analyzer.domain.pure

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object ProdDomain extends pure.Domain {
  // abstraction functions
  def alpha(v: PureValue): Elem = v match {
    case loc: Loc => Elem(loc = AbsLoc(loc))
    case addr: Addr => Elem(loc = AbsLoc(addr.toLoc))
    case const: Const => Elem(const = AbsConst(const))
    case clo: Clo => Elem(clo = AbsClo(clo))
    case cont: Cont => Elem(cont = AbsCont(cont))
    case ast: ASTVal => Elem(ast = AbsAST(ast))
    case prim: Prim => Elem(prim = AbsPrim(prim))
  }

  // bottom value
  val Bot: Elem = Elem()

  // top value
  val Top: Elem = Elem(
    AbsLoc.Top,
    AbsTy.Top,
    AbsConst.Top,
    AbsClo.Top,
    AbsCont.Top,
    AbsAST.Top,
    AbsPrim.Top
  )

  // constructor
  def apply(
    loc: AbsLoc = AbsLoc.Bot,
    ty: AbsTy = AbsTy.Bot,
    const: AbsConst = AbsConst.Bot,
    clo: AbsClo = AbsClo.Bot,
    cont: AbsCont = AbsCont.Bot,
    ast: AbsAST = AbsAST.Bot,
    prim: AbsPrim = AbsPrim.Bot
  ): Elem = Elem(loc, ty, const, clo, cont, ast, prim)

  // constructor for types
  def apply(ty: Ty): Elem = Elem(ty = AbsTy(ty))

  // extractor
  def unapply(elem: Elem): Option[(AbsLoc, AbsTy, AbsConst, AbsClo, AbsCont, AbsAST, AbsPrim)] =
    Some((elem.loc, elem.ty, elem.const, elem.clo, elem.cont, elem.ast, elem.prim))

  case class Elem(
    loc: AbsLoc = AbsLoc.Bot,
    ty: AbsTy = AbsTy.Bot,
    const: AbsConst = AbsConst.Bot,
    clo: AbsClo = AbsClo.Bot,
    cont: AbsCont = AbsCont.Bot,
    ast: AbsAST = AbsAST.Bot,
    prim: AbsPrim = AbsPrim.Bot
  ) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (
      this.loc ⊑ that.loc &&
      this.ty ⊑ that.ty &&
      this.const ⊑ that.const &&
      this.clo ⊑ that.clo &&
      this.cont ⊑ that.cont &&
      this.ast ⊑ that.ast &&
      this.prim ⊑ that.prim
    )

    // join operator
    def ⊔(that: Elem): Elem = Elem(
      this.loc ⊔ that.loc,
      this.ty ⊔ that.ty,
      this.const ⊔ that.const,
      this.clo ⊔ that.clo,
      this.cont ⊔ that.cont,
      this.ast ⊔ that.ast,
      this.prim ⊔ that.prim
    )

    // meet operator
    def ⊓(that: Elem): Elem = Elem(
      this.loc ⊓ that.loc,
      this.ty ⊓ that.ty,
      this.const ⊓ that.const,
      this.clo ⊓ that.clo,
      this.cont ⊓ that.cont,
      this.ast ⊓ that.ast,
      this.prim ⊓ that.prim
    )

    // prune
    def prune(v: PureValue): Elem = v match {
      case loc: Loc => copy(loc = this.loc.prune(loc))
      case const: Const => copy(const = this.const.prune(const))
      case clo: Clo => copy(clo = this.clo.prune(clo))
      case cont: Cont => copy(cont = this.cont.prune(cont))
      case ast: ASTVal => copy(ast = this.ast.prune(ast))
      case prim: Prim => copy(prim = this.prim.prune(prim))
      case _ => this
    }

    // concretization clotion
    def gamma: concrete.Set[PureValue] = (
      this.loc.gamma ++
      (if (this.ty.isBottom) Finite() else Infinite) ++
      this.const.gamma ++
      this.clo.gamma ++
      this.cont.gamma ++
      this.ast.gamma ++
      this.prim.gamma
    )

    // conversion to flat domain
    def getSingle: concrete.Flat[PureValue] = (
      this.loc.getSingle ++
      (if (this.ty.isBottom) Zero else Many) ++
      this.const.getSingle ++
      this.clo.getSingle ++
      this.cont.getSingle ++
      this.ast.getSingle ++
      this.prim.getSingle
    )

    // conversion to normal completion
    def toCompletion: AbsComp = AbsComp(
      CompNormal -> (this, AbsPure(Const("empty")))
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
    def bigint: AbsBigINum = prim.bigint
    def str: AbsStr = prim.str
    def bool: AbsBool = prim.bool
    def undef: AbsUndef = prim.undef
    def nullval: AbsNull = prim.nullval
    def absent: AbsAbsent = prim.absent
  }
}
