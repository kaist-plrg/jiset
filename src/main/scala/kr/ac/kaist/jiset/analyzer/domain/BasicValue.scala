package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js.ast.AST
import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._

// basic abstract values
object BasicValue extends Domain {
  lazy val Bot = Elem(
    loc = AbsLoc.Bot,
    func = AbsFunc.Bot,
    clo = AbsClo.Bot,
    cont = AbsCont.Bot,
    ast = AbsAST.Bot,
    simple = AbsSimple.Bot,
  )
  lazy val Top = Elem(
    loc = AbsLoc.Top,
    func = AbsFunc.Top,
    clo = AbsClo.Top,
    cont = AbsCont.Top,
    ast = AbsAST.Top,
    simple = AbsSimple.Top,
  )

  // abstraction functions
  def apply(algo: Algo): Elem = Bot.copy(func = AbsFunc(algo))
  def apply(ast: AST): Elem = Bot.copy(ast = AbsAST(ast))
  def apply(num: Num): Elem = Bot.copy(simple = AbsSimple(num))
  def apply(int: Long): Elem = Bot.copy(simple = AbsSimple(int))
  def apply(bigint: BigInt): Elem = Bot.copy(simple = AbsSimple(bigint))
  def apply(str: String): Elem = Bot.copy(simple = AbsSimple(str))
  def apply(bool: Boolean): Elem = Bot.copy(simple = AbsSimple(bool))
  lazy val undef: Elem = Bot.copy(simple = AbsSimple.undef)
  lazy val nullv: Elem = Bot.copy(simple = AbsSimple.nullv)
  lazy val absent: Elem = Bot.copy(simple = AbsSimple.absent)
  def apply(value: Value): Elem = this(AValue.from(value))
  def apply(value: AValue): Elem = value match {
    case (loc: Loc) => Bot.copy(loc = AbsLoc(loc))
    case AFunc(algo) => this(algo)
    case (clo: AClo) => Bot.copy(clo = AbsClo(clo))
    case (cont: ACont) => Bot.copy(cont = AbsCont(cont))
    case AAst(ast) => this(ast)
    case (simple: ASimple) => Bot.copy(simple = AbsSimple(simple))
  }

  // appender
  implicit val app: App[Elem] = (app, elem) => {
    if (elem.isBottom) app >> "⊥"
    else if (elem.isTop) app >> "⊤"
    else {
      val Elem(loc, func, clo, cont, ast, simple) = elem
      if (!loc.isBottom) app >> loc.toString
      if (!func.isBottom) app >> func.toString
      if (!clo.isBottom) app >> clo.toString
      if (!cont.isBottom) app >> cont.toString
      if (!ast.isBottom) app >> ast.toString
      if (!simple.isBottom) app >> simple.toString
      app
    }
  }

  // elements
  case class Elem(
    loc: AbsLoc,
    func: AbsFunc,
    clo: AbsClo,
    cont: AbsCont,
    ast: AbsAST,
    simple: AbsSimple
  ) extends ElemTrait {
    // getters
    def num: AbsNum = simple.num
    def int: AbsInt = simple.int
    def bigint: AbsBigInt = simple.bigint
    def str: AbsStr = simple.str
    def bool: AbsBool = simple.bool
    def undef: AbsUndef = simple.undef
    def nullv: AbsNull = simple.nullv
    def absent: AbsAbsent = simple.absent

    // partial order
    def ⊑(that: Elem): Boolean = (
      this.loc ⊑ that.loc &&
      this.func ⊑ that.func &&
      this.clo ⊑ that.clo &&
      this.cont ⊑ that.cont &&
      this.ast ⊑ that.ast &&
      this.simple ⊑ that.simple
    )

    // join operator
    def ⊔(that: Elem): Elem = Elem(
      this.loc ⊔ that.loc,
      this.func ⊔ that.func,
      this.clo ⊔ that.clo,
      this.cont ⊔ that.cont,
      this.ast ⊔ that.ast,
      this.simple ⊔ that.simple
    )

    // get single value
    def getSingle: Flat[AValue] = (
      this.loc.getSingle ⊔
      this.func.getSingle.map(x => AFunc(x)) ⊔
      this.clo.getSingle ⊔
      this.cont.getSingle ⊔
      this.ast.getSingle.map(x => AAst(x)) ⊔
      this.simple.getSingle
    )

    // remove absent values
    def removeAbsent: Elem = copy(simple = simple.removeAbsent)

    // escape completion
    def escaped: Elem = ???
  }
}
