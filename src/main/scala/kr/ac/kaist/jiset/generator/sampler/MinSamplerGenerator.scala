package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._

case class MinSamplerGenerator(grammar: Grammar) extends GrammarWorklist[String](grammar) {
  val name: String = "MinSampler"
  val resultType: String = "String"
  def getResult(elem: DepElem): String = s""""${result(elem)}""""
  def preString: String = baseSampler.map {
    case (name, result) => s"""  def $name(): String = "$result""""
  }.mkString(LINE_SEP)

  protected def get(elem: DepElem): Option[String] = getSampler(elem)
  protected def compare(oldVal: String, newVal: String): Boolean = (
    (newVal.length < oldVal.length) ||
    (newVal.length == oldVal.length && newVal < oldVal)
  )

  ////////////////////////////////////////////////////////////////////////////////
  // Private Helpers
  ////////////////////////////////////////////////////////////////////////////////
  private def min(left: String, right: String): String =
    if (compare(left, right)) right else left
  private val Grammar(lexProds, prods) = grammar
  private val targetProds = prods.filter(!_.lhs.isModule)

  private lazy val baseSampler: Map[String, String] = Map(
    "IdentifierName" -> "x",
    "NullLiteral" -> "null",
    "BooleanLiteral" -> "true",
    "NumericLiteral" -> "42",
    "StringLiteral" -> "''",
    "NoSubstitutionTemplate" -> "``",
    "TemplateHead" -> "`${",
    "TemplateMiddle" -> "}${",
    "TemplateTail" -> "}`",
  )

  private def getSampler(elem: DepElem): Option[String] = baseSampler.get(elem.name) match {
    case Some(str) => Some(str)
    case None => prodMap(elem.name).rhsList.flatMap(rhs => {
      if (rhs.satisfy(elem.params)) getSampler(rhs, elem.params)
      else None
    }).reduceOption(min)
  }

  private def getSampler(rhs: Rhs, params: Set[String]): Option[String] = {
    // filter module grammar
    if (!rhs.containsModuleNT) {
      val results = rhs.tokens.flatMap(getSampler(_, params))
      if (results.length != rhs.tokens.length) None
      else results.reduceOption(_ + " " + _)
    } else None
  }
  private def getSampler(token: Token, params: Set[String]): Option[String] = token match {
    case NonTerminal(name, _, _) if (Grammar.isModuleNT(name)) => None
    case NonTerminal(name, args, optional) =>
      val ntParams = args.flatMap(arg => {
        arg.headOption match {
          case Some(a) => a match {
            // ? => params, + => true, ~ => false
            case '?' if params contains (arg substring 1) => Some(arg substring 1)
            case '+' => Some(arg substring 1)
            case _ => None
          }
          case None => None
        }
      }).toSet

      // handle optional
      if (optional) Some("")
      else _result.get(DepElem(name, ntParams))
    case ButNot(base, cases) => getSampler(base, params)
    case Terminal(t) => Some(t)
    case _ => Some("")
  }
}
