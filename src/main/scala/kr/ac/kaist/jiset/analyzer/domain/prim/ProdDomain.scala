package kr.ac.kaist.jiset.analyzer.domain.prim

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object ProdDomain extends prim.Domain {
  // abstraction functions
  def alpha(v: Const): Elem = v match {
    case num: Num => Elem(_num = AbsNum(num))
    case int: INum => Elem(_int = AbsINum(int))
    case bigint: BigINum => Elem(_bigint = AbsBigINum(bigint))
    case str: Str => Elem(_str = AbsStr(str))
    case bool: Bool => Elem(_bool = AbsBool(bool))
    case undef: Undef => Elem(_undef = AbsUndef(undef))
    case nullval: Null => Elem(_nullval = AbsNull(nullval))
    case absent: Absent => Elem(_absent = AbsAbsent(absent))
  }

  // Members declared in Domain
  val Bot: Elem = Elem(
    AbsNum.Bot,
    AbsINum.Bot,
    AbsBigINum.Bot,
    AbsStr.Bot,
    AbsBool.Bot,
    AbsUndef.Bot,
    AbsNull.Bot,
    AbsAbsent.Bot
  )
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

  case class Elem(
      _num: AbsNum = AbsNum.Bot,
      _int: AbsINum = AbsINum.Bot,
      _bigint: AbsBigINum = AbsBigINum.Bot,
      _str: AbsStr = AbsStr.Bot,
      _bool: AbsBool = AbsBool.Bot,
      _undef: AbsUndef = AbsUndef.Bot,
      _nullval: AbsNull = AbsNull.Bot,
      _absent: AbsAbsent = AbsAbsent.Bot
  ) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (
      this._num ⊑ that._num &&
      this._int ⊑ that._int &&
      this._bigint ⊑ that._bigint &&
      this._str ⊑ that._str &&
      this._bool ⊑ that._bool &&
      this._undef ⊑ that._undef &&
      this._nullval ⊑ that._nullval &&
      this._absent ⊑ that._absent
    )

    // join operator
    def ⊔(that: Elem): Elem = Elem(
      this._num ⊔ that._num,
      this._int ⊔ that._int,
      this._bigint ⊔ that._bigint,
      this._str ⊔ that._str,
      this._bool ⊔ that._bool,
      this._undef ⊔ that._undef,
      this._nullval ⊔ that._nullval,
      this._absent ⊔ that._absent
    )

    // meet operator
    def ⊓(that: Elem): Elem = Elem(
      this._num ⊓ that._num,
      this._int ⊓ that._int,
      this._bigint ⊓ that._bigint,
      this._str ⊓ that._str,
      this._bool ⊓ that._bool,
      this._undef ⊓ that._undef,
      this._nullval ⊓ that._nullval,
      this._absent ⊓ that._absent
    )

    // concretization function
    def gamma: concrete.Set[Const] = (
      this._num.gamma ++
      this._int.gamma ++
      this._bigint.gamma ++
      this._str.gamma ++
      this._bool.gamma ++
      this._undef.gamma ++
      this._nullval.gamma ++
      this._absent.gamma
    )

    // conversion to flat domain
    def getSingle: concrete.Flat[Const] = (
      this._num.getSingle ++
      this._int.getSingle ++
      this._bigint.getSingle ++
      this._str.getSingle ++
      this._bool.getSingle ++
      this._undef.getSingle ++
      this._nullval.getSingle ++
      this._absent.getSingle
    )
  }

  // Members declared in prim.Domain
  def num(elem: Elem): AbsNum = elem._num
  def int(elem: Elem): AbsINum = elem._int
  def bigint(elem: Elem): AbsBigINum = elem._bigint
  def str(elem: Elem): AbsStr = elem._str
  def bool(elem: Elem): AbsBool = elem._bool
  def undef(elem: Elem): AbsUndef = elem._undef
  def nullval(elem: Elem): AbsNull = elem._nullval
  def absent(elem: Elem): AbsAbsent = elem._absent
}
