package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._
import scala.util.parsing.combinator._
import spray.json._

// ECMAScript grammars
case class Grammar(
    var lexProds: List[Production],
    var prods: List[Production]
) {
  def getProdByName(name: String): Production =
    prods.find(_.lhs.name == name) match {
      case Some(prod) => prod
      case None => throw new Exception(s"Grammar: $name is not production")
    }
}

// ECMAScript grammar helper
object GrammarHelper {
  private val moduleNT: Set[String] = Set(
    "ExportDeclaration",
    "ExportFromClause",
    "ExportsList",
    "ExportsSpecifier",
    "FromClause",
    "ImportCall",
    "ImportClause",
    "ImportDeclaration",
    "ImportMeta",
    "ImportSpecifier",
    "ImportedBinding",
    "ImportedDefaultBinding",
    "ImportsList",
    "Module",
    "ModuleBody",
    "ModuleItem",
    "ModuleItemList",
    "ModuleSpecifier",
    "NameSpaceImport",
    "NamedExports",
    "NamedImports",
    "RegularExpressionLiteral",
  )
  private val supplementalNT: Set[String] = Set(
    "CallMemberExpression",
    "ParenthesizedExpression",
    "ArrowFormalParameters",
    "AsyncArrowHead",
    "AssignmentPattern",
    "ObjectAssignmentPattern",
    "ArrayAssignmentPattern",
    "AssignmentRestProperty",
    "AssignmentPropertyList",
    "AssignmentElementList",
    "AssignmentElisionElement",
    "AssignmentProperty",
    "AssignmentElement",
    "AssignmentRestElement",
    "DestructuringAssignmentTarget"
  )
  def isModuleNT(name: String): Boolean = moduleNT contains name
  def isSupplementalNT(name: String): Boolean = supplementalNT contains name
  def isTargetNT(name: String): Boolean = !(isModuleNT(name) || isSupplementalNT(name))
}

// productions
case class Production(
    var lhs: Lhs,
    var rhsList: List[Rhs]
)
object Production extends RegexParsers {
  def apply(prod: List[String]): Option[Production] =
    prod.map(revertSpecialCodes) match {
      case lhsStr :: rhsStrList => {
        val lhs = parse(lhsParser, lhsStr.trim).get
        val oneOf = lhsStr.trim.endsWith("one of")
        // TODO create rhsList
        val rhsList = rhsStrList.map(r => parse(rhsParser, r.trim).get)
        // TODO handle oneOf
        Some(Production(lhs, rhsList))
      }
      case Nil => error(s"ill-formed production:" + LINE_SEP + prod.mkString(LINE_SEP))
    }

  //common
  lazy val any = "\\S+".r
  lazy val word = "\\w+".r
  lazy val cWord = "[A-Z]\\w+".r
  lazy val pWord = "[?|\\+|~]*\\w+".r
  lazy val params: Parser[List[String]] = "[" ~> repsep(pWord, ",") <~ "]"
  // lhs
  lazy val lhsParser: Parser[Lhs] = word ~ opt(params) <~ "[:]+".r ^^ {
    case n ~ None => Lhs(n, Nil)
    case n ~ Some(params) => Lhs(n, params)
  }
  //butnot
  lazy val butnot = (nt <~ ("but not" <~ opt("one of"))) ~ rep(token <~ opt("or")) ^^ {
    case base ~ cases => ButNot(base, cases)
  }
  // lookahead
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
  // terminal
  lazy val term = "`" ~> ("[^`]+".r | "`") <~ "`" ^^ { Terminal(_) }
  // non terminal
  lazy val nt = cWord ~ opt(params) ~ opt("?") ^^ {
    case n ~ Some(args) ~ Some(_) => NonTerminal(n, args, true)
    case n ~ Some(args) ~ None => NonTerminal(n, args, false)
    case n ~ None ~ Some(_) => NonTerminal(n, Nil, true)
    case n ~ None ~ None => NonTerminal(n, Nil, false)
  }
  // rhs
  lazy val token: Parser[Token] = butnot | lookahead | nt | term
  lazy val constraints = "[" ~> "[+|~]".r ~ word <~ "]" ^^ {
    case "+" ~ c => "p" + c
    case "~" ~ c => "!p" + c
    case _ => ??? // impossible
  }
  lazy val rhsParser: Parser[Rhs] = opt(constraints) ~ rep(token) ^^ {
    case Some(cond) ~ tokens => Rhs(tokens, cond)
    case None ~ tokens => Rhs(tokens, "")
  }

  def log[T](parser: Parser[T]): Parser[T] =
    parser ^^ { x => { /*println(s"[LOG]$msg: $x");*/ x } }
}

// left-hand-sides
case class Lhs(
    var name: String,
    var params: List[String]
) {
  def isModule: Boolean = GrammarHelper.isModuleNT(name)
  def isSupplemental: Boolean = GrammarHelper.isSupplementalNT(name)
  def isTarget: Boolean = GrammarHelper.isTargetNT(name)
  def isScript: Boolean = name == "Script"
}

// right-hand-sides
case class Rhs(
    var tokens: List[Token],
    var cond: String
) {
  // check whehter if tokens is a single nonterminal
  def isSingleNT: Boolean = tokens.flatMap(_.norm) match {
    case List(_: NonTerminal) => true
    case _ => false
  }
  // check non terminal
  def check(f: String => Boolean, init: Boolean, op: (Boolean, Boolean) => Boolean) =
    tokens.foldLeft(init) {
      case (b, t) => t match {
        case NonTerminal(name, _, _) => op(b, f(name))
        case _ => b
      }
    }
  // check wheter if tokens contain module nonterminal
  def containsModuleNT: Boolean =
    check(GrammarHelper.isModuleNT, false, (x, y) => x || y)
  def containsSupplementalNT: Boolean =
    check(GrammarHelper.isSupplementalNT, false, (x, y) => x || y)
  def isTarget: Boolean =
    check(GrammarHelper.isTargetNT, true, (x, y) => x && y)
  // check if rhs satifies parameters
  def satisfy(params: Set[String]): Boolean = {
    if (cond == "") true
    else {
      if (cond startsWith "p") params contains (cond substring 1)
      else !(params contains (cond substring 2))
    }
  }
}

// tokens
trait Token {
  def norm: Option[Token] = this match {
    case ButNot(base, _) => base.norm
    case EmptyToken | Lookahead(_, _) => None
    case t => Some(t)
  }
}
case class Terminal(var term: String) extends Token
case class NonTerminal(
    var name: String,
    var args: List[String],
    var optional: Boolean
) extends Token
case class ButNot(
    var base: Token,
    var cases: List[Token]
) extends Token
case class Lookahead(
    var contains: Boolean,
    var cases: List[List[Token]]
) extends Token
case class Unicode(var code: String) extends Token
case object EmptyToken extends Token
case object NoLineTerminatorToken extends Token
case object UnicodeAny extends Token
case object UnicodeIdStart extends Token
case object UnicodeIdContinue extends Token
