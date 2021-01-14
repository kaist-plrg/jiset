package kr.ac.kaist.jiset.spec

import kr.ac.kaist.ires.ir
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.algorithm.GeneralAlgoCompiler
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._

// ECMASCript abstract algorithms
case class Algo(
    name: String,
    params: List[String],
    body: ir.Inst
) {
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
    s"$name (${params.mkString(", ")}) ${ir.beautify(body)}"
}

object Algo {
  // get algorithms
  def apply(
    elem: Element,
    detail: Boolean = false
  )(
    implicit
    lines: Array[String],
    grammar: Grammar
  ): Option[Algo] = optional {
    val (name, params) = getHead(elem)
    val code = getRawBody(elem)
    if (detail) {
      println(s"--------------------------------------------------")
      println(s"$name (${params.mkString(", ")}):")
      code.foreach(println _)
      println(s"====>")
    }
    val body =
      if (detail) errorLog(getBody(code))("[Error]: algorithm parsing failed")
      else getBody(code)
    val algo = Algo(name, params, body)
    if (detail) println(algo)
    algo
  }

  // get names and parameters
  val paramPattern = "[^\\s,()\\[\\]]+".r
  val namePattern = "[a-zA-Z]+".r // TODO extend
  val prefixPattern = ".*Semantics:".r
  def nameCheck(name: String): Boolean =
    namePattern.matches(name) && !ECMAScript.PREDEF.contains(name)
  def getHead(elem: Element)(implicit lines: Array[String]): (String, List[String]) = {
    val headElem = elem.siblingElements.get(0)
    if (headElem.tag.toString != "h1") error(s"no algorithm head: $headElem")
    val str = headElem.text

    // extract name
    val from = str.indexOf("(")
    var name = if (from == -1) str else str.substring(0, from)
    name = prefixPattern.replaceFirstIn(name, "").trim
    if (!nameCheck(name)) error(s"not target algorithm: $str")

    val prev = elem.previousElementSibling
    if (prev.tag.toString == "emu-grammar") {
      error("[TODO] syntax-directed algorithm")
      // for {
      //   code <- splitBy(getRawBody(prev).toList, "")
      //   prod = Production(code)
      //   names = prod.getIdxMap.keySet
      // } {
      //   code.foreach(println _)
      //   println(names)
      // }
    }

    // extract parameters
    val params = if (from == -1) Nil else paramPattern
      .findAllMatchIn(str.substring(from))
      .map(m => {
        val s = m.toString
        s.substring(1, s.length - 1)
      }).toList
    (name, params)
  }

  // get body instructions
  def getBody(code: Iterable[String])(implicit grammar: Grammar): ir.Inst = {
    val tokens = (new Tokenizer).getTokens(code)
    GeneralAlgoCompiler.compile(tokens)
  }
}
