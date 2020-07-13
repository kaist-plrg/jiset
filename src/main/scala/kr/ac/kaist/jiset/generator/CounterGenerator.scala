package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import collection.mutable.Queue

case class CounterGenerator(grammar: Grammar) {
  val Grammar(lexProds, prods) = grammar
  val lexicals = lexProds.map(_.lhs.name).map((_, List[String]())).toSet
  // key : (name of nonterminal, list of true parameters)
  var depth: Map[(String, List[String]), Int] = lexicals.map(_ -> 1).toMap
  // intialize queue with production whose lhs has empty parameters
  val queue: Queue[(Production, List[String])] = Queue(prods.filter(prod => prod.lhs.params.isEmpty).map((_, List[String]())): _*)
  val log = getPrintWriter(s"${BASE_DIR}/CounterGenerator.log")

  init

  def satisfyParams(params: List[String], cond: String): Boolean = {
    if (cond == "") true
    else {
      if (cond startsWith "p") params contains (cond substring 1)
      else !(params contains (cond substring 2))
    }
  }

  def getProdByName(name: String): Production = prods.find(_.lhs.name == name) match {
    case Some(p) => p
    case None => {
      val msg = s"CounterGenerator: unexpected production name $name"
      throw new Exception(msg)
    }
  }

  // get minimum depth of each token, rhs, production
  def getDepth(token: Token, params: List[String], ctx: Lhs): Option[Int] = {
    token match {
      case NonTerminal(name, args, optional) => {
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
        }).sorted

        // handle supplemental syntax
        if (ctx.name == "AsyncArrowFunction" && name == "CoverCallExpressionAndAsyncArrowHead") {
          return getDepth(getProdByName("AsyncArrowHead"), List())
        } else if (ctx.name == "PrimaryExpression" && name == "CoverParenthesizedExpressionAndArrowParameterList") {
          return getDepth(getProdByName("ParenthesizedExpression"), ntParams)
        } else if (ctx.name == "AssignmentExpression" && name == "LeftHandSideExpression") {
          return getDepth(getProdByName("AssignmentPattern"), ntParams)
        }

        // if depth of nonterminal with parameters is not discovered, add it to queue
        if (!(depth contains (name, ntParams)) && !(queue contains (getProdByName(name), ntParams)))
          queue.enqueue((getProdByName(name), ntParams))

        // handle optional
        if (optional) Some(0)
        else depth.get((name, ntParams))
      }
      case ButNot(base, cases) => getDepth(base, params, ctx)
      case _ => Some(0)
    }
  }

  def getDepth(rhs: Rhs, params: List[String], ctx: Lhs): Option[Int] = {
    val depths = rhs.tokens.flatMap((t: Token) => getDepth(t, params, ctx))

    if (depths.length == rhs.tokens.length) {
      val d = depths.reduceOption(_ max _)
      // if rhs is single nonterminal, then do not increase depth
      // other wise increase depth by 1
      rhs.tokens.headOption match {
        case Some(t) => t match {
          case NonTerminal(_, _, _) if rhs.tokens.length == 1 => d
          case _ => d.map(_ + 1)
        }
        case None => d
      }
    } else None
  }

  def getDepth(prod: Production, params: List[String]): Option[Int] =
    prod
      .rhsList
      .flatMap((rhs: Rhs) =>
        if (satisfyParams(params, rhs.cond)) getDepth(rhs, params, prod.lhs)
        else None)
      .reduceOption(_ min _)

  // depth map
  // lazy val depthMap: Map[String, List[Int]] =
  //   (Map[String, List[Int]]() /: prods)((m, prod) =>
  //     m + (prod.lhs.name -> prod.rhsList.map(rhs => getDepth(rhs).getOrElse(Int.MaxValue))))

  def printDepth = {
    for (
      ((prod, params), d) <- depth.toList.sortWith { case (((a, _), _), ((b, _), _)) => a < b }
    ) {
      val paramStr = params.mkString(",")
      println(s"[$prod($paramStr)] -> $d")
    }
  }

  // def printQueue = {
  //   log.println("**INITIAL QUEUE**")
  //   for ((prod, params) <- queue.toList) {
  //     val paramStr = params.mkString(",")
  //     val prodName = prod.lhs.name
  //     log.println(s"[$prodName($paramStr)]")
  //   }
  //   log.println("****************")
  // }

  // initialize depth
  def init: Unit = {
    while (!queue.isEmpty) {
      val (prod, params) = queue.dequeue
      val prodName = prod.lhs.name
      val paramString = params.mkString(",")
      log.println(s"$prodName($paramString)")
      getDepth(prod, params) match {
        case Some(d) => depth += (prod.lhs.name, params.sorted) -> d
        case None => queue.enqueue((prod, params))
      }
    }

    log.close()
  }

  // TODO : generate counter object
  def generate(packageName: String, modelDir: String): Unit = {
    val nf = getPrintWriter(s"$modelDir/DepthCounter.scala")

    def getLexicalCounter(prod: Production): Unit = {
      val name = prod.lhs.name
      nf.println(s"""  val $name = 0""")
    }

    def getDepthCounter(prod: Production): Unit = {
      val Production(lhs, rhsList) = prod
      val Lhs(name, rawParams) = lhs
      val params = rawParams.map("p" + _)
      val paramsStr = params.map(_ + ": Boolean").mkString(", ")

      val partialDepthMap = depth.filter { case ((prodName, _), _) => prodName == name }

      nf.println(s"""  def $name($paramsStr): Option[Int] = {""")
      partialDepthMap.foreach {
        case ((_, ps), d) => {
          if (rawParams.length > 0) {
            val conds = rawParams.map((rp: String) => if (ps.contains(rp)) s"p$rp == true" else s"p$rp == false")
            val ifCond = conds.mkString(" && ")
            nf.println(s"""    if( $ifCond ) Some($d)""")
          } else nf.println(s"""    Some($d)""")
        }
      }
      if (rawParams.length > 0) nf.println(s"""    None""")
      nf.println(s"""  }""")
    }

    nf.println(s"""package $packageName.model""")
    nf.println
    nf.println(s"""import $packageName.Lexical""")
    nf.println(s"""import $packageName.ir._""")
    nf.println
    nf.println(s"""object DepthCounter {""")
    lexProds.foreach(getLexicalCounter)
    prods.foreach(getDepthCounter)
    nf.println(s"""}""")

    nf.close()
  }
}
