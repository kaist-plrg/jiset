package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.cfg.Function
import kr.ac.kaist.jiset.spec.{ SpecElem, Parser }
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.spec.{ ECMAScript, Region }
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._
import scala.util.matching.Regex

// ECMASCript abstract algorithms
case class Algo(
  head: Head,
  id: String,
  rawBody: Inst,
  code: Iterable[String]
) extends SpecElem {
  // unique ids
  private var uid: Int = -1
  def setUId(k: Int): Unit = uid = k
  override def hashCode: Int = if (uid == -1) super.hashCode else uid
  override def equals(that: Any): Boolean = that match {
    case that: Algo if this.uid != -1 && that.uid != -1 =>
      this.uid == that.uid
    case _ => super.equals(that)
  }

  // functions
  private var funcOpt: Option[Function] = None
  def setFunc(func: Function) = funcOpt = Some(func)
  lazy val func: Function = funcOpt.getOrElse {
    error(s"function is not defined for the algorithm: $name")
  }

  // head fields
  def name: String = head.name
  def params: List[Param] = head.params

  // check builtin
  def isBuiltin: Boolean = head.isBuiltin

  // add instructions
  private def prepend(p: List[Inst], i: Inst): Inst = add(p, i, true)
  private def append(a: List[Inst], i: Inst): Inst = add(a, i, false)
  private def add(
    added: List[Inst],
    inst: Inst,
    prepend: Boolean
  ): Inst = added match {
    case Nil => inst
    case _ => inst match {
      case ISeq(list) =>
        ISeq(if (prepend) added ++ list else list ++ added)
      case _ =>
        ISeq(if (prepend) added :+ inst else inst :: added)
    }
  }

  // pop an instruction at the front
  private def popFront(inst: Inst): Inst = inst match {
    case ISeq(hd :: tl) => ISeq(tl)
    case _ => ISeq(Nil)
  }

  // get body with post processing
  lazy val body: Inst = getBody
  private def getBody: Inst = head match {
    case (head: SyntaxDirectedHead) if head.needPrefix =>
      val prefix = Insts(s"let ${head.lhsName} = this")
      prepend(prefix, rawBody)
    case (head: MethodHead) if head.isLetThisStep(code) =>
      popFront(rawBody)
    case (builtin: BuiltinHead) =>
      import Param.Kind._
      val prefix = builtin.origParams.zipWithIndex.map {
        case (Param(name, Variadic), i) =>
          Inst(s"let ${name} = $ARGS_LIST")
        case (Param(name, _), i) =>
          Inst(s"app ${name} = (GetArgument $ARGS_LIST)")
      }
      prepend(prefix, rawBody)
    // handle abstract relational comparison
    case (head: NormalHead) if head.name == "AbstractRelationalComparison" =>
      val inst = Inst("if (= LeftFirst absent) { LeftFirst = true } else { }")
      prepend(List(inst), rawBody)
    case _ => rawBody
  }

  // not supported
  def todos: List[String] = {
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

  // completion check (not containing ??? or !!! in the algorithm body)
  def isComplete: Boolean = rawBody.isComplete

  // normal check
  def isNormal: Boolean = head match {
    case _: NormalHead => true
    case _ => false
  }

  // ancestor check
  def isAncestor(spec: ECMAScript, targetId: String): Boolean =
    spec.getAncestors(id) contains targetId
}
object Algo extends Parser[Algo]
