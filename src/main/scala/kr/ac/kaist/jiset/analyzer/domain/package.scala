package kr.ac.kaist.jiset.analyzer

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

  // abstract contexts
  lazy val AbsCtxt: ctxt.Domain = ctxt.BasicDomain
  type AbsCtxt = AbsCtxt.Elem

  // abstract environment
  lazy val AbsEnv: env.Domain = env.BasicDomain
  type AbsEnv = AbsEnv.Elem

  // abstract heaps
  lazy val AbsHeap: heap.Domain = heap.BasicDomain
  type AbsHeap = AbsHeap.Elem

  // abstract objects
  lazy val AbsObj: obj.Domain = obj.BasicDomain
  type AbsObj = AbsObj.Elem

  // abstract values
  lazy val AbsValue: value.Domain = value.ProdDomain
  type AbsValue = AbsValue.Elem

  // abstract addresses
  lazy val AbsAddr: addr.Domain = addr.SetDomain
  type AbsAddr = AbsAddr.Elem

  // abstract continuations
  lazy val AbsCont: cont.Domain = cont.SimpleDomain
  type AbsCont = AbsCont.Elem

  // abstract function closures
  lazy val AbsClo: clo.Domain = clo.SimpleDomain
  type AbsClo = AbsClo.Elem

  // abstract AST values
  lazy val AbsAST: ast.Domain = ast.SimpleDomain
  type AbsAST = AbsAST.Elem

  // abstract primitives
  lazy val AbsPrim: prim.Domain = prim.ProdDomain
  type AbsPrim = AbsPrim.Elem

  // abstract numbers
  lazy val AbsNum: num.Domain = num.FlatDomain
  type AbsNum = AbsNum.Elem

  // abstract integers
  lazy val AbsINum: inum.Domain = inum.FlatDomain
  type AbsINum = AbsINum.Elem

  // abstract big integers
  lazy val AbsBigINum: biginum.Domain = biginum.FlatDomain
  type AbsBigINum = AbsBigINum.Elem

  // abstract strings
  lazy val AbsStr: str.Domain = str.FlatDomain
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

  //////////////////////////////////////////////////////////////////////////////
  // helpers
  //////////////////////////////////////////////////////////////////////////////
  lazy val AbsTrue = AbsBool(true)
  lazy val AbsFalse = AbsBool(false)

  //////////////////////////////////////////////////////////////////////////////
  // implicit conversions
  //////////////////////////////////////////////////////////////////////////////
  implicit def bool2boolean(bool: Bool): Boolean = bool.bool
  implicit def boolean2bool(bool: Boolean): Bool = Bool(bool)
}
