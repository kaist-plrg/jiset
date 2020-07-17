package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import collection.mutable.Queue

import spray.json._

// RHSElem and JSON format of TargetRHS
case class RHSElem(rhsName: String, parserParams: List[Boolean], k: Int)
object RHSElemProtocol extends DefaultJsonProtocol {
  implicit val RHSElemFormat = jsonFormat3(RHSElem)
}
import RHSElemProtocol._

// TargetRHSGenerator
case class TargetRHSGenerator(grammar: Grammar) {
  // initialization
  private val Grammar(lexProds, prods) = grammar
  // private val targetProds = prods.filter(!_.lhs.isModule)
  private val lexProdNames = lexProds.map(_.lhs.name)
  private var _targetRhs: Set[RHSElem] = Set()
  // init queue with script
  private val queue = Queue(prods.filter(_.lhs.isScript).map((_, Set[String]())): _*)
  init

  lazy val targetRhs: List[RHSElem] = _targetRhs.toList.sortWith {
    case (RHSElem(n1, p1, k1), RHSElem(n2, p2, k2)) => {
      if (n1 != n2) n1 < n2
      else if (param2int(p1) != param2int(p2)) param2int(p1) < param2int(p2)
      else k1 < k2
    }
  }

  // generate
  def generate(resourceDir: String): Unit =
    dumpJson(targetRhs, s"$resourceDir/TargetRHS.json")

  // represent parameters as integer
  private def param2int(params: List[Boolean]): Int = {
    params.reverse.zipWithIndex.foldLeft(0) {
      case (acc, (p, i)) => acc + (if (p) 1 << i else 0)
    }
  }

  // extract target rhs from production, rhs
  private def getTarget(prod: Production, params: Set[String]): (Set[RHSElem], List[(Production, Set[String])]) = {
    def getQueueItems(token: Token, params: Set[String]): Option[(Production, Set[String])] =
      token match {
        // filter lexical production and get queue item
        case NonTerminal(name, args, optional) if !lexProdNames.contains(name) => {
          val tProd = grammar.getProdByName(name)
          val tParams = args.flatMap((arg: String) => {
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
          Some((tProd, tParams))
        }
        case ButNot(base, _) => getQueueItems(base, params)
        case _ => None
      }

    val name = prod.lhs.name
    prod.rhsList.zipWithIndex.foldLeft((Set[RHSElem](), List[(Production, Set[String])]())) {
      case ((s, l), (rhs, i)) => {
        val rhsName = s"$name$i"
        // check if rhs is target and satisfies parameters
        if (rhs.isTarget && rhs.satisfy(params)) {
          val bparams = prod.lhs.params.map(params.contains(_))
          val k = rhs.tokens.count {
            case NonTerminal(_, _, true) => true
            case _ => false
          }
          // all reachable rhs
          val targets = (0 to ((1 << k) - 1)).toList.map(RHSElem(rhsName, bparams, _)).toSet
          // next queue items
          val queueItems = rhs.tokens.flatMap(getQueueItems(_, params))
          (s ++ targets, l ++ queueItems)
        } else (s, l)
      }
    }
  }

  private def init: Unit = {
    var visited: Set[(Production, Set[String])] = Set()
    while (!queue.isEmpty) {
      val (prod, params) = queue.dequeue
      // check if targets contain production
      if (prod.lhs.isTarget && !lexProdNames.contains(prod.lhs.name)) {
        val (s, l) = getTarget(prod, params)
        _targetRhs = _targetRhs ++ s
        // add current prodcution to visited
        l.filter(!visited.contains(_)).foreach(queue.enqueue)
        visited = visited ++ l.toSet
      }
    }
  }
}
