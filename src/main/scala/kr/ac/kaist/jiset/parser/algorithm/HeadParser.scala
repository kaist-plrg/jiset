package kr.ac.kaist.jiset.parser.algorithm

import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.parser.grammar.ProductionParser
import kr.ac.kaist.jiset.spec.{ ECMAScript, Region }
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._
import scala.util.matching.Regex._

// head parsers
object HeadParser extends HeadParsers {
  import Head._

  def apply(elem: Element, detail: Boolean = false)(
    implicit
    lines: Array[String],
    grammar: Grammar,
    region: Region
  ): List[Head] = {
    try {
      var headElem = elem.siblingElements.get(0)
      if (rulePattern.matches(headElem.text)) {
        headElem = headElem.parent.siblingElements.get(0)
      }
      if (headElem.tagName != "h1") error(s"no algorithm head: $headElem")
      val str =
        if (isEquation(elem)) {
          val elemText = elem.text
          elemText.slice(0, elem.text.indexOf("=")).trim
        } else headElem.text

      val Region(envRange, builtinLine) = region

      // extract name
      val from = str.indexOf("(")
      var name = if (from == -1) str else str.substring(0, from)
      name = prefixPattern.replaceFirstIn(name, "").trim
      name = "[/\\s]".r.replaceAllIn(name, "")
      if (!nameCheck(name)) error(s"not target algorithm: $str")

      // extract parameters
      val params =
        if (isComparison(name)) COMP_PARAMS
        else if (from == -1) Nil
        else parse(paramList, str.substring(from)).get

      // classify head
      val prev = elem.previousElementSibling
      if (isEquation(elem)) {
        // equation
        List(NormalHead(name, params))
      } else if (isSyntaxDirected(prev)) {
        // fix name of regexp syntax -> always evaluation
        if (isRegexpSyntax(prev)) name = "Evaluation"

        // syntax-directed algorithms
        val nameMap = grammar.nameMap
        val idxMap = grammar.idxMap

        // with parameters
        val withParams: List[Param] = {
          val prevElem = headElem.nextElementSibling
          val isParagraph = prevElem.tagName == "p"
          val text = prevElem.text
          val isParams = "[wW]ith (parameter|argument).*".r.matches(text)
          if (!isParagraph || !isParams) Nil
          else withParamPattern.findAllMatchIn(text).toList.map(trimParam)
        }.map(Param(_))

        // extract emu-grammar
        val target =
          if (isCoreSyntax(prev)) prev
          else getElems(prev, "emu-grammar")(0)
        val body = getRawBody(target).toList
        // get head
        for {
          code <- splitBy(body, "")
          prod = ProductionParser(code)
          lhsName = prod.lhs.name
          rhs <- prod.rhsList
          rhsName = rhs.name
          syntax = lhsName + ":" + rhsName
          (i, j) <- idxMap.get(syntax)
        } yield SyntaxDirectedHead(nameMap(lhsName), i, j, rhs, name, withParams)
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

        // check if first step is "Let <var> be the ~ Environment Record ~"
        val firstStep = getRawBody(elem).head.trim
        val receiverParam = Param(firstStep match {
          case letEnvRecPattern(thisVar) => strip(thisVar, 1)
          case _ => firstReceiverParam(prev.text).getOrElse(ENV_PARAM)
        })

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
        val firstStep = getRawBody(elem).head.trim
        val receiverParam = Param(firstStep match {
          case letObjPattern(thisVar) => strip(thisVar, 1)
          case _ => firstReceiverParam(prev.text).getOrElse(OBJ_PARAM)
        })

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
    } catch {
      case e: Throwable =>
        if (detail) {
          println(s"[Head] ${e.getMessage}")
          e.getStackTrace.foreach(println _)
        }
        Nil
    }
  }
  // check whether current algorithm head is for equation functions.
  def isEquation(elem: Element): Boolean = elem.tagName == "emu-eqn"

  // check whether current algorithm head is for syntax directed functions.
  def isCoreSyntax(prev: Element): Boolean = prev.tagName == "emu-grammar"
  def isRegexpSyntax(prev: Element): Boolean =
    prev.tagName == "p" &&
      prev.text.startsWith("The production") &&
      !getElems(prev, "emu-grammar").isEmpty
  def isSyntaxDirected(prev: Element): Boolean =
    isCoreSyntax(prev) || isRegexpSyntax(prev)

  // check whether current algorithm head is for built-in functions.
  def isBuiltin(
    prev: Element,
    elem: Element,
    builtinLine: Int
  )(implicit lines: Array[String]): Boolean = getRange(elem) match {
    case None => false
    case Some((start, _)) =>
      start >= builtinLine && !prev.text.startsWith("The abstract operation")
  }

  // check whether current algorithm head is for thisValue
  def isThisValue(
    prev: Element,
    elem: Element,
    builtinLine: Int
  )(implicit lines: Array[String]): Boolean = getRange(elem) match {
    case None => false
    case Some((start, _)) =>
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
  )(implicit lines: Array[String]): Boolean = getRange(elem) match {
    case None => false
    case Some((start, end)) => {
      val (envStart, envEnd) = envRange
      val prevText = prev.text

      val included = start >= envStart && end <= envEnd
      val isMethod =
        !(prevText.startsWith("The abstract operation") ||
          prevText.startsWith("When the abstract operation"))

      included && isMethod
    }
  }

  // check whether current algorithm head is for object
  // internal method functions.
  def isObjMethod(name: String): Boolean =
    name.startsWith("[[") && name.endsWith("]]")

  // check whether algorithm is comparison
  def isComparison(name: String): Boolean = name.endsWith("Comparison")

  // check validity of names
  def nameCheck(name: String): Boolean =
    namePattern.matches(name) && !ECMAScript.PREDEF.contains(name)

  // find receiver parameter
  def firstReceiverParam(str: String): Option[String] = str match {
    case methodDescPattern(thisVar) => Some(strip(thisVar, 1))
    case _ => None
  }

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
trait HeadParsers extends Parsers {
  import ir._
  import Param.Kind._

  lazy val name = "[a-zA-Z]+".r
  lazy val field = (
    "." ~> name ^^ { EStr(_) } |
    "[" ~ "@@" ~> name <~ "]" ^^ { x => ir.Parser.parseExpr("SYMBOL_" + x) }
  )
  lazy val ref = name ~ rep(field) ^^ {
    case b ~ fs => fs.foldLeft[Ref](RefId(Id(b))) {
      case (b, f) => RefProp(b, f)
    }
  }
  lazy val param =
    "_[a-zA-Z0-9]+_".r ^^ { case s => s.substring(1, s.length - 1) }
  lazy val params: Parser[List[Param]] = (
    "[" ~ opt(",") ~> param ~ params <~ "]" ^^ { case x ~ ps => Param(x, Optional) :: ps } |
    opt(",") ~ "..." ~> param ~ params ^^ { case x ~ ps => Param(x, Variadic) :: ps } |
    opt(",") ~> param ~ params ^^ { case x ~ ps => Param(x) :: ps } |
    "" ^^^ Nil
  )
  lazy val paramList = (
    "(" ~> params <~ ")" |
    "(" ~ repsep(param | "…", ",") ~ ")" ^^^ Nil
  )
}
