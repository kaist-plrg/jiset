package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.ires.ir._

package object domain {
  import generator._
  import combinator._

  // extensible abstract domain
  type EAbsDomain[T] = AbsDomain[T] with Singleton

  //////////////////////////////////////////////////////////////////////////////
  // concrete domain
  //////////////////////////////////////////////////////////////////////////////
  lazy val Infinite = concrete.Infinite
  lazy val Finite = concrete.Finite
  lazy val Zero = concrete.Zero
  lazy val One = concrete.One
  lazy val Many = concrete.Many

  //////////////////////////////////////////////////////////////////////////////
  // abstract domain
  //////////////////////////////////////////////////////////////////////////////
  // abstract states
  lazy val AbsState = state.BasicDomain
  type AbsState = AbsState.Elem

  // TODO abstract contexts
  // TODO abstract heaps
  // TODO abstract objects
  // TODO abstract references

  // abstract values
  lazy val AbsValue = value.BasicDomain
  type AbsValue = AbsValue.Elem

  // TODO abstract locations
  // TODO abstract functions
  // TODO abstract primitives

  // abstract numbers
  lazy val AbsNum = SetDomain[Num]()
  type AbsNum = AbsNum.Elem

  // abstract strings
  lazy val AbsStr = SetDomain[Str]()
  type AbsStr = AbsStr.Elem

  // abstract booleans
  lazy val AbsBool: bool.Domain = bool.FlatDomain
  type AbsBool = AbsBool.Elem

  // abstract undefined values
  lazy val AbsUndef: undef.Domain = undef.SimpleDomain
  type AbsUndef = AbsUndef.Elem

  // abstract null values
  lazy val AbsNull: nullval.Domain = nullval.SimpleDomain
  type AbsNull = AbsNull.Elem

  // abstract absent values
  lazy val AbsAbsent: absent.Domain = absent.SimpleDomain
  type AbsAbsent = AbsAbsent.Elem

  // helpers
  lazy val AbsTrue = AbsBool(true)
  lazy val AbsFalse = AbsBool(false)

  //////////////////////////////////////////////////////////////////////////////
  // implicit conversions
  //////////////////////////////////////////////////////////////////////////////
  implicit def bool2boolean(bool: Bool): Boolean = bool.bool
  implicit def boolean2bool(bool: Boolean): Bool = Bool(bool)
}
