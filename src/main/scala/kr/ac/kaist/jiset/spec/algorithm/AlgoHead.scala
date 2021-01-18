package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._
import scala.util.matching.Regex._

trait AlgoHead {
  // name
  val name: String

  // parameters
  val params: List[String]

  // conversion to string
  override def toString: String = s"$name (${params.mkString(", ")}):"
}

object AlgoHead {
  def apply(elem: Element)(
    implicit
    lines: Array[String],
    grammar: Grammar
  ): List[AlgoHead] = {
    var headElem = elem.siblingElements.get(0)
    if (rulePattern.matches(headElem.text)) headElem = headElem.parent
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
      .map(trimParam(_)).toList

    // classify head
    val prev = elem.previousElementSibling
    if (prev.tag.toString == "emu-grammar") {
      // syntax-directed algorithms
      val idxMap = grammar.idxMap

      // with parameters
      val withParams: List[String] =
        toArray(elem.previousElementSiblings).toList.flatMap(prevElem => {
          val isParagraph = prevElem.tag.toString == "p"
          val text = prevElem.text
          val isParams = text.startsWith("With parameter")
          if (isParagraph && isParams)
            withParamPattern.findAllMatchIn(text).map(trimParam(_)).toList
          else List.empty
        })

      // get head
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
        // prepend `this` parameter and number duplicated params
        ntParams = thisParam :: rename(rhs.getNTs.map(_.name))
      } yield SyntaxDirected(newName, ntParams ++ withParams, lhsName)
    } else if (false) {
      // TODO built-in algorithms - handle parameters
      ???
    } else {
      // normal algorithms
      List(Normal(name, params))
    }
  }

  // get names and parameters
  val paramPattern = "[^\\s,()\\[\\]]+".r
  val rulePattern = ".*(Statement|Expression)\\s*Rules".r
  val namePattern = "[.:a-zA-Z0-9%\\[\\]@ /`_-]+".r
  val prefixPattern = ".*Semantics:".r
  val withParamPattern = "_\\w+_".r
  val thisParam = "this"

  // check validity of names
  def nameCheck(name: String): Boolean =
    namePattern.matches(name) && !ECMAScript.PREDEF.contains(name)

  // rename for duplicated parameters for syntex-directed algorithms
  def rename(params: List[String]): List[String] = {
    val duplicated = params.filter(p => params.count(_ == p) > 1).toSet.toList
    var counter: Map[String, Int] = Map()
    params.map(p => {
      if (duplicated contains p) {
        val n = counter.getOrElse(p, 0)
        counter = counter + (p -> (n + 1))
        p + n.toString
      } else p
    })
  }

  // trim parameters
  def trimParam(m: Match): String = {
    val s = m.toString
    s.substring(1, s.length - 1)
  }
}

case class Normal(name: String, params: List[String]) extends AlgoHead
case class Builtin(name: String, params: List[String]) extends AlgoHead
case class SyntaxDirected(name: String, params: List[String], lhsName: String) extends AlgoHead
