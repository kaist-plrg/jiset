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
  def apply(algo: Algo): Elem = Bot.copy(func = AbsFunc(AFunc(algo)))
  def apply(ast: AST): Elem = Bot.copy(ast = AbsAST(AAst(ast)))
  def apply(num: Num): Elem = Bot.copy(simple = AbsSimple(num))
  def apply(num: Double): Elem = Bot.copy(simple = AbsSimple(num))
  def apply(int: Long): Elem = Bot.copy(simple = AbsSimple(int))
  def apply(bigint: BigInt): Elem = Bot.copy(simple = AbsSimple(bigint))
  def apply(str: String): Elem = Bot.copy(simple = AbsSimple(str))
  def apply(bool: Boolean): Elem = Bot.copy(simple = AbsSimple(bool))
  lazy val num: Elem = Bot.copy(simple = AbsSimple.num)
  lazy val int: Elem = Bot.copy(simple = AbsSimple.int)
  lazy val bigint: Elem = Bot.copy(simple = AbsSimple.bigint)
  lazy val str: Elem = Bot.copy(simple = AbsSimple.str)
  lazy val bool: Elem = Bot.copy(simple = AbsSimple.bool)
  lazy val undef: Elem = Bot.copy(simple = AbsSimple.undef)
  lazy val nullv: Elem = Bot.copy(simple = AbsSimple.nullv)
  lazy val absent: Elem = Bot.copy(simple = AbsSimple.absent)
  def apply(value: Value): Elem = this(AValue.from(value))
  def apply(value: AValue): Elem = value match {
    case (comp: AComp) => Bot.copy(comp = AbsComp(comp))
    case (const: AConst) => Bot.copy(const = AbsConst(const))
    case (loc: Loc) => Bot.copy(loc = AbsLoc(loc))
    case AFunc(algo) => this(algo)
    case (clo: AClo) => Bot.copy(clo = AbsClo(clo))
    case (cont: ACont) => Bot.copy(cont = AbsCont(cont))
    case AAst(ast) => this(ast)
    case (simple: ASimple) => Bot.copy(simple = AbsSimple(simple))
  }

  // constructors
  def apply(
    comp: AbsComp = AbsComp.Bot,
    const: AbsConst = AbsConst.Bot,
    loc: AbsLoc = AbsLoc.Bot,
    func: AbsFunc = AbsFunc.Bot,
    clo: AbsClo = AbsClo.Bot,
    cont: AbsCont = AbsCont.Bot,
    ast: AbsAST = AbsAST.Bot,
    simple: AbsSimple = AbsSimple.Bot,
    num: AbsNum = AbsNum.Bot,
    int: AbsInt = AbsInt.Bot,
    bigint: AbsBigInt = AbsBigInt.Bot,
    str: AbsStr = AbsStr.Bot,
    bool: AbsBool = AbsBool.Bot,
    undef: AbsUndef = AbsUndef.Bot,
    nullv: AbsNull = AbsNull.Bot,
    absent: AbsAbsent = AbsAbsent.Bot
  ): Elem = {
    val newSimple = AbsSimple(num, int, bigint, str, bool, undef, nullv, absent)
    Elem(comp, const, loc, func, clo, cont, ast, simple ⊔ newSimple)
  }

  // extractors
  def unapply(elem: Elem) = Some((
    elem.comp,
    elem.const,
    elem.loc,
    elem.func,
    elem.clo,
    elem.cont,
    elem.ast,
    elem.simple,
  ))

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
    def pure: AbsValue = copy(comp = AbsComp.Bot)

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

    // meet operator
    def ⊓(that: Elem): Elem = Elem(
      this.comp ⊓ that.comp,
      this.const ⊓ that.const,
      this.loc ⊓ that.loc,
      this.func ⊓ that.func,
      this.clo ⊓ that.clo,
      this.cont ⊓ that.cont,
      this.ast ⊓ that.ast,
      this.simple ⊓ that.simple
    )

    // get single value
    def getSingle: Flat[AValue] = (
      this.comp.getSingle ⊔
      this.const.getSingle ⊔
      this.loc.getSingle ⊔
      this.func.getSingle ⊔
      this.clo.getSingle ⊔
      this.cont.getSingle ⊔
      this.ast.getSingle ⊔
      this.simple.getSingle
    )

    // get reachable locations
    def reachableLocs: Set[Loc] = {
      var locs = loc.toSet
      for ((_, AbsComp.Result(value, target)) <- comp.map) {
        locs ++= value.reachableLocs
        locs ++= target.reachableLocs
      }
      for {
        AClo(_, locals, _) <- clo
        (_, value) <- locals
      } locs ++= value.reachableLocs
      for {
        ACont(_, locals, _) <- cont
        (_, value) <- locals
      } locs ++= value.reachableLocs
      locs.filter(!_.isNamed)
    }

    // remove absent values
    def removeAbsent: Elem = copy(simple = simple.removeAbsent)

    // escape completion
    def escaped: Elem = comp.normal.value ⊔ copy(comp = AbsComp.Bot)

    // only values usable as keys
    def keyValue: AbsValue = AbsValue(loc = loc, str = str)

    // singleton checks
    def isSingle: Boolean = getSingle match {
      case FlatElem(_) => true
      case _ => false
    }

    // check completion
    def isCompletion: AbsBool = {
      var b: AbsBool = AbsBool.Bot
      if (!comp.isBottom) b ⊔= AT
      if (!pure.isBottom) b ⊔= AF
      b
    }

    // abstract equality
    def =^=(that: AbsValue): AbsBool = (this.getSingle, that.getSingle) match {
      case (FlatBot, _) | (_, FlatBot) => AbsBool.Bot
      case (FlatElem(l), FlatElem(r)) => AbsBool(Bool(l == r))
      case _ => if ((this ⊓ that).isBottom) AF else AB
    }

    // check abrupt completion
    def isAbruptCompletion: AbsBool = {
      var b: AbsBool = AbsBool.Bot
      if (!comp.removeNormal.isBottom) b ⊔= AT
      if (!comp.normal.isBottom || !pure.isBottom) b ⊔= AF
      b
    }

    // wrap completion
    def wrapCompletion: Elem = wrapCompletion("normal")
    def wrapCompletion(ty: String): Elem = AbsValue(comp = {
      if (pure.isBottom) comp
      else comp ⊔ AbsComp(ty -> AbsComp.Result(pure, AbsValue(CONST_EMPTY)))
    })

    // check absents
    def isAbsent: AbsBool = {
      var b: AbsBool = AbsBool.Bot
      if (!absent.isBottom) b ⊔= AF
      if (!removeAbsent.isBottom) b ⊔= AT
      b
    }
  }
}
