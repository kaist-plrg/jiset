package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.ires.ir
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.algorithm.GeneralAlgoCompiler
import kr.ac.kaist.jiset.algorithm.{ Nt, Code }
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._

// ECMASCript abstract algorithms
case class Algo(head: AlgoHead, body: ir.Inst) {
  // head fields
  def name: String = head.name
  def params: List[String] = head.params

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
      val heads = AlgoHead.parse(elem, builtinLine)
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
        if (detail) println(s"[Algo] ${e.getMessage}")
        Nil
    }
    if (detail) println(s"--------------------------------------------------")
    result
  }

  // get body instructions
  def getBody(
    head: AlgoHead,
    code: Iterable[String]
  )(implicit grammar: Grammar): ir.Inst = {
    import ir._
    val tokens = (new Tokenizer).getTokens(code)
    val prefix = head match {
      case (syntax: SyntaxDirectedHead) =>
        val x = syntax.lhsName
        val y = AlgoHead.THIS_PARAM
        List(Parser.parseInst(s"let $x = $y"))
      case (builtin: BuiltinHead) =>
        val args = AlgoHead.ARGS_LIST
        builtin.origParams.zipWithIndex.map {
          case (x, i) => Parser.parseInst(s"app $x = (GetArgument $args ${i}i)")
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
