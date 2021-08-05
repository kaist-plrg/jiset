package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._

// basic abstract simple values
object BasicSimple extends Domain {
  lazy val Bot = Elem(
    num = AbsNum.Bot,
    int = AbsInt.Bot,
    bigint = AbsBigInt.Bot,
    str = AbsStr.Bot,
    bool = AbsBool.Bot,
    undef = AbsUndef.Bot,
    nullv = AbsNull.Bot,
    absent = AbsAbsent.Bot,
  )
  lazy val Top = Elem(
    num = AbsNum.Top,
    int = AbsInt.Top,
    bigint = AbsBigInt.Top,
    str = AbsStr.Top,
    bool = AbsBool.Top,
    undef = AbsUndef.Top,
    nullv = AbsNull.Top,
    absent = AbsAbsent.Top,
  )

  // abstraction functions
  def apply(num: Num): Elem = Bot.copy(num = AbsNum(num))
  def apply(int: Long): Elem = Bot.copy(int = AbsInt(int))
  def apply(bigint: BigInt): Elem = Bot.copy(bigint = AbsBigInt(bigint))
  def apply(str: String): Elem = Bot.copy(str = AbsStr(str))
  def apply(bool: Boolean): Elem = Bot.copy(bool = AbsBool(bool))
  lazy val undef: Elem = Bot.copy(undef = AbsUndef.Top)
  lazy val nullv: Elem = Bot.copy(nullv = AbsNull.Top)
  lazy val absent: Elem = Bot.copy(absent = AbsAbsent.Top)
  def apply(simple: ASimple): Elem = simple.value match {
    case num: Num => this(num)
    case INum(long) => this(long)
    case BigINum(bigint) => this(bigint)
    case Str(str) => this(str)
    case Bool(bool) => this(bool)
    case Undef => this.undef
    case Null => this.nullv
    case Absent => this.absent
  }

  // appender
  implicit val app: App[Elem] = (app, elem) => {
    if (elem.isBottom) app >> "⊥"
    else if (elem.isTop) app >> "⊤"
    else {
      val Elem(num, int, bigint, str, bool, undef, nullv, absent) = elem
      if (!num.isBottom) app >> num.toString
      if (!int.isBottom) app >> int.toString
      if (!bigint.isBottom) app >> bigint.toString
      if (!str.isBottom) app >> str.toString
      if (!bool.isBottom) app >> bool.toString
      if (!undef.isBottom) app >> undef.toString
      if (!nullv.isBottom) app >> nullv.toString
      if (!absent.isBottom) app >> absent.toString
      app
    }
  }

  // elements
  case class Elem(
    num: AbsNum,
    int: AbsInt,
    bigint: AbsBigInt,
    str: AbsStr,
    bool: AbsBool,
    undef: AbsUndef,
    nullv: AbsNull,
    absent: AbsAbsent
  ) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (
      this.num ⊑ that.num &&
      this.int ⊑ that.int &&
      this.bigint ⊑ that.bigint &&
      this.str ⊑ that.str &&
      this.bool ⊑ that.bool &&
      this.undef ⊑ that.undef &&
      this.nullv ⊑ that.nullv &&
      this.absent ⊑ that.absent
    )

    // join operator
    def ⊔(that: Elem): Elem = Elem(
      this.num ⊔ that.num,
      this.int ⊔ that.int,
      this.bigint ⊔ that.bigint,
      this.str ⊔ that.str,
      this.bool ⊔ that.bool,
      this.undef ⊔ that.undef,
      this.nullv ⊔ that.nullv,
      this.absent ⊔ that.absent
    )

    // get single value
    def getSingle: Flat[ASimple] = (
      this.num.getSingle.map(x => ASimple(x)) ⊔
      this.int.getSingle.map(x => ASimple(INum(x))) ⊔
      this.bigint.getSingle.map(x => ASimple(BigINum(x))) ⊔
      this.str.getSingle.map(x => ASimple(Str(x))) ⊔
      this.bool.getSingle.map(x => ASimple(Bool(x))) ⊔
      this.undef.getSingle.map(x => ASimple(x)) ⊔
      this.nullv.getSingle.map(x => ASimple(x)) ⊔
      this.absent.getSingle.map(x => ASimple(x))
    )

    // remove absent values
    def removeAbsent: Elem = copy(absent = AbsAbsent.Bot)
  }
}
