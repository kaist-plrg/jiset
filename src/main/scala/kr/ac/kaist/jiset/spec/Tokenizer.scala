package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.algorithm.{ Grammar => GRToken, _ }
import kr.ac.kaist.jiset.util.Useful._
import scala.collection.mutable.Stack
import scala.util.parsing.combinator._

class Tokenizer(implicit grammar: Grammar) extends ProductionParsers {
  // step parsers
  lazy val step: Parser[List[Token]] = {
    lazy val name = "[_-a-zA-Z]+".r
    lazy val word = "[a-zA-Z]+".r
    lazy val number = "[0-9]+".r
    lazy val char = "\\S".r

    // token parsers
    def wrap(x: String) = s"[$x][^ $x]+[$x]".r ^^ {
      case s => s.substring(x.length, s.length - x.length)
    }
    lazy val gram = "<emu-grammar>" ~> lhs ~ rhs <~ "</emu-grammar>" ^^ {
      case Lhs(lhsName, _) ~ rhs => {
        val caseName = norm(s"${lhsName}:${rhs.names.head}")
        grammar.idxMap.get(caseName) match {
          case Some((idx, _)) => {
            val subs = rhs.tokens.flatMap {
              case NonTerminal(n, _, _) => Some(n)
              case _ => None
            }
            val grToken = GRToken(s"${lhsName}${idx}", subs)
            grToken
          }
          case None => error("`spec/Tokenizer`: no such `caseName` in index map")
        }
      }
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
      gram | const | code | value | id | nt | sup | url | text

    // indentation parsers
    lazy val indent = number ~ "." | "*" | "<" ~ rep(char)
    indent ~> rep(token)
  }

  // get tokens
  val TAB = 2
  def getTokens(code: Iterable[String]): List[Token] = {
    val initial = getIndent(code.head)
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
