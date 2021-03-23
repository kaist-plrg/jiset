package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.ir.Beautifier._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.spec.{ ECMAScript, Region }
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.{ InfNum, PInf }
import org.jsoup.nodes._
import scala.util.matching.Regex

// ECMASCript abstract algorithms
case class Algo(
  head: Head,
  ids: List[String],
  rawBody: Inst,
  code: Iterable[String]
) {
  // head fields
  def name: String = head.printName
  def params: List[Param] = head.params

  // prepend instructions
  private def prepend(prefix: List[Inst], inst: Inst): Inst = prefix match {
    case Nil => inst
    case _ => inst match {
      case ISeq(list) => ISeq(prefix ++ list)
      case _ => ISeq(prefix :+ inst)
    }
  }

  // pop an instruction at the front
  private def popFront(inst: Inst): Inst = inst match {
    case ISeq(hd :: tl) => ISeq(tl)
    case _ => ISeq(Nil)
  }

  // get body with post processing
  def getBody: Inst = head match {
    case (head: MethodHead) if head.isLetThisStep(code.head.trim) =>
      popFront(rawBody)
    case (builtin: BuiltinHead) =>
      val prefix = builtin.origParams.zipWithIndex.map {
        case (x, i) =>
          Parser.parseInst(s"app ${x.name} = (GetArgument $ARGS_LIST ${i}i)")
      }
      prepend(prefix, rawBody)
    // handle abstract relational comparison
    case (head: NormalHead) if head.name == "AbstractRelationalComparison" =>
      val inst = Parser.parseInst(s"""if (= LeftFirst absent) { LeftFirst = true } else { }""")
      prepend(List(inst), rawBody)
    case _ => rawBody
  }

  // arity
  lazy val arity: (InfNum, InfNum) = head.arity

  // not supported
  lazy val todos: List[String] = {
    var l: List[String] = List()
    object Walker extends UnitWalker {
      override def walk(expr: Expr): Unit = expr match {
        case ENotSupported(msg) => l ::= msg
        case _ => super.walk(expr)
      }
    }
    Walker.walk(rawBody)
    l
  }

  // exclusion check
  def isParent(id: String): Boolean = ids contains id

  // completion check (not containing ??? or !!! in the algorithm body)
  def isComplete: Boolean = {
    var complete = true
    object Walker extends UnitWalker {
      override def walk(expr: Expr): Unit = expr match {
        case ENotSupported(_) => complete = false
        case _ => super.walk(expr)
      }
    }
    Walker.walk(rawBody)
    complete
  }

  // conversion to string
  override def toString: String =
    s"$head ${getBody.beautified(index = true)}"
}
