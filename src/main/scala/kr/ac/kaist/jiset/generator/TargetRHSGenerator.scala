package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import collection.mutable.Queue

import spray.json._

// RHSElem and JSON format of TargetRHS
case class RHSElem(rhsName: String, params: List[Boolean], o: Int)
object RHSElemProtocol extends DefaultJsonProtocol {
  implicit val RHSElemFormat = jsonFormat3(RHSElem)
}
import RHSElemProtocol._

// TargetRHSGenerator
case class TargetRHSGenerator(grammar: Grammar) {
  // initialization
  private val Grammar(lexProds, prods) = grammar
  private val targetProds = prods.filter(!_.lhs.isModuleNT)
  private var _targetRhs: Set[(String, List[Boolean], Int)] = Set()
  init

  lazy val targetRhs: List[RHSElem] = {
    val sorted = _targetRhs.toList.sortWith {
      case ((n1, p1, o1), (n2, p2, o2)) => {
        if (n1 != n2) n1 < n2
        else if (param2int(p1) != param2int(p2)) param2int(p1) < param2int(p2)
        else o1 < o2
      }
    }
    sorted.map { case (rhsName, params, o) => RHSElem(rhsName, params, o) }
  }

  // generate
  def generate(resourceDir: String): Unit =
    dumpJson(targetRhs, s"$resourceDir/TargetRHS.json")

  // helpers
  // def printRhs: Unit = for ((rhsName, params, o) <- targetRhs.toList.sortWith { case ((a, _, _), (b, _, _)) => a < b }) {
  //   val paramStr = params.mkString(",")
  //   println(s"$rhsName[$paramStr][$o]")
  // }

  // represent parameters as integer
  private def param2int(params: List[Boolean]): Int = {
    params.reverse.zipWithIndex.foldLeft(0) {
      case (acc, (p, i)) => acc + (if (p) 1 << i else 0)
    }
  }

  // extract target rhs from production, rhs
  private def getTarget(rhs: Rhs, params: Set[String], rhsName: String, rawParams: List[String]): List[(String, List[Boolean], Int)] = {
    // count option
    val o = rhs.tokens.count {
      case NonTerminal(_, _, true) => true
      case _ => false
    }
    val bparams = rawParams.map(params.contains(_))
    (0 to ((1 << o) - 1)).toList.map((rhsName, bparams, _))
  }

  private def getTarget(prod: Production, params: Set[String]): Unit = {
    val name = prod.lhs.name
    // iter all rhs list
    val subTargetList = prod.rhsList.zipWithIndex.flatMap {
      case (rhs, i) => {
        val rhsName = s"$name$i"
        // filter module and check parameter condition
        if (!rhs.isModuleNT && rhs.satisfy(params)) {
          Some(getTarget(rhs, params, rhsName, prod.lhs.params))
        } else None
      }
    }
    // add target sub list to target rhs
    _targetRhs ++= subTargetList.flatten.toSet
  }

  private def init: Unit = targetProds.foreach {
    case prod => {
      val paramsList = prod.lhs.params.toSet.subsets().toList
      paramsList.foreach(getTarget(prod, _))
    }
  }
}