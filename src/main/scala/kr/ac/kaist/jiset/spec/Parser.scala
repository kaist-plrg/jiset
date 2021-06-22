package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.spec.algorithm.{ token => atoken, _ }
import kr.ac.kaist.jiset.spec.grammar.{ token => gtoken, _ }
import kr.ac.kaist.jiset.parser.BasicParsers

// ECMAScript parser
object Parser extends Parsers

// ECMAScript parser
trait Parser[T] extends Parsers {
  def fromFile(str: String)(implicit parser: Parser[T]): T =
    fromFileWithParser(str, parser)
  def apply(str: String)(implicit parser: Parser[T]): T =
    parse[T](str)
}

// ECMAScript parsers
trait Parsers extends BasicParsers with ir.Parsers {
  // name
  lazy val name: Parser[String] = "[^\\[\\]\\s(),?\\.|{}<>]+".r

  // section
  implicit lazy val section: Parser[Section] = (
    string ~ (":" ~ "{" ~> rep(section) <~ "}") ^^ {
      case x ~ ss => Section(x, ss)
    }
  )

  // algorithms
  implicit lazy val algorithm: Parser[Algo] = (
    ("def" ~> head <~ "=") ~
    inst ~
    ("- id:" ~> string) ~
    ("- code:(?s).*".r)
  ) ^^ { case h ~ i ~ x ~ s => Algo(h, x, i, s.split(LINE_SEP).drop(1)) }

  // algorithm heads
  implicit lazy val head: Parser[Head] = (
    "[^( ]+".r ~ params ^^ {
      case x ~ ps => NormalHead(x, ps)
    } | "[METHOD]" ~> name ~ ("." ~> name) ~ ("(" ~> param <~ ")") ~ params ^^ {
      case b ~ f ~ r ~ ps => MethodHead(b, f, r, ps)
    } | "[SYNTAX]" ~> name ~ ("[" ~> int) ~ ("," ~> int <~ "].") ~ name ~ ("<" ~> rhs <~ ">") ~ params ^^ {
      case l ~ i ~ j ~ m ~ r ~ ps => SyntaxDirectedHead(l, i, j, r, m, ps)
    } | "[BUILTIN]" ~> ref ~ params ^^ {
      case r ~ ps => BuiltinHead(r, ps)
    }
  )

  // algorithm parameters
  lazy val params: Parser[List[Param]] = "(" ~> repsep(param, ",") <~ ")"
  implicit lazy val param: Parser[Param] = {
    import Param.Kind._
    (
      name <~ "?" ^^ { case x => Param(x, Optional) } |
      "..." ~> name ^^ { case x => Param(x, Variadic) } |
      name ^^ { case x => Param(x, Normal) }
    )
  }

  // algorithm tokens
  implicit lazy val algoTokens: Parser[List[atoken.Token]] = rep(algoToken)
  implicit lazy val algoToken: Parser[atoken.Token] = {
    import atoken._
    lazy val const = getTokenParser("const") ^^ { Const(_) }
    lazy val code = getTokenParser("code") ^^ { Code(_) }
    lazy val value = getTokenParser("value") ^^ { Value(_) }
    lazy val id = getTokenParser("id") ^^ { Id(_) }
    lazy val nt = getTokenParser("nt") ^^ { Nt(_) }
    lazy val sup = getTokenParser(
      "sup",
      rep(token) ^^ { case ts => Sup(ts) }
    )
    lazy val link = getTokenParser("link") ^^ { Link(_) }
    lazy val grtext = "[^,\\[\\]]+".r
    lazy val grammar = getTokenParser(
      "grammar",
      grtext ~ ("[" ~> repsep(grtext, ",") <~ "]") ^^ { case g ~ s => Gr(g, s) }
    )
    lazy val sub = getTokenParser(
      "sub",
      rep(token) ^^ { case ts => Sub(ts) }
    )

    lazy val next = getTokenParser("next") ^^ { s => Next(s.toInt) }
    lazy val in = getTokenParser("in") ^^^ In
    lazy val out = getTokenParser("out") ^^^ Out

    lazy val text = "\\S+".r ^^ { Text(_) }

    lazy val token: Parser[Token] = (
      const | code | value | id | nt | sup | link | grammar | sub |
      next | in | out | text
    )

    token
  }

  // get token parsers
  private def getTokenParser[T](name: String, parser: Parser[T]) =
    s"$name:{" ~> parser <~ "}"
  private def getTokenParser[T](name: String) =
    s"$name:\\{[^{}]*\\}".r ^^ { case s => s.drop(name.length + 2).dropRight(1) }

  // grammars
  implicit lazy val grammar: Parser[Grammar] = (
    (Grammar.lexicalHeader ~> prods) ~
    (Grammar.syntacticHeader ~> prods)
  ) ^^ { case ls ~ ss => Grammar(ls, ss) }

  // grammar left-hand-sides
  implicit lazy val lhs: Parser[Lhs] = (
    ident ~ opt("[" ~> repsep(ident, ",") <~ "]")
  ) ^^ { case x ~ ps => Lhs(x, ps.getOrElse(Nil)) }

  // grammar productions
  implicit lazy val prods: Parser[List[Production]] = rep(prod)
  implicit lazy val prod: Parser[Production] = (
    (lhs <~ ":") ~ ("{" ~> repsep(rhs, "|") <~ "}")
  ) ^^ { case l ~ rs => Production(l, rs) }

  // grammar right-hand-sides
  implicit lazy val rhs: Parser[Rhs] = (
    opt(rhsCond) ~ grammarTokens
  ) ^^ { case c ~ ts => Rhs(ts, c) }
  implicit lazy val rhsCond: Parser[RhsCond] = (
    "[+" ~> ident <~ "]" ^^ { case x => RhsCond(x, true) } |
    "[~" ~> ident <~ "]" ^^ { case x => RhsCond(x, false) }
  )

  // grammar tokens
  implicit lazy val grammarTokens: Parser[List[gtoken.Token]] = rep(grammarToken)
  implicit lazy val grammarToken: Parser[gtoken.Token] = {
    import gtoken._
    (
      "`[^`]+`|```".r ^^ {
        case s => Terminal(s.substring(1, s.length - 1))
      } | ("[lookahead" ~> ("<!" | "<")) ~ ("{" ~> repsep(grammarTokens, ",") <~ "}]") ^^ {
        case s ~ ts => Lookahead(s == "<", ts)
      } | "[empty]" ^^ {
        case _ => EmptyToken
      } | "[no LineTerminator here]" ^^ {
        case _ => NoLineTerminatorToken
      } | "<" ~> ident <~ ">" ^^ {
        case x => Character.fromName(x)
      } | ident ~ opt("[" ~> repsep(arg, ",") <~ "]") ~ opt("?") ^^ {
        case x ~ as ~ o => NonTerminal(x, as.getOrElse(Nil), o.isDefined)
      }
    ) ~ opt("but not" ~> repsep(grammarToken, "or")) ^^ {
        case b ~ Some(ts) => ButNot(b, ts)
        case b ~ None => b
      }
  }
  implicit lazy val arg: Parser[String] =
    ("~" | "+" | "?") ~ ident ^^ { case x ~ y => x + y }
}
