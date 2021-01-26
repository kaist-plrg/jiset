package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.ires.ir
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.spec.{ ECMAScript, Region }
import kr.ac.kaist.jiset.util.Conversion._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.{ InfNum, PInf }
import org.jsoup.nodes._

// ECMASCript abstract algorithms
case class Algo(head: Head, body: ir.Inst, code: Iterable[String]) {
  // head fields
  def name: String = head.name
  def params: List[Param] = head.params

  // arity
  lazy val arity: (InfNum, InfNum) = head.arity

  // not supported
  lazy val todos: List[String] = {
    var l: List[String] = List()
    object Walker extends ir.UnitWalker {
      override def walk(expr: ir.Expr): Unit = expr match {
        case ir.ENotSupported(msg) => l ::= msg
        case ir.ENotYetModeled(msg) => l ::= msg
        case _ => super.walk(expr)
      }
    }
    Walker.walk(body)
    l
  }

  // completion check (not containing ??? or !!! in the algorithm body)
  def isComplete: Boolean = {
    var complete = true
    object Walker extends ir.UnitWalker {
      override def walk(expr: ir.Expr): Unit = expr match {
        case ir.ENotYetModeled(_) | ir.ENotSupported(_) => complete = false
        case _ => super.walk(expr)
      }
    }
    Walker.walk(body)
    complete
  }

  // conversion to string
  override def toString: String =
    s"$head ${ir.beautify(body)}"
}
