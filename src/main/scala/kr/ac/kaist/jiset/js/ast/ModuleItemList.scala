package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ModuleItemList extends AST { val kind: String = "ModuleItemList" }

object ModuleItemList {
  def apply(data: Json): ModuleItemList = AST(data) match {
    case Some(compressed) => ModuleItemList(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ModuleItemList = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ModuleItem(_)).get
        ModuleItemList0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(ModuleItemList(_)).get
        val x1 = subs(1).map(ModuleItem(_)).get
        ModuleItemList1(x0, x1, params, span)
    }
  }
}

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
