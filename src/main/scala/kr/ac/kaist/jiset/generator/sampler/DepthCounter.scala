package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import collection.mutable.Queue

case class DepthCounter(grammar: Grammar) extends GrammarWorklist[Int](grammar) {
  val name: String = "DepthCounter"
  val resultType: String = "Depth"
  def getResult(elem: DepElem): String = {
    val pair = (elem.name, elem.params)
    val d = _result(elem)
    val rhsD = getRhsDepth(pair)
    s"Depth($d, $rhsD)"
  }
  def preString: String = "  case class Depth(depth: Int, rhsDepth: List[Option[Int]])"

  private lazy val Grammar(lexProds, prods) = grammar
  private lazy val targetProds = prods.filter(!_.lhs.isModule)
  private lazy val lexicals = lexProds.map(_.lhs.name).toSet

  protected def get(elem: DepElem): Option[Int] = getDepth(elem)
  protected def compare(oldVal: Int, newVal: Int): Boolean = oldVal > newVal

  ////////////////////////////////////////////////////////////////////////////////
  // Private Helperes
  ////////////////////////////////////////////////////////////////////////////////
  // get depth of productions
  private def getDepth(elem: DepElem): Option[Int] = lexicals.contains(elem.name) match {
    case true => Some(1)
    case false => prodMap(elem.name).rhsList.flatMap(rhs => {
      if (rhs.satisfy(elem.params)) getDepth(rhs, elem.params)
      else None
    }).reduceOption(_ min _)
  }

  // get depth of Rhs
  private def getDepth(rhs: Rhs, params: Set[String]): Option[Int] = {
    // filter module grammar
    if (!rhs.containsModuleNT) {
      val depths = rhs.tokens.flatMap((t: Token) => getDepth(t, params))
      if (depths.length == rhs.tokens.length) {
        val d = depths.reduceOption(_ max _)
        // if rhs is single nonterminal, then do not increase depth
        // other wise increase depth by 1
        if (rhs.isSingleNT) d else d.map(_ + 1)
      } else None
    } else None
  }

  // get minimum depth of each token, rhs, production
  private def getDepth(token: Token, params: Set[String]): Option[Int] = {
    token match {
      case NonTerminal(name, args, optional) => {
        if (Grammar.isModuleNT(name)) return None
        val ntParams = args.flatMap((arg: String) => {
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
        if (optional) Some(0)
        else _result.get(DepElem(name, ntParams))
      }
      case ButNot(base, cases) => getDepth(base, params)
      case _ => Some(0)
    }
  }

  // minimum depth for right-hand-sides
  private def getRhsDepth: Map[(String, Set[String]), List[Option[Int]]] =
    targetProds.foldLeft(Map[(String, Set[String]), List[Option[Int]]]()) {
      case (m, prod) => prod.lhs.params.toSet.subsets().foldLeft(m) {
        case (m, params) =>
          val name = prod.lhs.name
          val key = (name, params)
          val depths = prod.rhsList.map(rhs => {
            if (rhs.satisfy(params)) getDepth(rhs, params)
            else None
          })
          m + (key -> depths)
      }
    }

  // initialize queue with all cases of productions with parameters
  private def initQueue: Queue[(Production, Set[String])] = Queue(targetProds.flatMap(prod => {
    prod.lhs.params.toSet.subsets().toList.map((prod, _))
  }): _*)
}
