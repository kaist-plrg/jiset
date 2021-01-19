package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.jiset.spec.{ ECMAScript, Region }
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._
import scala.util.matching.Regex._

trait Head {
  // name
  val name: String

  // parameters
  val params: List[Param]

  // conversion to string
  override def toString: String = s"$name (${params.mkString(", ")}):"
}

object Head extends HeadParsers {
  def parse(elem: Element)(
    implicit
    lines: Array[String],
    grammar: Grammar,
    region: Region
  ): List[Head] = {
    var headElem = elem.siblingElements.get(0)
    if (rulePattern.matches(headElem.text)) headElem = headElem.parent
    if (headElem.tagName != "h1") error(s"no algorithm head: $headElem")
    val str = headElem.text

    // extract region
    val Region(envRange, builtinLine) = region

    // extract name
    val from = str.indexOf("(")
    var name = if (from == -1) str else str.substring(0, from)
    name = prefixPattern.replaceFirstIn(name, "").trim
    if (!nameCheck(name)) error(s"not target algorithm: $str")
    name = "[/\\s]".r.replaceAllIn(name, "")

    // extract parameters
    val params =
      if (isComparison(name)) COMP_PARAMS
      else if (from == -1) Nil
      else parse(paramList, str.substring(from)).get

    // classify head
    val prev = elem.previousElementSibling
    if (isSyntaxDirected(elem)) {
      // syntax-directed algorithms
      val idxMap = grammar.idxMap

      // with parameters
      val withParams: List[Param] =
        toArray(elem.previousElementSiblings).toList.flatMap(prevElem => {
          val isParagraph = prevElem.tagName == "p"
          val text = prevElem.text
          val isParams = text.startsWith("With parameter")
          if (!isParagraph || !isParams) Nil
          else withParamPattern.findAllMatchIn(text).toList.map(trimParam)
        }).map(Param(_))

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
      } yield SyntaxDirectedHead(lhsName, rhs, i, j, name, withParams)
    } else if (isEnvMethod(prev, elem, envRange)) {
      // environment record method
      val bases =
        toArray(elem.parent.previousElementSiblings).toList.flatMap(prevElem => {
          val isHeader = prevElem.tagName == "h1"
          val text = prevElem.text
          val isEnvRecord = text.endsWith("Environment Records")

          if (isHeader && isEnvRecord) {
            List(prevElem.text.replaceAll(" ", "").dropRight(1))
          } else List.empty
        })

      val receiverParam = Param(firstParam(prev.text).getOrElse(ENV_PARAM))

      bases match {
        case base :: Nil =>
          List(MethodHead(base, name, receiverParam, params))
        case _ => error("`Head`: no base in environment record method")
      }
    } else if (isObjMethod(name)) {
      // object method
      val bases =
        toArray(elem.parent.previousElementSiblings).toList.flatMap(prevElem => {
          val isHeader = prevElem.tagName == "h1"
          val text = prevElem.text
          val isObject = text.contains(OBJECT)

          if (isHeader && isObject) {
            val endIdx = text.indexOfSlice(OBJECT) + OBJECT.length
            val base = normPattern.replaceAllIn(text.slice(0, endIdx), "")
            List(base)
          } else List.empty
        })

      val methodName = strip(name, 2)
      val receiverParam = Param(firstParam(prev.text).getOrElse(OBJ_PARAM))

      bases match {
        case base :: Nil =>
          List(MethodHead(base, methodName, receiverParam, params))
        case _ => error("`Head`: no base in object method")
      }
    } else if (isBuiltin(prev, elem, builtinLine)) {
      // built-in algorithms
      List(BuiltinHead(parseAll(ref, name).get, params))
    } else if (isThisValue(prev, elem, builtinLine)) {
      // thisValue
      val prevText = prev.text
      // NOTE name and params always exist
      val name = thisValuePattern.findAllIn(prevText).toList.head
      val params = List(Param(firstParam(prevText).get))
      List(NormalHead(name, params))
    } else {
      // normal algorithms
      List(NormalHead(name, params))
    }
  }

  // check whether current algorithm head is for syntax directed functions.
  def isSyntaxDirected(elem: Element): Boolean =
    elem.previousElementSibling.tagName == "emu-grammar"

  // check whether current algorithm head is for built-in functions.
  def isBuiltin(
    prev: Element,
    elem: Element,
    builtinLine: Int
  )(implicit lines: Array[String]): Boolean = {
    val (start, _) = getRange(elem).get
    start >= builtinLine && !prev.text.startsWith("The abstract operation")
  }

  // check whether current algorithm head is for thisValue
  def isThisValue(
    prev: Element,
    elem: Element,
    builtinLine: Int
  )(implicit lines: Array[String]): Boolean = {
    val (start, _) = getRange(elem).get
    start >= builtinLine &&
      prev.text.startsWith("The abstract operation") &&
      !thisValuePattern.findAllIn(prev.text).toList.isEmpty
  }

  // check whether current algorithm head is for environment record
  // internal method functions.
  def isEnvMethod(
    prev: Element,
    elem: Element,
    envRange: (Int, Int)
  )(implicit lines: Array[String]): Boolean = {
    val (start, end) = getRange(elem).get
    val (envStart, envEnd) = envRange
    val prevText = prev.text

    val included = start >= envStart && end <= envEnd
    val isMethod =
      !(prevText.startsWith("The abstract operation") ||
        prevText.startsWith("When the abstract operation"))

    included && isMethod
  }

  // check whether current algorithm head is for object
  // internal method functions.
  def isObjMethod(name: String): Boolean =
    name.startsWith("[[") && name.endsWith("]]")

  // check whether algorithm is comparison
  def isComparison(name: String): Boolean = name.endsWith("Comparison")

  // get names and parameters
  val paramPattern = "[^\\s,()\\[\\]]+".r
  val rulePattern = ".*(Statement|Expression)\\s*Rules".r
  val namePattern = "[.:a-zA-Z0-9%\\[\\]@ /`_-]+".r
  val prefixPattern = ".*Semantics:".r
  val withParamPattern = "_\\w+_".r
  val normPattern = "[\\s|-]".r
  val thisValuePattern = "this\\w+Value".r

  // check validity of names
  def nameCheck(name: String): Boolean =
    namePattern.matches(name) && !ECMAScript.PREDEF.contains(name)

  // find first parameter
  def firstParam(str: String): Option[String] =
    withParamPattern.findFirstMatchIn(str).map(trimParam _)

  // trim parameters
  def trimParam(m: Match): String = {
    val s = m.toString
    strip(s, 1)
  }

  // substring
  def strip(str: String, n: Int) = str.slice(n, str.length - n)
}
