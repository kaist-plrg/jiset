package kr.ac.kaist.jiset.spec

import scala.util.parsing.combinator._

// common parsers
trait Parsers extends RegexParsers {
  lazy val word = "\\w+".r
  lazy val params: Parser[List[String]] = "[" ~> repsep(pWord, ",") <~ "]"
  lazy val cWord = "[A-Z]\\w+".r
  lazy val pWord = "[?|\\+|~]*\\w+".r
}

// Token parsers
trait TokenParsers extends Parsers {
  // butnot tokens
  lazy val butnot = (nt <~ ("but not" <~ opt("one of"))) ~ rep(token <~ opt("or")) ^^ {
    case base ~ cases => ButNot(base, cases)
  }

  // lookahead tokens
  lazy val containsSymbol = ("!=" | "<!" | "==" | "<") ^^ {
    case "!=" | "<!" => false
    case "==" | "<" => true
    case _ => ??? // impossible
  }
  lazy val laElem: Parser[List[Token]] = rep(token)
  lazy val laList = opt("{") ~> repsep(laElem, ",") <~ opt("}")
  lazy val lookahead = "[lookahead " ~> containsSymbol ~ laList <~ "]" ^^ {
    case b ~ cases => Lookahead(b, cases)
  }

  // terminals
  lazy val term = "`" ~> ("[^`]+".r | "`") <~ "`" ^^ { Terminal(_) }

  // non-terminals
  lazy val nt = cWord ~ opt(params) ~ opt("?") ^^ {
    case n ~ Some(args) ~ Some(_) => NonTerminal(n, args, true)
    case n ~ Some(args) ~ None => NonTerminal(n, args, false)
    case n ~ None ~ Some(_) => NonTerminal(n, Nil, true)
    case n ~ None ~ None => NonTerminal(n, Nil, false)
  }

  // tokens
  lazy val token: Parser[Token] = butnot | lookahead | nt | term
}

// Rhs parsers
trait RhsParsers extends TokenParsers {
  lazy val rhs: Parser[Rhs] = opt(constraints) ~ rep(token) ^^ {
    case Some(cond) ~ tokens => Rhs(tokens, cond)
    case None ~ tokens => Rhs(tokens, "")
  }
  lazy val constraints = "[" ~> "[+|~]".r ~ word <~ "]" ^^ {
    case "+" ~ c => "p" + c
    case "~" ~ c => "!p" + c
    case _ => ??? // impossible
  }
}

// Lhs parsers
trait LhsParsers extends Parsers {
  lazy val lhs: Parser[Lhs] = word ~ opt(params) <~ "[:]+".r ^^ {
    case n ~ None => Lhs(n, Nil)
    case n ~ Some(params) => Lhs(n, params)
  }
}

// Production parsers
trait ProductionParsers extends LhsParsers with RhsParsers {
  def parse(lines: List[String]): Production = lines match {
    case lhsStr :: rhsStrList => {
      val lhs = Lhs(lhsStr)
      val oneOf = lhsStr.trim.endsWith("one of")
      // TODO create rhsList
      val rhsList = rhsStrList.map(Rhs(_))
      // TODO handle oneOf
      Production(lhs, rhsList)
    }
    case Nil => ??? // impossible
  }
}
