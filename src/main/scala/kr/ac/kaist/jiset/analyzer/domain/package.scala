package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js.ast.AST
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.spec.algorithm.Algo

package object domain {
  //////////////////////////////////////////////////////////////////////////////
  // aliases
  //////////////////////////////////////////////////////////////////////////////
  lazy val T = Bool(true)
  lazy val F = Bool(false)
  lazy val AB = AbsBool.Top
  lazy val AT = AbsBool(Bool(true))
  lazy val AF = AbsBool(Bool(false))
  lazy val AVB = AbsValue(bool = AB)
  lazy val AVT = AbsValue(bool = AT)
  lazy val AVF = AbsValue(bool = AF)
  lazy val AV_COMP_PROPS =
    AbsValue("Type") ⊔ AbsValue("Value") ⊔ AbsValue("Target")
  //////////////////////////////////////////////////////////////////////////////
  // abstract domains
  //////////////////////////////////////////////////////////////////////////////
  // abstract states
  val AbsState = BasicState
  type AbsState = AbsState.Elem

  // abstract heaps
  val AbsHeap = BasicHeap
  type AbsHeap = AbsHeap.Elem

  // abstract objects
  val AbsObj = BasicObj
  type AbsObj = AbsObj.Elem

  // abstract values
  val AbsValue = BasicValue
  type AbsValue = AbsValue.Elem

  // abstract return values and heaps
  val AbsRet = BasicRet
  type AbsRet = AbsRet.Elem

  // abstract completions
  val AbsComp = BasicComp
  type AbsComp = AbsComp.Elem

  // abstract constants
  val AbsConst = SetDomain[AConst]("~")
  type AbsConst = AbsConst.Elem

  // abstract locations
  val AbsLoc = SetDomain[Loc]("#")
  type AbsLoc = AbsLoc.Elem

  // abstract functions
  val AbsFunc = SetDomain[AFunc]("λ")
  type AbsFunc = AbsFunc.Elem

  // abstract closures
  val AbsClo = BasicClo
  type AbsClo = AbsClo.Elem

  // abstract continuations
  val AbsCont = BasicCont
  type AbsCont = AbsCont.Elem

  // abstract simple values
  val AbsSimple = BasicSimple
  type AbsSimple = AbsSimple.Elem

  // abstract AST values
  val AbsAST = FlatDomain[AAst]("☊")
  type AbsAST = AbsAST.Elem

  // abstract floating-point number values
  // XXX select: FlatNum | IntervalNum
  val AbsNum = FlatNum
  type AbsNum = AbsNum.Elem

  // abstract integers
  val AbsInt = FlatInt
  type AbsInt = AbsInt.Elem

  // abstract big integers
  val AbsBigInt = FlatBigInt
  type AbsBigInt = AbsBigInt.Elem

  // abstract strings
  // XXX select: SetStr(k) | CharIncStr | PrefixSuffixStr
  val AbsStr = new SetStr(5)
  type AbsStr = AbsStr.Elem

  // abstract booleans
  val AbsBool = FlatBool
  type AbsBool = AbsBool.Elem

  // abstract undefined
  val AbsUndef = SimpleDomain(Undef)
  type AbsUndef = AbsUndef.Elem

  // abstract null
  val AbsNull = SimpleDomain(Null)
  type AbsNull = AbsNull.Elem

  // abstract absent
  val AbsAbsent = SimpleDomain(Absent)
  type AbsAbsent = AbsAbsent.Elem
}
