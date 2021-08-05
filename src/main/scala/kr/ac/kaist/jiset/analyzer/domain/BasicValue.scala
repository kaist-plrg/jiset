package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js.ast.AST
import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// basic abstract values
object BasicValue extends Domain {
  lazy val Bot = Elem(
    comp = AbsComp.Bot,
    const = AbsConst.Bot,
    loc = AbsLoc.Bot,
    func = AbsFunc.Bot,
    clo = AbsClo.Bot,
    cont = AbsCont.Bot,
    ast = AbsAST.Bot,
    simple = AbsSimple.Bot,
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
    case (comp: AComp) => Bot.copy(comp = AbsComp(comp))
    case (const: AConst) => Bot.copy(const = AbsConst(const.name))
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
    else {
      val Elem(comp, const, loc, func, clo, cont, ast, simple) = elem
      var strs = Vector[String]()
      if (!comp.isBottom) strs :+= comp.toString
      if (!const.isBottom) strs :+= const.toString
      if (!loc.isBottom) strs :+= loc.toString
      if (!func.isBottom) strs :+= func.toString
      if (!clo.isBottom) strs :+= clo.toString
      if (!cont.isBottom) strs :+= cont.toString
      if (!ast.isBottom) strs :+= ast.toString
      if (!simple.isBottom) strs :+= simple.toString
      app >> strs.mkString(", ")
    }
  }

  // elements
  case class Elem(
    comp: AbsComp,
    const: AbsConst,
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
      this.comp ⊑ that.comp &&
      this.const ⊑ that.const &&
      this.loc ⊑ that.loc &&
      this.func ⊑ that.func &&
      this.clo ⊑ that.clo &&
      this.cont ⊑ that.cont &&
      this.ast ⊑ that.ast &&
      this.simple ⊑ that.simple
    )

    // join operator
    def ⊔(that: Elem): Elem = Elem(
      this.comp ⊔ that.comp,
      this.const ⊔ that.const,
      this.loc ⊔ that.loc,
      this.func ⊔ that.func,
      this.clo ⊔ that.clo,
      this.cont ⊔ that.cont,
      this.ast ⊔ that.ast,
      this.simple ⊔ that.simple
    )

    // get single value
    def getSingle: Flat[AValue] = (
      this.comp.getSingle ⊔
      this.const.getSingle.map(x => AConst(x)) ⊔
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
    def escaped: Elem = comp("normal").value ⊔ copy(comp = AbsComp.Bot)
  }
}
