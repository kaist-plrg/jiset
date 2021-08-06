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
  lazy val AT = AbsBool(Bool(true))
  lazy val AF = AbsBool(Bool(false))

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
  val AbsClo = FlatDomain[AClo]("=>")
  type AbsClo = AbsClo.Elem

  // abstract continuations
  val AbsCont = FlatDomain[ACont]("[=>]")
  type AbsCont = AbsCont.Elem

  // abstract simple values
  val AbsSimple = BasicSimple
  type AbsSimple = AbsSimple.Elem

  // abstract AST values
  val AbsAST = FlatDomain[AAst]("☊")
  type AbsAST = AbsAST.Elem

  // abstract floating-point number values
  val AbsNum = FlatDomain[Num]("num")
  type AbsNum = AbsNum.Elem

  // abstract integers
  val AbsInt = FlatDomain[INum]("int")
  type AbsInt = AbsInt.Elem

  // abstract big integers
  val AbsBigInt = FlatDomain[BigINum]("bigint")
  type AbsBigInt = AbsBigInt.Elem

  // abstract strings
  val AbsStr = FlatDomain[Str]("str")
  type AbsStr = AbsStr.Elem

  // abstract booleans
  val AbsBool = FlatDomain[Bool]("bool")
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
