package NLPjse

import scala.util.Random
import scala.collection.mutable.HashMap

import com.codecommit.gll.{RegexParsers => GRegexParsers}
import scala.util.parsing.combinator.{RegexParsers}

trait GSymbol {}

trait GRule {}

trait Grammar {
  def contains(a: String): Boolean
}

sealed trait CFGSymbol extends GSymbol
case class Nonterm(a: String) extends CFGSymbol {
  override def toString = s"{$$$a}"
}
case class Term(a: String) extends CFGSymbol {
  override def toString = s"$a"
}

case class CFGRule(beta: List[CFGSymbol]) extends GRule {
  override def toString = beta.map(_.toString).reduce(_ + " " + _)
}
/*
sealed trait CNFRule extends GRule
case class NTrule(a: Nonterm, b: Nonterm, c: Nonterm) extends CNFRule
case class Trule(a: Nonterm, b: Term) extends CNFRule
case class Erule() extends CNFRule

case class CNFGrammar(non: List[String], rn: List[NTrule], rt: List[Trule], re: List[Erule], s: String) extends Grammar {
  def contains(a: List[String]) = {
    //println(a)
    val n = a.length
    val r = non.length
    if (n == 0) { re.length != 0 } else {
      val cykmat = Array.ofDim[Boolean](n, n, r)
      a.zipWithIndex.foreach {
        case (ai, i) => rt.foreach {
          case Trule(Nonterm(rj), Term(aj)) => if (ai == aj) {
            // println(s"$rj => $aj")
            cykmat(0)(i)(non.indexOf(rj)) = true
          } else ()
        }
      }
      var l = 0
      var s = 0
      var p = 0
      for (l <- 1 until n) {
        for (s <- 0 until n - l) {
          for (p <- 1 until l + 1) {
            rn.foreach {
              case NTrule(Nonterm(ra), Nonterm(rb), Nonterm(rc)) => {
                val va = non.indexOf(ra)
                val vb = non.indexOf(rb)
                val vc = non.indexOf(rc)
                if (cykmat(p - 1)(s)(vb) && cykmat(l - p)(s + p)(vc)) {
                  // println(s"$ra => $rb + $rc")
                  cykmat(l)(s)(va) = true
                } else ()
              }
            }
          }
        }
      }
      cykmat(n - 1)(0)(0)
    }
  }
}
 */
case class LLGrammar(
    non: List[String],
    r: Map[String, List[CFGRule]],
    s: String
) extends Grammar {
  var parser: InstantParser = new InstantParser(this)
  //var candidate: CandidateParser = new CandidateParser(this)
  def contains(a: String) = parser(a)
  /*def suggest(a: String) =
    candidate.candidate(a).map { case (i, j) => (i, parser.generalize(j)) }
   */
  def addGrammar(ks: String, kr: CFGRule): LLGrammar = {
    val nnon = if (non contains ks) non else non :+ ks
    val nr = r.updated(ks, r.getOrElse(ks, List()) :+ kr)
    LLGrammar(nnon, nr, s)
  }

  def pprint() = r.foreach {
    case (k, v) => {
      print(s"$k => ")
      v.foreach((i) => {
        print(i)
        print(" | ")
      })
      println("")
    }
  }
}

class InstantParser(g: LLGrammar) extends TokenParsers {

  val (r, s) = g match {
    case LLGrammar(_, r, s) => (r, s)
  }
  def lazyParser(in: String): Parser[Unit] =
    Parser[Unit]((i) => ruleParser(in)(i))

  def ruleParser(s: String): Parser[Unit] =
    r(s)
      .map {
        case CFGRule(b) =>
          b.map {
              case Nonterm("Var") =>
                id ^^ { _ =>
                  ()
                }
              case Nonterm("Value") =>
                value ^^ { _ =>
                  ()
                }
              case Nonterm("Code") =>
                code ^^ { _ =>
                  ()
                }
              case Nonterm("Const") =>
                const ^^ { _ =>
                  ()
                }
              case Nonterm("Linelist") =>
                linelist ^^ { _ =>
                  ()
                }
              case Nonterm(a) => lazyParser(a)
              case Term(a) =>
                a ^^ { _ =>
                  ()
                }
            }
            .reduce(
              (i, j) =>
                i ~ j ^^ { _ =>
                  ()
                }
            )
      }
      .reduce((i, j) => i ||| j)

  /*
  def nonTermParser: Parser[String] =
    r.map {
        case (k, v) =>
          k -> (v
            .map {
              case CFGRule(b) =>
                b.map {
                    case Nonterm(a) => lazyParser(a)
                    case Term(a) =>
                      a ^^ { _ =>
                        ()
                      }
                  }
                  .reduce(
                    (i, j) =>
                      i ~ j ^^ { _ =>
                        ()
                      }
                  )
            }
            .reduce((i, j) => i ||| j) ^^ { _ =>
            k
          })
      }
      .values
      .reduce(_ ||| _)
   */
  def apply(code: String): Boolean = parseAll(ruleParser(s), code) match {
    case Success(result, next) => true
    case _: NoSuccess => false
  }
  /*
  def readTerm(s: Reader[Char]): (String, Reader[Char]) = {
    parse(anyone, s) match {
      case Success(result, next) => (result, next)
      case Failure(msg, next)    => (next.first.toString, next.rest)
      case Error(msg, next)      => (next.first.toString, next.rest)
    }
  }

  def complete[T](p: Parser[T]) = new Parser[T] {
    def apply(in: Reader[Char]) = p(in) match {
      case s @ Success(out, in1) =>
        if (in1.atEnd || whiteSpace.findFirstIn(in1.first.toString).isDefined ) s
        else Failure("end of input or whitespace expected", in)
      case ns @ Failure(msg, in1) => Failure(msg, in)
      case ns => ns
    }
  }


  def generalize(code: String): CFGRule = {
    def aux(
        c: List[CFGSymbol],
        l: Reader[Char]
    ): (List[CFGSymbol], Reader[Char]) = {
      if (l.atEnd) (c, l)
      else {
        parse(complete(nonTermParser), l) match {
          case Success(result, next) => {
            aux(c :+ Nonterm(result), next)
          }
          case Failure(msg, next) => {
            val (term1, rest) = readTerm(next)
            aux(c :+ Term(term1), rest)
          }
          case Error(msg, next) => (c, l)
        }
      }
    }
    val (matched, _) = aux(List(), new CharSequenceReader(code))
    CFGRule(matched)
  }
 */
}
/*
class CandidateParser(g: LLGrammar) extends GRegexParsers {
  import com.codecommit.gll._
  override protected val whitespace = """[\s\,\.]+""".r

  val (r, s) = g match {
    case LLGrammar(_, r, s) => (r, s)
  }

  val anyone = """(\[\[|\]\]|\(|\)|[^\s\,\.\[\]\(\)]+)""".r ^^ { case id => id }
  lazy val anything: Parser[String] = anyone |
    anyone ~ anything ^^ { case (i, j) => i + " " + j }

  def candidateParser(s: String): List[Parser[List[(String, String)]]] =
    r(s)
      .map {
        case CFGRule(b) =>
          b.map {
              case Nonterm(a) =>
                anything ^^ {
                  case i => {
                    List((a, i))
                  }
                }
              case Term(a) =>
                a ^^ { _ =>
                  List()
                }
            }
            .reduce((i, j) => i ~ j ^^ { case (x, y) => x ++ y })
      }

  // def possibleParser(s: String): Parser[List[(Nonterm, String)]] = {

  // }

  def candidate(code: String): List[(String, String)] = {
    def aux(x: String, c: String): List[(String, String)] = {
      val l = candidateParser(x)
        .map(_(c).head)
        .collect {
          case Success(value, _) => value
        }
        .foldLeft(List[(String, String)]())(_ ++ _)
        .distinct
      l ++ l
        .map {
          case (i, j) => aux(i, j)
        }
        .foldLeft(List[(String, String)]())(_ ++ _)
        .distinct
    }
    aux(s, code)
  }

  def apply(code: String): List[(String, String)] = candidate(code)

}
 */
