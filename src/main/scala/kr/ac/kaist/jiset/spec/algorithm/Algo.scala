package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.spec.{ ECMAScript, Region }
import kr.ac.kaist.jiset.util.Conversion._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.{ InfNum, PInf }
import org.jsoup.nodes._

// ECMASCript abstract algorithms
case class Algo(head: Head, private val rawBody: Inst, code: Iterable[String]) {
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
        case ENotYetModeled(msg) => l ::= msg
        case _ => super.walk(expr)
      }
    }
    Walker.walk(rawBody)
    l
  }

  // completion check (not containing ??? or !!! in the algorithm body)
  def isComplete: Boolean = {
    var complete = true
    object Walker extends UnitWalker {
      override def walk(expr: Expr): Unit = expr match {
        case ENotYetModeled(_) | ENotSupported(_) => complete = false
        case _ => super.walk(expr)
      }
    }
    Walker.walk(rawBody)
    complete
  }

  // conversion to string
  override def toString: String =
    s"$head ${beautify(getBody, index = true)}"
}
