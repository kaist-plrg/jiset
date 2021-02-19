package kr.ac.kaist.jiset.parser

import kr.ac.kaist.jiset.parser.algorithm.{ AlgoParser, HeadParser }
import kr.ac.kaist.jiset.parser.grammar.GrammarParser
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.{ ECMA262_DIR, SPEC_HTML, LINE_SEP, RECENT_VERSION }
import org.jsoup._
import org.jsoup.nodes._
import scala.collection.mutable.Stack
import spray.json._

object ECMAScriptParser {
  def apply(
    version: String,
    query: String,
    useCount: Boolean,
    detail: Boolean
  ): ECMAScript =
    apply(preprocess(version), query, useCount, detail)
  def apply(
    input: (Array[String], Document, Region),
    query: String,
    useCount: Boolean,
    detail: Boolean
  ): ECMAScript = {
    implicit val (lines, document, region) = input

    // parse grammar
    implicit val grammar = parseGrammar

    // parse algorithm
    val algos =
      if (query == "") parseAlgo(document, useCount, detail)
      else getElems(document, query).toList.flatMap(parseAlgo(_, useCount, detail))

    // intrinsic object names
    val intrinsics = parseIntrinsic

    // well-known symbols
    val symbols = parseSymbol

    // get aoids
    val aoids = parseAoids

    // section hierarchy
    val section = parseSection

    ECMAScript(grammar, algos, intrinsics, symbols, aoids, section)
  }
  ////////////////////////////////////////////////////////////////////////////////
  // helper
  ////////////////////////////////////////////////////////////////////////////////
  // preprocess for spec.html
  def preprocess(version: String = RECENT_VERSION): (Array[String], Document, Region) = {
    val rawVersion = getRawVersion(version)
    val cur = currentVersion(ECMA262_DIR)
    val src = if (cur == rawVersion) readFile(SPEC_HTML) else {
      changeVersion(rawVersion, ECMA262_DIR)
      val src = readFile(SPEC_HTML)
      changeVersion(cur, ECMA262_DIR)
      src
    }
    val lines = unescapeHtml(src).split(LINE_SEP)
    val cutted = dropNoScope(attachLines(src.split(LINE_SEP))).mkString(LINE_SEP)
    val document = Jsoup.parse(cutted)
    val region = Region(document)
    (lines, document, region)
  }

  // attach line numbers
  val startPattern = """(\s*<)([-a-z]+)([^<>]*(<[^<>]*>[^<>]*)*>[^<>]*)""".r
  val endPattern = """\s*</([-a-z]+)>\s*""".r
  val pairPattern = """(\s*<)([-a-z]+)([^<>]*(<[^<>]*>[^<>]*)*>.*</[-a-z]+>\s*)""".r
  val ignoreTags = Set("meta", "link", "style", "br", "img", "li", "p")
  def attachLines(lines: Array[String]): Array[String] = {
    val tagStack = Stack[(String, Int)]()
    var rngs = Map[Int, Int]()
    lines.zipWithIndex.foreach {
      case (line, k) => line match {
        case startPattern(_, tag, _, _) if !ignoreTags.contains(tag) =>
          tagStack.push((tag, k))
        case endPattern(tag) if !ignoreTags.contains(tag) =>
          val (expected, start) = tagStack.pop
          if (expected != tag)
            error(s"[ECMAScript.attachLines] $tag not matched with $expected")
          rngs += start -> (k + 1)
        case _ =>
      }
    }
    lines.zipWithIndex.map(_ match {
      case (line @ startPattern(pre, tag, post, _), start) if !ignoreTags.contains(tag) =>
        rngs.get(start).fold(line)(end => s"$pre$tag s=$start e=$end$post")
      case (line @ pairPattern(pre, tag, post, _), start) if !ignoreTags.contains(tag) =>
        s"$pre$tag s=$start e=${start + 1}$post"
      case (line, _) => line
    })
  }

  // drop lines not in the scope of extraction
  val startLinePattern = "<emu-clause.*sec-ecmascript-data-types-and-values.*>".r
  val endLinePattern = "<emu-annex.*annexB.*>".r
  def dropNoScope(lines: Array[String]): Array[String] = {
    val startLineNum = lines.indexWhere(startLinePattern matches _)
    val endLineNum = lines.indexWhere(endLinePattern matches _)
    if (startLineNum == -1) error("[ECMAScript.dropAppendix] not found start line.")
    if (endLineNum == -1) error("[ECMAScript.dropAppendix] not found end line.")
    lines.slice(startLineNum, endLineNum)
  }

  // parse table#id > tag
  private def parseTable(
    query: String
  )(implicit document: Document): Array[Array[Element]] = {
    getElems(document, s"$query table > tbody > tr")
      .map(row => toArray(row.children))
  }

  private def getTargetElems(
    target: Element,
    detail: Boolean = false
  )(
    implicit
    lines: Array[String],
    document: Document
  ): Array[Element] = {
    // HTML elements with `emu-alg` tags
    // `emu-alg` that reside inside `emu-note` should be filtered out
    val emuAlgs = getElems(target, "emu-alg").filter(elem => elem.parent().tagName() != "emu-note")

    // HTML elements for Early Error
    val earlyErrors = for {
      parentElem <- getElems(target, "emu-clause[id$=early-errors]")
      elem <- getElems(parentElem, "ul")
    } yield elem

    // HTML elements for table algorithms, with "Argument Type"
    val typeTableAlgs = getElems(target, "emu-table:contains(Argument Type)")

    // HTML elements with `emu-eqn` tags
    val emuEqns = getElems(target, "emu-eqn[aoid]")

    // target elements
    val elems = emuAlgs ++ earlyErrors ++ typeTableAlgs ++ emuEqns

    if (detail) {
      println(s"# algorithm elements: ${elems.size}")
      println(s"  - <emu-alg>: ${emuAlgs.size}")
      println(s"  - Early Error: ${earlyErrors.size}")
      println(s"  - <emu-table> with header Arguments Type : ${typeTableAlgs.size}")
      println(s"  - <emu-eqn>: ${emuEqns.size}")
    }
    elems
  }
  def getSecId(elem: Element): String =
    toArray(elem.parents).find(_.tagName == "emu-clause").get.id

  ////////////////////////////////////////////////////////////////////////////////
  // grammar
  ////////////////////////////////////////////////////////////////////////////////
  // parse spec.html to Grammar
  def parseGrammar(version: String): (Grammar, Document) = {
    implicit val (lines, document, _) = preprocess(version)
    (parseGrammar, document)
  }
  def parseGrammar(implicit lines: Array[String], document: Document): Grammar =
    GrammarParser(lines, document)

  ////////////////////////////////////////////////////////////////////////////////
  // algorithm
  ////////////////////////////////////////////////////////////////////////////////
  def parseHeads(
    targetSections: Array[String] = Array(),
    detail: Boolean = false
  )(
    implicit
    lines: Array[String],
    grammar: Grammar,
    document: Document,
    region: Region
  ): (Map[String, Name], List[(Element, List[Head])]) = {
    var res: List[(Element, List[Head])] = List()
    var secIds = (for {
      elem <- getTargetElems(document)
      secId = getSecId(elem)
      // TODO handle exception
      heads = HeadParser(elem)
      if !heads.isEmpty
    } yield {
      // if elem is in targets, add it to result
      if (targetSections.contains(secId)) res :+= (elem, heads)
      secId -> Name(heads.head.name)
    }).toMap

    (secIds, res)
  }

  // parse spec.html to Algo
  def parseAlgo(
    target: Element,
    useCount: Boolean,
    detail: Boolean
  )(
    implicit
    lines: Array[String],
    grammar: Grammar,
    document: Document,
    region: Region
  ): List[Algo] = {
    // get section id of target elements
    val targetSections = getTargetElems(target).map(getSecId(_))

    // parse heads and
    val (secIds, parsedHeads) = parseHeads(targetSections, detail)

    // algorithms
    val (atime, passed) = time(for {
      parsedHead <- parsedHeads
      algos = AlgoParser(parsedHead, secIds, useCount, detail)
      if !algos.isEmpty
    } yield algos)
    if (detail) println(s"# successful algorithm parsing: ${passed.size} ($atime ms)")
    val algos = passed.toList.flatten

    // return algos
    algos
  }

  // parse well-known intrinsic object names
  def parseIntrinsic(implicit document: Document): Set[String] = {
    val table = parseTable("#sec-well-known-intrinsic-objects")
    (for (k <- (1 until table.size)) yield table(k)(0).text.replace("%", "")).toSet
  }

  // parse well-known symbol names
  def parseSymbol(implicit document: Document): Set[String] = {
    val table = parseTable("#sec-well-known-symbols")
    (for (k <- (1 until table.size)) yield table(k)(0).text.replace("@@", "")).toSet
  }

  // get aoids
  def parseAoids(implicit document: Document): Set[String] = {
    toArray(document.select("[aoid]")).map(elem => {
      "[/\\s]".r.replaceAllIn(elem.attr("aoid"), "")
    }).toSet
  }

  // parse section hierarchy
  def parseSection(implicit document: Document): Section = Section(document.body)
}
