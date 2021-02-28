package kr.ac.kaist.jiset.analyzer

import scala.collection.immutable.StringOps
import kr.ac.kaist.jiset.ir._

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
  // abstract with states
  lazy val AbsState = state.BasicDomain
  type AbsState = AbsState.Elem

  // abstract environment
  lazy val AbsEnv = env.BasicDomain
  type AbsEnv = AbsEnv.Elem

  // abstract heaps
  lazy val AbsHeap = heap.BasicDomain
  type AbsHeap = AbsHeap.Elem

  // abstract objects
  lazy val AbsObj = obj.BasicDomain
  type AbsObj = AbsObj.Elem

  // abstract values
  lazy val AbsValue = value.ProdDomain
  type AbsValue = AbsValue.Elem

  // abstract addresses
  lazy val AbsAddr = addr.SetDomain
  type AbsAddr = AbsAddr.Elem

  // abstract addresses
  lazy val AbsSymb = symb.SetDomain
  type AbsSymb = AbsSymb.Elem

  // abstract continuations
  lazy val AbsCont = cont.SimpleDomain
  type AbsCont = AbsCont.Elem

  // abstract function closures
  lazy val AbsClo = clo.SetDomain
  type AbsClo = AbsClo.Elem

  // abstract AST values
  lazy val AbsAST = ast.SetDomain
  type AbsAST = AbsAST.Elem

  // abstract primitives
  lazy val AbsPrim = prim.ProdDomain
  type AbsPrim = AbsPrim.Elem

  // abstract numbers
  lazy val AbsNum = num.FlatDomain
  type AbsNum = AbsNum.Elem

  // abstract integers
  lazy val AbsINum = inum.FlatDomain
  type AbsINum = AbsINum.Elem

  // abstract big integers
  lazy val AbsBigINum = biginum.FlatDomain
  type AbsBigINum = AbsBigINum.Elem

  // abstract strings
  lazy val AbsStr = str.FlatDomain
  type AbsStr = AbsStr.Elem

  // abstract booleans
  lazy val AbsBool = bool.FlatDomain
  type AbsBool = AbsBool.Elem

  // abstract undefined values
  lazy val AbsUndef = undef.SimpleDomain
  type AbsUndef = AbsUndef.Elem

  // abstract null values
  lazy val AbsNull = nullval.SimpleDomain
  type AbsNull = AbsNull.Elem

  // abstract absent values
  lazy val AbsAbsent = absent.SimpleDomain
  type AbsAbsent = AbsAbsent.Elem

  // abstract reference values
  lazy val AbsRefValue = refvalue.BasicDomain
  type AbsRefValue = AbsRefValue.Elem

  //////////////////////////////////////////////////////////////////////////////
  // helpers
  //////////////////////////////////////////////////////////////////////////////
  lazy val AbsTrue = AbsBool(true)
  lazy val AbsFalse = AbsBool(false)

  // abstract Scala strings
  lazy val StrFlat = new FlatDomain[String]
  type StrFlat = StrFlat.Elem

  //////////////////////////////////////////////////////////////////////////////
  // implicit conversions
  //////////////////////////////////////////////////////////////////////////////
  implicit def bool2boolean(x: Bool): Boolean = x.bool
  implicit def boolean2bool(x: Boolean): Bool = Bool(x)
  implicit def str2string(x: Str): StringOps = x.str
  implicit def string2str(x: String): Str = Str(x)
  implicit def long2inum(x: Long): INum = INum(x)
  implicit def inum2long(x: INum): Long = x.long
  implicit def bigint2biginum(x: BigInt): BigINum = BigINum(x)
  implicit def biginum2bigint(x: BigINum): BigInt = x.bigint
  implicit def double2num(x: Double): Num = Num(x)
  implicit def num2double(x: Num): Double = x.double

  implicit def num2prim[T](x: T)(implicit ev: T => AbsNum) =
    AbsPrim(num = x)
  implicit def int2prim[T](x: T)(implicit ev: T => AbsINum) =
    AbsPrim(int = x)
  implicit def bigint2prim[T](x: T)(implicit ev: T => AbsBigINum) =
    AbsPrim(bigint = x)
  implicit def str2prim[T](x: T)(implicit ev: T => AbsStr) =
    AbsPrim(str = x)
  implicit def bool2prim[T](x: T)(implicit ev: T => AbsBool) =
    AbsPrim(bool = x)
  implicit def undef2prim[T](x: T)(implicit ev: T => AbsUndef) =
    AbsPrim(undef = x)
  implicit def null2prim[T](x: T)(implicit ev: T => AbsNull) =
    AbsPrim(nullval = x)
  implicit def absent2prim[T](x: T)(implicit f: T => AbsAbsent) =
    AbsPrim(absent = x)
  implicit def addr2value[T](x: T)(implicit f: T => AbsAddr) =
    AbsValue(addr = x)
  implicit def clo2value[T](x: T)(implicit f: T => AbsClo) =
    AbsValue(clo = x)
  implicit def cont2value[T](x: T)(implicit f: T => AbsCont) =
    AbsValue(cont = x)
  implicit def ast2value[T](x: T)(implicit f: T => AbsAST) =
    AbsValue(ast = x)
  implicit def prim2value[T](x: T)(implicit f: T => AbsPrim) =
    AbsValue(prim = x)
}
