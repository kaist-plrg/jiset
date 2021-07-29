package kr.ac.kaist.jiset.extractor.algorithm

import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.extractor.grammar.ProductionParser
import kr.ac.kaist.jiset.spec.{ ECMAScript, Region }
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._
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
  ): List[Head] = try {
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
    val isStatic = name match {
      case semanticsPattern(kind, x) =>
        name = x
        kind == "Static"
      case _ => false
    }
    name = "[-/\\s]".r.replaceAllIn(name, "")
    if (!nameCheck(name)) error(s"not target algorithm: $str")

    // extract parameters
    val params =
      if (manualParams.contains(name)) manualParams(name)
      else if (from == -1) Nil
      else parse(paramList, str.substring(from)).get

    // classify head
    val prev = elem.previousElementSibling
    if (isEquation(elem))
      getEquationHead(name, params)
    else if (isSyntaxDirected(prev))
      getSyntaxDirectedHead(name, isStatic, headElem, prev)
    else if (isEnvMethod(prev, elem, envRange))
      getEnvMethodHead(name, prev, elem, params)
    else if (isObjMethod(name))
      getObjMethodHead(name, prev, elem, params)
    else if (isBuiltin(name, elem, builtinLine))
      getBuiltinHead(name, headElem, params)
    else if (isThisValue(prev, elem, builtinLine))
      getThisValueHead(prev)
    else
      getNormalHead(name, params)
  } catch {
    case e: Throwable =>
      if (detail) {
        println(s"[Head] ${e.getMessage}")
        e.getStackTrace.foreach(println _)
      }
      Nil
  }

  // normal head
  def getNormalHead(name: String, params: List[Param]): List[Head] =
    List(NormalHead(name, params))

  // check whether current algorithm head is for equation functions.
  def isEquation(elem: Element): Boolean = elem.tagName == "emu-eqn"

  // equation head
  def getEquationHead(name: String, params: List[Param]): List[Head] =
    getNormalHead(name, params)

  // check whether current algorithm head is for syntax directed functions.
  def isCoreSyntax(prev: Element): Boolean = prev.tagName == "emu-grammar"
  def isRegexpSyntax(prev: Element): Boolean =
    prev.tagName == "p" &&
      prev.text.startsWith("The production") &&
      !getElems(prev, "emu-grammar").isEmpty
  def isSyntaxDirected(prev: Element): Boolean =
    isCoreSyntax(prev) || isRegexpSyntax(prev)

  // get with parameters
  def getWithParams(headElem: Element): List[Param] = {
    val text = getText(headElem, false)
    val isParams = ".*[wW]ith (optional |zero or more )?(parameter|argument).*".r.matches(text)
    if (!isParams) Nil else {
      val optionalParamText: String = optionalParamPattern.findFirstIn(text).getOrElse("")
      val optionalParams: List[String] = withParamPattern.findAllMatchIn(optionalParamText).toList.map(trimParam)
      val restParamText: String = restParamPattern.findFirstIn(text).getOrElse("")
      val restParams: List[String] = withParamPattern.findAllMatchIn(restParamText).toList.map(trimParam)
      val normalParams: List[String] = withParamPattern.findAllMatchIn(text).toList.map(trimParam) diff (optionalParams ++ restParams)
      normalParams.map(Param(_, Param.Kind.Normal)) ++
        optionalParams.map(Param(_, Param.Kind.Optional)) ++
        restParams.map(Param(_, Param.Kind.Variadic))
    }
  }

  // syntax directed head
  def getSyntaxDirectedHead(
    tempName: String,
    isStatic: Boolean,
    headElem: Element,
    prev: Element
  )(implicit grammar: Grammar, lines: Array[String]): List[Head] = {
    // fix name of regexp syntax -> always evaluation
    var name = tempName
    if (isRegexpSyntax(prev)) name = "Evaluation"

    // syntax-directed algorithms
    val nameMap = grammar.nameMap
    val idxMap = grammar.idxMap

    // with parameters
    val withParams = getWithParams(headElem)

    // extract emu-grammar
    val target =
      if (isCoreSyntax(prev)) prev
      else getElems(prev, "emu-grammar")(0)

    // algorithm body
    val body = getRawBody(target).map(unescapeHtml(_)).toList

    // get head
    for {
      code <- splitBy(body, "")
      prod = ProductionParser(code)
      lhsName = prod.lhs.name
      rhs <- prod.rhsList
      rhsName <- rhs.allNames
      syntax = lhsName + ":" + rhsName
      (i, j) = idxMap(syntax)
      rhsParams = Param.fromGrammar(grammar, lhsName, i)
      needPrefix = !(rhs.getNTs.map(_.name) contains lhsName)
    } yield SyntaxDirectedHead(
      lhsName, i, j, rhsParams,
      name, isStatic, withParams, needPrefix
    )
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

  // environment method head
  def getEnvMethodHead(
    name: String,
    prev: Element,
    elem: Element,
    params: List[Param]
  )(implicit lines: Array[String]): List[Head] = {
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
  }

  // check whether current algorithm head is for object
  // internal method functions.
  def isObjMethod(name: String): Boolean =
    name.startsWith("[[") && name.endsWith("]]")

  // object method head
  def getObjMethodHead(
    name: String,
    prev: Element,
    elem: Element,
    params: List[Param]
  )(implicit lines: Array[String]): List[Head] = {
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
  }

  // get sequential <p> tagged text
  def getText(elem: Element, toPrev: Boolean = true): String = {
    var text = ""
    var keep = true
    val prevs = toArray {
      if (toPrev) elem.previousElementSiblings
      else elem.nextElementSiblings
    }
    for (prev <- prevs if keep) {
      if (prev.tagName == "p") text += prev.text
      else keep = false
    }
    text
  }

  // check whether current algorithm head is for built-in functions.
  private val absOpPattern = ".*abstract operation.*".r
  private val argPattern = "An Arg[GS]etter function is.*".r
  private val builtinPattern = ".*is an anonymous built-in function.*".r
  private val manualBuiltins = Set("DefaultConstructorFunctions")
  def isBuiltin(
    name: String,
    elem: Element,
    builtinLine: Int
  )(implicit lines: Array[String]): Boolean = {
    val text = getText(elem)
    val rangeCheck = getRange(elem) match {
      case None => false
      case Some((start, _)) =>
        start >= builtinLine && !absOpPattern.matches(text)
    }
    val prevCommentCheck =
      !argPattern.matches(text) && builtinPattern.matches(text)
    val manualCheck = manualBuiltins contains name
    rangeCheck || prevCommentCheck || manualCheck
  }

  // builtin head
  def getBuiltinHead(
    name: String,
    headElem: Element,
    params: List[Param]
  ): List[Head] = {
    val newParams =
      if (params.isEmpty) getWithParams(headElem)
      else params
    List(BuiltinHead(parseAll(ref, name).get, newParams))
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

  // this value head
  def getThisValueHead(prev: Element) = {
    // thisValue
    val prevText = prev.text
    // NOTE name and params always exist
    val name = thisValuePattern.findAllIn(prevText).toList.head
    val params = List(Param(firstParam(prevText).get))
    List(NormalHead(name, params))
  }

  // check whether algorithm is comparison
  def isComparison(name: String): Boolean = name.endsWith("Comparison")

  // check validity of names
  def nameCheck(name: String): Boolean = namePattern.matches(name)

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

  // manual parameters
  lazy val manualParams: Map[String, List[Param]] = Map(
    "AbstractEqualityComparison" -> COMP_PARAMS,
    "StrictEqualityComparison" -> COMP_PARAMS,
    "AbstractRelationalComparison" -> REL_COMP_PARAMS,
    "Await" -> toParams(AWAIT_PARAM),
  )
}
trait HeadParsers extends Parsers {
  import ir._
  import Param.Kind._

  lazy val name = "[a-zA-Z]+".r
  lazy val field = (
    "." ~> name ^^ { EStr(_) } |
    "[" ~ "@@" ~> name <~ "]" ^^ { x => ir.Expr("SYMBOL_" + x) }
  )
  lazy val base = (
    "%" ~> name <~ "%" |
    "get" ~> name |
    "set" ~> name |
    name
  )
  lazy val ref = base ~ rep(field) ^^ {
    case b ~ fs => fs.foldLeft[Ref](RefId(Id(b))) {
      case (b, f) => RefProp(b, f)
    }
  }
  lazy val param =
    "_[a-zA-Z0-9]+_".r ^^ { case s => s.substring(1, s.length - 1) }
  lazy val params: Parser[List[Param]] = (
    "[" ~ opt(",") ~> param ~ params <~ "]" ^^ {
      case x ~ ps => Param(x, Optional) :: ps.map(_.toOptional)
    } |
    opt(",") ~ "..." ~> param ~ params ^^ { case x ~ ps => Param(x, Variadic) :: ps } |
    opt(",") ~> param ~ params ^^ { case x ~ ps => Param(x) :: ps } |
    "" ^^^ Nil
  )
  lazy val paramList = (
    "(" ~> params <~ ")" |
    "(" ~ repsep(param | "…", ",") ~ ")" ^^^ Nil
  )
}
