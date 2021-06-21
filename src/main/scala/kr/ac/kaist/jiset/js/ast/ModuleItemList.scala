package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ModuleItemList extends AST { val kind: String = "ModuleItemList" }

case class ModuleItemList0(x0: ModuleItem, parserParams: List[Boolean], span: Span) extends ModuleItemList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ModuleItem", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ModuleItemList1(x0: ModuleItemList, x1: ModuleItem, parserParams: List[Boolean], span: Span) extends ModuleItemList {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("ModuleItem", x1, l("ModuleItemList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}
