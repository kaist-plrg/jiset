package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import collection.mutable.Queue

case class DepthCounter(grammar: Grammar) {
  // initialization
  private val Grammar(lexProds, prods) = grammar
  private val targetProds = prods.filter(!_.lhs.isModule)
  private val queue: Queue[(Production, Set[String])] = initQueue
  private var _depth: Map[(String, Set[String]), Int] = Map()
  init

  // minimum depth of each production with parameters
  lazy val depth: Map[(String, Set[String]), Int] = _depth

  // minimum depth for right-hand-sides
  lazy val rhsDepth: Map[(String, Set[String]), List[Option[Int]]] = getRhsDepth

  // print depth
  def print[T](map: Map[(String, Set[String]), T]): Unit = for (
    ((prod, params), d) <- map.toList.sortWith { case (((a, _), _), ((b, _), _)) => a < b }
  ) {
    val paramStr = params.mkString(",")
    println(s"[$prod($paramStr)] -> $d")
  }

  // synthesize Scala file for modeling
  def generate(packageName: String, modelDir: String): Unit = {
    val nf = getPrintWriter(s"$modelDir/DepthCounter.scala")

    def getDepthCounter(prod: Production): Unit = {
      val Production(lhs, rhsList) = prod
      val Lhs(name, rawParams) = lhs
      val params = rawParams.map("p" + _)
      val paramsStr = params.map(_ + ": Boolean").mkString(", ")

      val partialDepthMap = depth.filter { case ((prodName, _), _) => prodName == name }

      nf.println(s"""  def $name($paramsStr): Depth = (${params.mkString(", ")}) match {""")
      partialDepthMap.foreach {
        case (pair @ (_, ps), d) => {
          val conds = rawParams.map(rp => if (ps.contains(rp)) s"true" else s"false")
          val cond = conds.mkString("(", ", ", ")")
          val rhsD = rhsDepth(pair)
          nf.println(s"""    case $cond => Depth($d, $rhsD)""")
        }
      }
      nf.println(s"""  }""")
    }

    nf.println(s"""package $packageName.model""")
    nf.println
    nf.println(s"""import $packageName.Lexical""")
    nf.println(s"""import $packageName.ir._""")
    nf.println
    nf.println(s"""case class Depth(depth: Int, rhsDepth: List[Option[Int]])""")
    nf.println(s"""object DepthCounter {""")
    // TODO base cases
    targetProds.foreach(getDepthCounter)
    nf.println(s"""}""")

    nf.close()
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Private Helperes
  ////////////////////////////////////////////////////////////////////////////////
  // initialize depth
  private def init: Unit = {
    val lexicals = lexProds.map(_.lhs.name).map((_, Set[String]())).toSet
    _depth = lexicals.map(_ -> 1).toMap
    while (!queue.isEmpty) {
      val (prod, params) = queue.dequeue
      // filter module grammar
      if (!prod.lhs.isModule) {
        getDepth(prod, params) match {
          case Some(d) => _depth += (prod.lhs.name, params) -> d
          case None => queue.enqueue((prod, params))
        }
      }
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

  // get minimum depth of each token, rhs, production
  private def getDepth(token: Token, params: Set[String]): Option[Int] = {
    token match {
      case NonTerminal(name, args, optional) => {
        if (GrammarHelper.isModuleNT(name)) return None
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
        else _depth.get((name, ntParams))
      }
      case ButNot(base, cases) => getDepth(base, params)
      case _ => Some(0)
    }
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

  // get depth of productions
  private def getDepth(prod: Production, params: Set[String]): Option[Int] =
    prod
      .rhsList
      .flatMap((rhs: Rhs) =>
        if (rhs.satisfy(params)) getDepth(rhs, params)
        else None)
      .reduceOption(_ min _)
}
