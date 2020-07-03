package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import collection.mutable.Queue

case class CounterGenerator(grammar: Grammar) {
  val Grammar(lexProds, prods) = grammar
  val lexicals = lexProds.map(_.lhs.name).toSet
  var depth = lexicals.map(_ -> 1).toMap
  val queue = Queue(prods: _*)

  init

  def getDepth(token: Token): Option[Int] = token match {
    case NonTerminal(name, args, false) => depth.get(name)
    case ButNot(base, cases) => getDepth(base)
    case _ => Some(0)
  }
  def getDepth(rhs: Rhs): Option[Int] = {
    val depths = rhs.tokens.flatMap(getDepth)
    if (depths.length == rhs.tokens.length) depths.reduceOption(_ max _)
    else None
  }
  def getDepth(prod: Production): Option[Int] =
    prod.rhsList.flatMap(getDepth).reduceOption(_ min _).map(_ + 1)

  def init: Unit = {
    while (!queue.isEmpty) {
      val prod = queue.dequeue
      getDepth(prod) match {
        case Some(d) => depth += prod.lhs.name -> d
        case None => queue.enqueue(prod)
      }
    }
  }
}
