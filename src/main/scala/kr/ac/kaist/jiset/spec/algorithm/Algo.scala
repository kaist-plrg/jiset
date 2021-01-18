package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.ires.ir
import kr.ac.kaist.ires.ir.Parser.parseInst
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.algorithm.GeneralAlgoCompiler
import kr.ac.kaist.jiset.algorithm.{ Nt, Code }
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._

// ECMASCript abstract algorithms
case class Algo(head: Head, body: ir.Inst) {
  // head fields
  def name: String = head.name
  def params: List[Param] = head.params

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
object Algo {
  // get algorithms
  def parse(
    elem: Element,
    builtinLine: Int,
    detail: Boolean = false
  )(
    implicit
    lines: Array[String],
    grammar: Grammar
  ): List[Algo] = {
    if (detail) println(s"--------------------------------------------------")
    val result = try {
      val heads = Head.parse(elem, builtinLine)
      if (detail) heads.foreach(println(_))
      val code =
        if (s"${elem.tag}" == "ul") toArray(elem.children).map(li => "* " + li.text)
        else getRawBody(elem)
      if (detail) {
        code.foreach(println _)
        println(s"====>")
      }
      var printBody = detail && true
      heads.map(h => {
        val body = getBody(h, code)
        if (printBody) {
          println(ir.beautify(body))
          printBody = false
        }
        Algo(h, body)
      })
    } catch {
      case e: Throwable =>
        if (detail) {
          println(s"[Algo] ${e.getMessage}")
          e.getStackTrace.foreach(println _)
        }
        Nil
    }
    if (detail) println(s"--------------------------------------------------")
    result
  }

  // get body instructions
  def getBody(
    head: Head,
    code: Iterable[String]
  )(implicit grammar: Grammar): ir.Inst = {
    import ir._
    val tokens = (new Tokenizer).getTokens(code)
    val prefix = head match {
      case (syntax: SyntaxDirectedHead) =>
        val x = syntax.lhsName
        List(parseInst(s"let $x = $THIS_PARAM"))
      case (builtin: BuiltinHead) =>
        builtin.origParams.zipWithIndex.map {
          case (x, i) => parseInst(s"app ${x.name} = (GetArgument $ARGS_LIST ${i}i)")
        }
      case _ => Nil
    }
    val body = GeneralAlgoCompiler.compile(tokens)
    prefix match {
      case Nil => body
      case _ => body match {
        case ISeq(list) => ISeq(prefix ++ list)
        case _ => ISeq(prefix :+ body)
      }
    }
  }
}
