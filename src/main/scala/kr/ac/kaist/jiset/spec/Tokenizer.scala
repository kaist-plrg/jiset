package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.algorithm.{ Grammar => GRToken, _ }
import kr.ac.kaist.jiset.util.Useful._
import scala.collection.mutable.Stack
import scala.util.parsing.combinator._

object Tokenizer extends RegexParsers {
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
    const | code | value | id | nt | sup | url | text // TODO grammar token

  // step parsers
  lazy val indent = number ~ "." | "*" | "<" ~ rep(char)
  lazy val step: Parser[List[Token]] = indent ~> rep(token)

  // parsing

  // get tokens
  val TAB = 2
  def getTokens(code: List[String]): List[Token] = {
    val initial = getIndent(code(0))
    var prev = -1
    var tokens = Vector[Token]()
    val nexts = Stack[Next]()
    var k = 0
    def next: Next = { val res = Next(k); k += 1; res }
    code.foreach(line => {
      var indent = getIndent(line)
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
      tokens ++= parse(step, line).get
    })
    tokens :+= next
    while (prev > initial) { prev -= TAB; tokens ++= List(Out, nexts.pop) }
    tokens.toList
  }
}
