package kr.ac.kaist.jiset.analyzer.domain.prim

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

object ProdDomain extends prim.Domain {
  // abstraction functions
  def alpha(v: Prim): Elem = v match {
    case num: Num => Elem(num = AbsNum(num))
    case int: INum => Elem(int = AbsINum(int))
    case bigint: BigINum => Elem(bigint = AbsBigINum(bigint))
    case str: Str => Elem(str = AbsStr(str))
    case bool: Bool => Elem(bool = AbsBool(bool))
    case undef: Undef => Elem(undef = AbsUndef(undef))
    case nullval: Null => Elem(nullval = AbsNull(nullval))
    case absent: Absent => Elem(absent = AbsAbsent(absent))
  }

  // bottom value
  val Bot: Elem = Elem()

  // top value
  val Top: Elem = Elem(
    AbsNum.Top,
    AbsINum.Top,
    AbsBigINum.Top,
    AbsStr.Top,
    AbsBool.Top,
    AbsUndef.Top,
    AbsNull.Top,
    AbsAbsent.Top
  )

  // constructor
  def apply(
    num: AbsNum = AbsNum.Bot,
    int: AbsINum = AbsINum.Bot,
    bigint: AbsBigINum = AbsBigINum.Bot,
    str: AbsStr = AbsStr.Bot,
    bool: AbsBool = AbsBool.Bot,
    undef: AbsUndef = AbsUndef.Bot,
    nullval: AbsNull = AbsNull.Bot,
    absent: AbsAbsent = AbsAbsent.Bot
  ): Elem = Elem(num, int, bigint, str, bool, undef, nullval, absent)

  case class Elem(
    num: AbsNum = AbsNum.Bot,
    int: AbsINum = AbsINum.Bot,
    bigint: AbsBigINum = AbsBigINum.Bot,
    str: AbsStr = AbsStr.Bot,
    bool: AbsBool = AbsBool.Bot,
    undef: AbsUndef = AbsUndef.Bot,
    nullval: AbsNull = AbsNull.Bot,
    absent: AbsAbsent = AbsAbsent.Bot
  ) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (
      this.num ⊑ that.num &&
      this.int ⊑ that.int &&
      this.bigint ⊑ that.bigint &&
      this.str ⊑ that.str &&
      this.bool ⊑ that.bool &&
      this.undef ⊑ that.undef &&
      this.nullval ⊑ that.nullval &&
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
      this.nullval ⊔ that.nullval,
      this.absent ⊔ that.absent
    )

    // meet operator
    def ⊓(that: Elem): Elem = Elem(
      this.num ⊓ that.num,
      this.int ⊓ that.int,
      this.bigint ⊓ that.bigint,
      this.str ⊓ that.str,
      this.bool ⊓ that.bool,
      this.undef ⊓ that.undef,
      this.nullval ⊓ that.nullval,
      this.absent ⊓ that.absent
    )

    // concretization function
    def gamma: concrete.Set[Prim] = (
      this.num.gamma ++
      this.int.gamma ++
      this.bigint.gamma ++
      this.str.gamma ++
      this.bool.gamma ++
      this.undef.gamma ++
      this.nullval.gamma ++
      this.absent.gamma
    )

    // conversion to flat domain
    def getSingle: concrete.Flat[Prim] = (
      this.num.getSingle ++
      this.int.getSingle ++
      this.bigint.getSingle ++
      this.str.getSingle ++
      this.bool.getSingle ++
      this.undef.getSingle ++
      this.nullval.getSingle ++
      this.absent.getSingle
    )
  }
}
