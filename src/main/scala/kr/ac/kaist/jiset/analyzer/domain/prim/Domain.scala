package kr.ac.kaist.jiset.analyzer.domain.prim

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.ires.ir.Const

// primitive value abstract domain
trait Domain extends AbsDomain[Const] { domain =>
  // abstract operators
  implicit class Ops(elem: Elem) {
    def num: AbsNum = domain.num(elem)
    def int: AbsINum = domain.int(elem)
    def bigint: AbsBigINum = domain.bigint(elem)
    def str: AbsStr = domain.str(elem)
    def bool: AbsBool = domain.bool(elem)
    def undef: AbsUndef = domain.undef(elem)
    def nullval: AbsNull = domain.nullval(elem)
    def absent: AbsAbsent = domain.absent(elem)
  }

  // number accessors
  def num(elem: Elem): AbsNum

  // integer accessors
  def int(elem: Elem): AbsINum

  // big integer accessors
  def bigint(elem: Elem): AbsBigINum

  // string accessors
  def str(elem: Elem): AbsStr

  // boolean accessors
  def bool(elem: Elem): AbsBool

  // undefined accessors
  def undef(elem: Elem): AbsUndef

  // null accessors
  def nullval(elem: Elem): AbsNull

  // absent accessors
  def absent(elem: Elem): AbsAbsent
}
