package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.algorithm._
import kr.ac.kaist.jiset.spec.{ Grammar => SpecGrammar, Token => GrammarToken, _ }
import scala.util.parsing.combinator._
import scala.util.parsing.input._

object BugPatch extends RegexParsers {
  // es10-1
  val assertForAsyncIterator = true
  def patchAssertForAsyncIterator(algo: Algorithm): Unit = {
    algo.steps(6).tokens(2).asInstanceOf[StepList].steps(0).tokens =
      getTokens("Assert: id:iterationKind is const:iterate or const:async-iterate .")
  }

  // es10-2
  val ambiguousIfStatement = true
  def patchAmbiguousIfStatement(grammar: SpecGrammar): Unit = {
    val SpecGrammar(_, prods) = grammar
    prods.foreach {
      case Production(lhs @ Lhs("Statement", params), rhsList) =>
        lhs.params :+= "Else"
        rhsList.foreach {
          case Rhs(List(nt @ NonTerminal("IfStatement", _, _)), _) => nt.args :+= "?Else"
          case Rhs(List(nt @ NonTerminal("BreakableStatement", _, _)), _) => nt.args :+= "?Else"
          case Rhs(List(nt @ NonTerminal("WithStatement", _, _)), _) => nt.args :+= "?Else"
          case Rhs(List(nt @ NonTerminal("LabelledStatement", _, _)), _) => nt.args :+= "?Else"
          case _ =>
        }
      case Production(lhs @ Lhs("BreakableStatement", params), rhsList) =>
        lhs.params :+= "Else"
        rhsList.foreach {
          case Rhs(List(nt @ NonTerminal("IterationStatement", _, _)), _) => nt.args :+= "?Else"
          case _ =>
        }
      case Production(lhs @ Lhs("StatementListItem", params), rhsList) =>
        rhsList.foreach {
          case Rhs(List(nt @ NonTerminal("Statement", _, _)), _) => nt.args :+= "~Else"
          case _ =>
        }
      case Production(lhs @ Lhs("IfStatement", params), rhsList) =>
        lhs.params :+= "Else"
        rhsList(0).tokens(4).asInstanceOf[NonTerminal].args :+= "+Else"
        rhsList(0).tokens(6).asInstanceOf[NonTerminal].args :+= "?Else"
        rhsList(1).cond = "!pElse"
        rhsList(1).tokens(4).asInstanceOf[NonTerminal].args :+= "~Else"
      case Production(lhs @ Lhs("IterationStatement", params), rhsList) =>
        lhs.params :+= "Else"
        rhsList(0).tokens(1).asInstanceOf[NonTerminal].args :+= "~Else"
        rhsList.foreach {
          case rhs => rhs.tokens.last match {
            case nt @ NonTerminal("Statement", _, _) => nt.args :+= "?Else"
            case _ =>
          }
        }
      case Production(lhs @ Lhs("WithStatement", params), rhsList) =>
        lhs.params :+= "Else"
        rhsList(0).tokens.last.asInstanceOf[NonTerminal].args :+= "?Else"
      case Production(lhs @ Lhs("LabelledStatement", params), rhsList) =>
        lhs.params :+= "Else"
        rhsList(0).tokens.last.asInstanceOf[NonTerminal].args :+= "?Else"
      case _ =>
    }
  }

  // es10-3
  val numberEqual = true
  def patchNumberEqual(algo: Algorithm): Unit = {
    algo.steps(6).tokens =
      getTokens("If id:index is value:-0 , return value:undefined .")
  }

  // es10-4
  val completionInAbstractEquality = true
  def patchCompletionInAbstractEquality(algo: Algorithm): Unit = {
    algo.steps(7).tokens =
      getTokens("If Type( id:x ) is either String, Number, or Symbol and Type( id:y ) is Object, return the result of the comparison id:x == ?ToPrimitive( id:y ) .")
    algo.steps(8).tokens =
      getTokens("If Type( id:x ) is Object and Type ( id:y ) is either String, Number, or Symbol, return the result of the comparison ?ToPrimitive( id:x ) == id:y .")
  }

  // es10-5
  val completionInEqualityExpr = true
  def patchCompletionInEqualityExpr(algo: Algorithm): Unit = {
    algo.steps :+= algo.steps.last.copy()
    algo.steps(5).tokens =
      getTokens("ReturnIfAbrupt( id:r ) .")
  }

  private lazy val word = "[a-zA-Z0-9]+".r
  private lazy val symobol = ".".r
  private lazy val any = "\\S+".r
  private lazy val text = (word | symobol) ^^ { Text(_) }
  private lazy val id = "id:" ~> any ^^ { Id(_) }
  private lazy val value = "value:" ~> any ^^ { Value(_) }
  private lazy val const = "const:" ~> any ^^ { Const(_) }
  private lazy val token = text ||| id ||| value ||| const
  private lazy val tokens = rep1(token)
  def getTokens(str: String): List[Token] = parseAll(tokens, str).get
}
