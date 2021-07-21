package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AssignmentOperator extends AST { val kind: String = "AssignmentOperator" }

object AssignmentOperator {
  def apply(data: Json): AssignmentOperator = AST(data) match {
    case Some(compressed) => AssignmentOperator(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AssignmentOperator = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        AssignmentOperator0(params, span)
      case 1 =>
        AssignmentOperator1(params, span)
      case 2 =>
        AssignmentOperator2(params, span)
      case 3 =>
        AssignmentOperator3(params, span)
      case 4 =>
        AssignmentOperator4(params, span)
      case 5 =>
        AssignmentOperator5(params, span)
      case 6 =>
        AssignmentOperator6(params, span)
      case 7 =>
        AssignmentOperator7(params, span)
      case 8 =>
        AssignmentOperator8(params, span)
      case 9 =>
        AssignmentOperator9(params, span)
      case 10 =>
        AssignmentOperator10(params, span)
      case 11 =>
        AssignmentOperator11(params, span)
    }
  }
}

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
