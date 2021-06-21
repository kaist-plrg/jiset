package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait AssignmentOperator extends AST { val kind: String = "AssignmentOperator" }

case class AssignmentOperator0(parserParams: List[Boolean], span: Span) extends AssignmentOperator {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"*="
  }
}

case class AssignmentOperator1(parserParams: List[Boolean], span: Span) extends AssignmentOperator {
  def idx: Int = 1
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"/="
  }
}

case class AssignmentOperator2(parserParams: List[Boolean], span: Span) extends AssignmentOperator {
  def idx: Int = 2
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"%="
  }
}

case class AssignmentOperator3(parserParams: List[Boolean], span: Span) extends AssignmentOperator {
  def idx: Int = 3
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"+="
  }
}

case class AssignmentOperator4(parserParams: List[Boolean], span: Span) extends AssignmentOperator {
  def idx: Int = 4
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"-="
  }
}

case class AssignmentOperator5(parserParams: List[Boolean], span: Span) extends AssignmentOperator {
  def idx: Int = 5
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"<<="
  }
}

case class AssignmentOperator6(parserParams: List[Boolean], span: Span) extends AssignmentOperator {
  def idx: Int = 6
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s">>="
  }
}

case class AssignmentOperator7(parserParams: List[Boolean], span: Span) extends AssignmentOperator {
  def idx: Int = 7
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s">>>="
  }
}

case class AssignmentOperator8(parserParams: List[Boolean], span: Span) extends AssignmentOperator {
  def idx: Int = 8
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"&="
  }
}

case class AssignmentOperator9(parserParams: List[Boolean], span: Span) extends AssignmentOperator {
  def idx: Int = 9
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"^="
  }
}

case class AssignmentOperator10(parserParams: List[Boolean], span: Span) extends AssignmentOperator {
  def idx: Int = 10
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"|="
  }
}

case class AssignmentOperator11(parserParams: List[Boolean], span: Span) extends AssignmentOperator {
  def idx: Int = 11
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"**="
  }
}
