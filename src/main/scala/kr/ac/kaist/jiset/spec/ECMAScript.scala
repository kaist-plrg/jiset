package kr.ac.kaist.jiset.spec

import kr.ac.kaist.ires.ir
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.algorithm._
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup._
import org.jsoup.nodes._
import scala.util.matching.Regex

// ECMASCript specifications
case class ECMAScript(grammar: spec.Grammar, algos: List[Algo])

object ECMAScript {
  def apply(filename: String): ECMAScript = {
    val src = readFile(filename)
    // source lines
    val lines = src.split(LINE_SEP)
    // parse html
    val document = Jsoup.parse(src)
    // parse grammar
    val grammar = parseGrammar(lines, document)
    // parse algorithm
    val algos = parseAlgo(lines, document)
    // wrap grammar, algos
    ECMAScript(grammar, algos)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // helper
  ////////////////////////////////////////////////////////////////////////////////
  // get ranges of mathed pattern from spec.html
  def getRanges(
    lines: Array[String],
    pattern: (Regex, Regex)
  ): Array[(Int, Int)] = {
    val (entryPattern, exitPattern) = pattern
    var rngs = Vector[(Int, Int)]()
    var entries = List[Int]()
    for ((line, i) <- lines.zipWithIndex) line match {
      case entryPattern() => entries ::= i + 1
      case exitPattern() =>
        if (!entries.isEmpty) {
          rngs :+= (entries.head, i)
          entries = entries.tail
        }
      case _ =>
    }
    rngs.toArray
  }

  def getChunks(
    lines: Array[String],
    pattern: (Regex, Regex)
  ): Array[List[String]] = {
    val rngs = getRanges(lines, pattern)
    val chunks = rngs.map { case (s, e) => lines.slice(s, e).toList }
    chunks
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
  // regexp for `emu-grammar` tagged elements
  val grammarPattern =
    ("""[ ]*<emu-grammar type="definition">""".r, "[ ]*</emu-grammar.*>".r)

  // parse spec.html to Grammar
  def parseGrammar(lines: Array[String], document: Document): spec.Grammar = {
    // split codes by empty string
    // ex) split(List("a", "b", "", "c", "d")) = List(List("a", "b"), List("c", "d"))
    def split(prods: List[String]): List[List[String]] = {
      prods.span(_ != "") match {
        case (prod, _ :: remain) => prod :: split(remain)
        case (prod, Nil) => List(prod)
      }
    }

    // get lexical grammar list
    val lexElem = document.getElementById("sec-lexical-grammar")
    val lexProdElems = lexElem.getElementsByTag("emu-prodref").toArray(Array[Element]())
    val lexNames = lexProdElems.toList.map(_.attributes().get("name"))
    // println(s"# of lex: ${lexNames.length}")

    // codes for `emu-grammar` tagges elements
    val prodStrs = getChunks(lines, grammarPattern).toList.flatMap(split _)
    val prods = (for {
      prodStr <- prodStrs
      prod <- ProductionParser(prodStr)
    } yield prod).toList

    // TODO handle spec.html:41708~41737 additional syntax
    // partition prods
    val (lexProds, nonLexProds) =
      prods.partition(p => lexNames.contains(p.lhs.name))

    // for debug
    // (prodStrs zip prods).foreach {
    //   case (st, pr) => st.foreach(println _); println(pr.lhs); pr.rhsList.foreach(println _);
    // }
    // println(s"# of prodStrs: ${prodStrs.length}")
    // println(s"# of prods: ${prods.length}")
    // lexNames.foreach(println _)
    // println("################################################################################")
    // lexProds.foreach(p => println(p.lhs.name))

    println(s"# of lexical production: ${lexProds.length}")
    println(s"# of non-lexical production: ${nonLexProds.length}")

    spec.Grammar(lexProds, nonLexProds)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // algorithm
  ////////////////////////////////////////////////////////////////////////////////
  // regexp for `emu-alg` tagged elements
  val algoPattern = ("[ ]*<emu-alg.*>".r, "[ ]*</emu-alg.*>".r)

  // parse spec.html to Algo
  def parseAlgo(lines: Array[String], document: Document): List[Algo] = {
    // HTML elements with `emu-alg` tags
    val elems = document.getElementsByTag("emu-alg").toArray(Array[Element]())

    // codes for `emu-alg` tagged elements
    val codes = getChunks(lines, algoPattern)
    println(s"# total: ${codes.size}")

    // algorithms
    val (atime, algos) = time((for {
      (elem, code) <- elems zip codes
      algo <- Algo(elem, code)
    } yield algo).toList)
    println(s"# algos: ${algos.length} ($atime ms)")

    // return algos
    algos
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
