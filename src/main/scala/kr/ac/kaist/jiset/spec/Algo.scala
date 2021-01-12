package kr.ac.kaist.jiset.spec

import kr.ac.kaist.ires.ir
import kr.ac.kaist.jiset.DEBUG
import kr.ac.kaist.jiset.algorithm._
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._
import scala.collection.mutable.Stack
import scala.util.parsing.combinator._

// ECMASCript abstract algorithms
case class Algo(
    name: String,
    params: List[String],
    body: ir.Inst
) {
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
    s"$name (${params.mkString(", ")}) ${ir.beautify(body)}"
}

object Algo extends RegexParsers {
  // get algorithms
  def apply(elem: Element, code: List[String]): Option[Algo] = optional({
    if (elem.previousElementSibling.tag.toString == "emu-grammar")
      error("[TODO] syntax-directed algorithm")

    val (name, params) = getHead(elem)
    val body = getBody(code)
    Algo(name, params, body)
  }, x => if (DEBUG) println(x))

  // get names and parameters
  val paramPattern = "[^\\s,()\\[\\]]+".r
  val namePattern = "[a-zA-Z]+".r // TODO extend
  val prefixPattern = ".*Semantics:".r
  def nameCheck(name: String): Boolean =
    namePattern.matches(name) && !ECMAScript.PREDEF.contains(name)
  def getHead(elem: Element): (String, List[String]) = {
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
    (name, params)
  }

  // get tokens
  val TAB = 2
  def getTokens(code: List[String]): List[Token] = {
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
    tokens.toList
  }
  object Tokenizer {
    // basic parsers
    lazy val name = "[_-a-zA-Z]+".r
    lazy val word = "[a-zA-Z]+".r
    lazy val number = "[0-9]+".r
    lazy val char = "\\S".r

    // token parsers
    def wrap(x: String) = s"[$x][^ $x]+[$x]".r ^^ {
      case s => s.substring(x.length, s.length - x.length)
    }
    lazy val const = wrap("~") ^^ { Const(_) }
    lazy val code = wrap("`") ^^ { Code(_) }
    lazy val value = wrap("*") ^^ { Value(_) }
    lazy val id = wrap("_") ^^ { Id(_) }
    lazy val nt = wrap("|") ^^ { Nt(_) }
    lazy val sup = "<sup>" ~> "[^<]*".r <~ "</sup>" ^^ {
      case s => Sup(Step(parseAll(rep(token), s).getOrElse(Nil)))
    }
    lazy val url = "<a[^>]*>".r ~> "[^<]*".r <~ "</a>" ^^ { Url(_) }
    lazy val text = (word | number | char) ^^ { Text(_) }
    lazy val token: Parser[Token] =
      const | code | value | id | nt | sup | url | text // TODO grammar

    // step parsers
    lazy val indent = number ~ "." | "*" | "<" ~ rep(char)
    lazy val step: Parser[List[Token]] = indent ~> rep(token)

    // parsing
    def apply(str: String): List[Token] = parse(step, str).get
  }

  // get body instructions
  def getBody(code: List[String]): ir.Inst = {
    val tokens = getTokens(code)
    GeneralAlgoCompiler.compile(tokens)
  }
}
