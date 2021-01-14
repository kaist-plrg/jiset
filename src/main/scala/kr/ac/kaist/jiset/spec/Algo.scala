package kr.ac.kaist.jiset.spec

import kr.ac.kaist.ires.ir
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.algorithm.GeneralAlgoCompiler
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
    detail: Boolean = false
  )(
    implicit
    lines: Array[String],
    grammar: Grammar
  ): List[Algo] = try {
    val heads = getHeads(elem)
    if (detail) {
      println(s"--------------------------------------------------")
      heads.foreach {
        case AlgoHead(name, params) => println(s"$name (${params.mkString(", ")}):")
      }
    }
    val code = getRawBody(elem)
    if (detail) {
      code.foreach(println _)
      println(s"====>")
    }
    val body = getBody(code)
    if (detail) println(ir.beautify(body))
    heads.map(Algo(_, body))
  } catch {
    case _: Throwable =>
      if (detail) println("[Error]: algorithm parsing failed")
      Nil
  }

  // get names and parameters
  val paramPattern = "[^\\s,()\\[\\]]+".r
  val namePattern = "[a-zA-Z]+".r // TODO extend
  val prefixPattern = ".*Semantics:".r
  def nameCheck(name: String): Boolean =
    namePattern.matches(name) && !ECMAScript.PREDEF.contains(name)
  def getHeads(elem: Element)(
    implicit
    lines: Array[String],
    grammar: Grammar
  ): List[AlgoHead] = {
    val headElem = elem.siblingElements.get(0)
    if (headElem.tag.toString != "h1") error(s"no algorithm head: $headElem")
    val str = headElem.text

    // extract name
    val from = str.indexOf("(")
    var name = if (from == -1) str else str.substring(0, from)
    name = prefixPattern.replaceFirstIn(name, "").trim
    if (!nameCheck(name)) error(s"not target algorithm: $str")

    // extract parameters
    val params = if (from == -1) Nil else paramPattern
      .findAllMatchIn(str.substring(from))
      .map(m => {
        val s = m.toString
        s.substring(1, s.length - 1)
      }).toList

    val prev = elem.previousElementSibling
    if (prev.tag.toString == "emu-grammar") {
      // syntax-directed algorithms
      val idxMap = grammar.idxMap
      val body = getRawBody(prev).toList
      for {
        code <- splitBy(body, "")
        prod = Production(code)
        lhsName = prod.lhs.name
        rhs <- prod.rhsList
        rhsName <- rhs.names
        syntax = lhsName + ":" + rhsName
        (i, j) <- idxMap.get(syntax)
        newName = s"$lhsName[$i,$j].$name"
        newParams = rhs.getNTs.map(_.name) ++ params
      } yield AlgoHead(newName, newParams)
    } else if (false) {
      // TODO built-in algorithms
      ???
    } else {
      // normal algorithms
      List(AlgoHead(name, params))
    }
  }

  // get body instructions
  def getBody(code: Iterable[String])(implicit grammar: Grammar): ir.Inst = {
    val tokens = (new Tokenizer).getTokens(code)
    GeneralAlgoCompiler.compile(tokens)
  }
}

case class AlgoHead(name: String, params: List[String]) {
  override def toString: String = s"$name (${params.mkString(", ")})"
}
