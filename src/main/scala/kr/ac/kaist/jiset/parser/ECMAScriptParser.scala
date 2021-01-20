package kr.ac.kaist.jiset.parser

import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.{ ECMA262_DIR, SPEC_HTML, LINE_SEP, RECENT_VERSION }
import org.jsoup._
import org.jsoup.nodes._
import scala.collection.mutable.Stack

object ECMAScriptParser {
  def apply(version: String, query: String, detail: Boolean): ECMAScript = {
    implicit val (lines, document) = preprocess(version)
    implicit val grammar = parseGrammar

    // region of spec.html
    implicit val region = Region(document)

    // parse algorithm
    val algos =
      if (query == "") parseAlgo(document, detail)
      else getElems(document, query).toList.flatMap(parseAlgo(_, detail))

    // intrinsic object names
    val intrinsics = parseIntrinsic

    // well-known symbols
    val symbols = parseSymbol

    // get aoids
    val aoids = parseAoids

    ECMAScript(grammar, algos, intrinsics, symbols, aoids)
  }

  def parseGrammar(version: String): Grammar = {
    implicit val (lines, document) = preprocess(version)
    parseGrammar
  }
  ////////////////////////////////////////////////////////////////////////////////
  // helper
  ////////////////////////////////////////////////////////////////////////////////
  // preprocess for spec.html
  def preprocess(version: String = RECENT_VERSION): (Array[String], Document) = {
    val rawVersion = getRawVersion(version)
    val cur = currentVersion(ECMA262_DIR)
    val src = if (cur == rawVersion) readFile(SPEC_HTML) else {
      changeVersion(rawVersion, ECMA262_DIR)
      val src = readFile(SPEC_HTML)
      changeVersion(cur, ECMA262_DIR)
      src
    }
    val lines = attachLines(dropNoScope(src.split(LINE_SEP)))
    val document = Jsoup.parse(lines.mkString(LINE_SEP))
    (lines, document)
  }

  // attach line numbers
  val startPattern = """(\s*<)([-a-z]+)([^<>]*(<[^<>]*>[^<>]*)*>[^<>]*)""".r
  val endPattern = """\s*</([-a-z]+)>\s*""".r
  val pairPattern = s"$startPattern$endPattern".r
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

  ////////////////////////////////////////////////////////////////////////////////
  // grammar
  ////////////////////////////////////////////////////////////////////////////////
  // parse spec.html to Grammar
  def parseGrammar(
    implicit
    lines: Array[String],
    document: Document
  ): Grammar = {
    // get lexical grammar list
    val lexElem = document.getElementById("sec-lexical-grammar")
    val lexProdElems = lexElem
      .getElementsByTag("emu-prodref")
      .toArray(Array[Element]())
    val lexNames = lexProdElems.toList.map(_.attributes().get("name"))

    // codes for `emu-grammar` tagges elements
    val headPattern = "h\\d".r
    val prodElems = getElems(document, "emu-grammar").filter(elem => {
      val (found, pass) = toArray(elem.previousElementSiblings).foldLeft((false, true)) {
        case ((false, true), prev) => prev.tagName match {
          case headPattern() => (true, prev.text.endsWith("Syntax"))
          case _ => (false, true)
        }
        case (res, _) => res
      }
      found && pass
    })
    val prods = (for {
      prodElem <- prodElems
      body = getRawBody(prodElem).toList
      prodStr <- splitBy(body, "")
      prod <- optional(Production(prodStr))
    } yield prod).toList

    // partition prods
    val (lexProds, nonLexProds) =
      prods.partition(p => lexNames.contains(p.lhs.name))

    Grammar(lexProds, nonLexProds)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // algorithm
  ////////////////////////////////////////////////////////////////////////////////
  // parse spec.html to Algo
  def parseAlgo(
    target: Element,
    detail: Boolean = false
  )(
    implicit
    lines: Array[String],
    grammar: Grammar,
    region: Region
  ): List[Algo] = {
    // HTML elements with `emu-alg` tags
    val emuAlgs = getElems(target, "emu-alg")

    // HTML elements for Early Error
    val earlyErrors = for {
      parentElem <- getElems(target, "emu-clause[id$=early-errors]")
      elem <- getElems(parentElem, "ul")
    } yield elem

    // HTML elements for table algorithms, with "Argument Type"
    val typeTableAlgs = getElems(target, "emu-table:contains(Argument Type)")

    // HTML elements with `emu-eqn` tags
    val emuEqns = getElems(target, "emu-eqn")
    // val emuEqns = List.empty

    val elems = emuAlgs ++ earlyErrors ++ typeTableAlgs ++ emuEqns
    if (detail) {
      println(s"# algorithm elements: ${elems.size}")
      println(s"  - <emu-alg>: ${emuAlgs.size}")
      println(s"  - Early Error: ${earlyErrors.size}")
      println(s"  - <emu-table> with header Arguments Type : ${typeTableAlgs.size}")
      println(s"  - <emu-eqn>: ${emuEqns.size}")
    }

    // algorithms
    val (atime, passed) = time(for {
      elem <- elems
      algos = AlgoParser(elem, detail)
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
}
