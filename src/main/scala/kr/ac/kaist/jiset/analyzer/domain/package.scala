package kr.ac.kaist.jiset.analyzer

import scala.collection.immutable.StringOps

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
  implicit class ValueOps(elem: AbsValue) {
    def addr: AbsAddr = AbsValue.addr(elem)
    def clo: AbsClo = AbsValue.clo(elem)
    def cont: AbsCont = AbsValue.cont(elem)
    def ast: AbsAST = AbsValue.ast(elem)
    def prim: AbsPrim = AbsValue.prim(elem)
  }

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
  implicit class PrimOps(elem: AbsPrim) {
    def num: AbsNum = AbsPrim.num(elem)
    def int: AbsINum = AbsPrim.int(elem)
    def bigint: AbsBigINum = AbsPrim.bigint(elem)
    def str: AbsStr = AbsPrim.str(elem)
    def bool: AbsBool = AbsPrim.bool(elem)
    def undef: AbsUndef = AbsPrim.undef(elem)
    def nullval: AbsNull = AbsPrim.nullval(elem)
    def absent: AbsAbsent = AbsPrim.absent(elem)
  }

  // abstract numbers
  lazy val AbsNum: num.Domain = num.FlatDomain
  type AbsNum = AbsNum.Elem
  implicit class NumOps(e: AbsNum) extends ops.NumericOpsHelper {
    type Domain = AbsNum.type
    val Domain = AbsNum
    val elem = e
  }

  // abstract integers
  lazy val AbsINum: inum.Domain = inum.FlatDomain
  type AbsINum = AbsINum.Elem
  implicit class INumOps(e: AbsINum)
    extends ops.NumericOpsHelper
    with ops.BitwiseOpsHelper
    with ops.ShiftOpsHelper {
    type Domain = AbsINum.type
    val Domain = AbsINum
    val elem = e
  }

  // abstract big integers
  lazy val AbsBigINum: biginum.Domain = biginum.FlatDomain
  type AbsBigINum = AbsBigINum.Elem
  implicit class BigINumOps(e: AbsBigINum)
    extends ops.NumericOpsHelper
    with ops.BitwiseOpsHelper
    with ops.ShiftOpsHelper {
    type Domain = AbsBigINum.type
    val Domain = AbsBigINum
    val elem = e
  }

  // abstract strings
  lazy val AbsStr: str.Domain = str.FlatDomain
  type AbsStr = AbsStr.Elem
  implicit class StrOps(elem: AbsStr) {
    def +(that: AbsStr): AbsStr = AbsStr.add(elem, that)
    def -(num: AbsINum): AbsStr = AbsStr.sub(elem, num)
    def *(num: AbsINum): AbsStr = AbsStr.mul(elem, num)
    def <(that: AbsStr): AbsBool = AbsStr.lt(elem, that)
  }

  // abstract booleans
  lazy val AbsBool: bool.Domain = bool.FlatDomain
  type AbsBool = AbsBool.Elem
  implicit class BoolOps(elem: AbsBool) {
    def &&(that: AbsBool): AbsBool = AbsBool.and(elem, that)
    def ||(that: AbsBool): AbsBool = AbsBool.or(elem, that)
    def ^(that: AbsBool): AbsBool = AbsBool.xor(elem, that)
    def unary_!(): AbsBool = AbsBool.not(elem)
  }

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
