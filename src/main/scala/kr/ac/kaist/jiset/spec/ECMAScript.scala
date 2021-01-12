package kr.ac.kaist.jiset.spec

import kr.ac.kaist.ires.ir
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.algorithm._
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup._
import org.jsoup.nodes._
import scala.collection.mutable.Stack
import scala.util.parsing.combinator._

// ECMASCript specifications
case class ECMAScript(algos: List[Algo])

object ECMAScript extends JavaTokenParsers with RegexParsers {
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

  def apply(filename: String): ECMAScript = {
    val src = readFile(filename)

    // source lines
    val lines = src.split(LINE_SEP)

    // HTML nodes with `emu-alg` tags
    val document = Jsoup.parse(src)
    val nodes = document.getElementsByTag("emu-alg").toArray(Array[Element]())

    // codes for `emu-alg` tagged nodes
    val rngs = getRanges(lines)
    val codes = rngs.map { case (s, e) => lines.slice(s, e).toList }

    println(s"# total: ${codes.size}")

    // algorithms
    val (atime, algos) = time((for {
      (node, code) <- nodes zip codes
      algo <- getAlgo(node, code)
    } yield algo).toList)

    println(s"# algos: ${algos.length} ($atime ms)")

    // instructions
    val (itime, insts) = time(for {
      algo <- algos
      inst = GeneralAlgoCompiler.compile(algo.tokens)
      if isComplete(inst)
    } yield inst)

    println(s"# insts: ${insts.length} ($itime ms)")

    ECMAScript(algos)
  }

  // get ranges of each `emu-alg` tagged nodes
  val entryPattern = "[ ]*<emu-alg.*>".r
  val exitPattern = "[ ]*</emu-alg.*>".r
  def getRanges(lines: Array[String]): Array[(Int, Int)] = {
    var rngs = Vector[(Int, Int)]()
    var entries = List[Int]()
    for ((line, i) <- lines.zipWithIndex) line match {
      case entryPattern() => entries ::= i + 1
      case exitPattern() =>
        rngs :+= (entries.head, i)
        entries = entries.tail
      case _ =>
    }
    rngs.toArray
  }

  // tokenizer
  object Tokenizer {
    // basic parsers
    lazy val name = "[_-a-zA-Z]+".r
    lazy val word = "[a-zA-Z]+".r
    lazy val number = "[0-9]+".r
    lazy val any = "\\S".r

    // token parsers
    def wrap(str: String) = s"$str$any+$str".r ^^ {
      case s => s.substring(1, s.length - 1)
    }
    lazy val const = wrap("~") ^^ { Const(_) }
    lazy val code = wrap("`") ^^ { Code(_) }
    lazy val value = wrap("\\*") ^^ { Value(_) }
    lazy val id = wrap("_") ^^ { Id(_) }
    lazy val nt = wrap("\\|") ^^ { Nt(_) }
    lazy val sup = "<sup>" ~> "[^<]*".r <~ "</sup>" ^^ {
      case s => Sup(Step(parseAll(rep(token), s).getOrElse(Nil)))
    }
    lazy val url = "<a[^>]*>".r ~> "[^<]*".r <~ "</a>" ^^ { Url(_) }
    lazy val text = (word | number | any) ^^ { Text(_) }
    lazy val token: Parser[Token] =
      const | code | value | id | nt | sup | url | text // TODO grammar

    // step parsers
    lazy val indent = number ~ "." | "*" | "<" ~ rep(any)
    lazy val step: Parser[List[Token]] = indent ~> rep(token)

    // parsing
    def apply(str: String): List[Token] = parse(step, str).get
  }

  // get algorithms
  val TAB = 2
  def getAlgo(node: Element, code: List[String]): Option[Algo] = try {
    val initial = getIndent(code(0))
    var prev = -1
    var tokens = Vector[Token]()
    val nexts = Stack[Next]()
    var k = 0
    def next: Next = { val res = Next(k); k += 1; res }
    code.foreach(step => {
      var indent = getIndent(step)
      if (prev != -1) {
        if (indent > prev) {
          tokens :+= In
          nexts.push(next)
        } else if (indent < prev) {
          tokens :+= next
          while (prev > indent) { prev -= TAB; tokens ++= List(Out, nexts.pop) }
        } else tokens :+= next
      }
      prev = indent
      tokens ++= Tokenizer(step)
    })
    tokens :+= next
    while (prev > initial) { prev -= TAB; tokens ++= List(Out, nexts.pop) }

    Some(Algo(tokens.toList))
  } catch {
    case e: Throwable => None
  }
  def getIndent(str: String): Int = "[ ]+".r.findFirstIn(str).fold(-1)(_.length)
}
