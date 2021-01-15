package kr.ac.kaist.jiset.spec

import kr.ac.kaist.ires.ir
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.{ ECMA262_DIR, SPEC_HTML, LINE_SEP, RECENT_VERSION }
import org.jsoup._
import org.jsoup.nodes._
import scala.collection.mutable.Stack

// ECMASCript specifications
case class ECMAScript(
    grammar: Grammar,
    algos: List[Algo],
    intrinsic: Set[String]
)

object ECMAScript {
  def apply(version: String, query: String, detail: Boolean): ECMAScript = {
    implicit val (lines, document) = preprocess(version)
    implicit val grammar = parseGrammar
    // parse algorithm
    val algos =
      if (query == "") parseAlgo(document, detail)
      else getElems(document, query).toList.flatMap(parseAlgo(_, detail))
    // intrinsic object names
    val intrinsic = parseInstrinsic

    // wrap grammar, algos
    ECMAScript(grammar, algos, intrinsic)
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
    val src = version match {
      case `RECENT_VERSION` | "recent" => readFile(SPEC_HTML)
      case _ =>
        val cur = currentVersion(ECMA262_DIR)
        changeVersion(version, ECMA262_DIR)
        val src = readFile(SPEC_HTML)
        changeVersion(cur, ECMA262_DIR)
        src
    }
    val target = getTarget(version)
    println(s"version: $target")
    val lines = attachLines(dropAppendix(src.split(LINE_SEP)))
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

  // drop appendix lines
  def dropAppendix(lines: Array[String]): Array[String] = {
    val appendixLineNum = lines.indexWhere("<emu-annex.*annexB.*>".r matches _)
    if (appendixLineNum == -1) error("[ECMAScript.dropAppendix] not found Appendix.")
    lines.slice(0, appendixLineNum)
  }

  // check if inst has ??? or !!!
  def isComplete(inst: ir.Inst): Boolean = {
    var complete = true
    object Walker extends ir.UnitWalker {
      override def walk(expr: ir.Expr): Unit = expr match {
        case ir.ENotYetModeled(_) | ir.ENotSupported(_) => complete = false
        case _ => super.walk(expr)
      }
    }
    Walker.walk(inst)
    complete
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
        case ((false, true), prev) => prev.tag.toString match {
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

    println(s"# of lexical production: ${lexProds.length}")
    println(s"# of non-lexical production: ${nonLexProds.length}")

    Grammar(lexProds, nonLexProds)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // algorithm
  ////////////////////////////////////////////////////////////////////////////////
  // parse spec.html to Algo
  def parseAlgo(target: Element, detail: Boolean = false)(
    implicit
    lines: Array[String],
    grammar: Grammar
  ): List[Algo] = {
    // HTML elements with `emu-alg` tags
    val emuAlgs = getElems(target, "emu-alg")

    // HTML elements for Early Error
    val earlyErrors = for {
      parentElem <- getElems(target, "emu-clause[id$=early-errors]")
      elem <- getElems(parentElem, "ul")
    } yield elem

    val elems = emuAlgs ++ earlyErrors
    println(s"# algorithm elements: ${elems.size}")
    println(s"  - <emu-alg>: ${emuAlgs.size}")
    println(s"  - Early Error: ${earlyErrors.size}")

    // algorithms
    val (atime, passed) = time(for {
      elem <- elems
      algos = Algo.parse(elem, detail)
      if !algos.isEmpty
    } yield algos)
    println(s"# successful algorithm parsing: ${passed.size}")
    val algos = passed.toList.flatten
    println(s"# algorithms: ${algos.length} ($atime ms)")

    // return algos
    algos
  }

  // parse 6.1.7.4 Well-known intrinsic objects table, return list of intrinsic object names
  def parseInstrinsic(implicit document: Document): Set[String] = {
    val intrinsicTableRows: Array[Element] = getElems(document, "emu-clause[id=sec-well-known-intrinsic-objects] table > tbody > tr")
    val intrinsicNames = intrinsicTableRows
      .map(row => getElems(row, "td"))
      .filter(!_.isEmpty) // header row doesn't have `td`, remove it
      .map(_(0).text())
      .map("INTRINSIC_" + _.replaceAll("%", ""))
      .toSet
    println(s"# intrinsic object names: ${intrinsicNames.size}")
    intrinsicNames
  }

  // pre-defined global identifiers
  val PREDEF = Set(
    // Completion-related ECMAScript internal algorithms
    "NormalCompletion", "ThrowCompletion", "ReturnIfAbrupt", "Completion",
    // ECMAScript type getter algorithm
    "Type",
    // JISET specific internal algorithms
    "IsAbruptCompletion", "WrapCompletion",
    // JISET specific global variables
    "GLOBAL_agent"
  )
}
