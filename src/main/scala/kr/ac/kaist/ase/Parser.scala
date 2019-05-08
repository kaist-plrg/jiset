package kr.ac.kaist.ase

import com.codecommit.gll._

object JParser extends RegexParsers {
  override protected val whitespace = """\s+""".r
  val nonterm: Parser[Nonterm] = """\$[^\s]+""".r ^^ {
    case r => Nonterm(r.substring(1))
  }
  val term: Parser[Term] = """[^\$\s][^\s]*""".r ^^ { case r => Term(r) }
  val cfgrule: Parser[(String, CFGRule)] = nonterm ~ "=>" ~ rep(nonterm | term) ^^ {
    case (Nonterm(s), _, l) => (s, CFGRule(l))
  }

  def apply(s: String): Either[String, (String, CFGRule)] =
    cfgrule(s).head match {
      case Success(value, tail) => Right(value);
      case Failure(data, tail) => Left("err")
    }
}
