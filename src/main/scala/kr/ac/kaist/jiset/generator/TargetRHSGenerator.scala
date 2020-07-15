package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import collection.mutable.Queue

case class TargetRHSGenerator(grammar: Grammar) {
  // initialization
  private val Grammar(lexProds, prods) = grammar
  private val targetProds = prods.filter(!_.lhs.isModuleNT)
  private var _targetRhs: Set[(String, List[Boolean], Int)] = Set()
  init

  private def param2int(params: List[Boolean]): Int = {
    params.reverse.zipWithIndex.foldLeft(0) {
      case (acc, (p, i)) => acc + (if (p) 1 << i else 0)
    }
  }

  lazy val targetRhs: List[(String, List[Boolean], Int)] = _targetRhs.toList.sortWith {
    case ((n1, p1, o1), (n2, p2, o2)) => {
      if (n1 != n2) n1 < n2
      else if (param2int(p1) != param2int(p2)) param2int(p1) < param2int(p2)
      else o1 < o2
    }
  }

  // generate
  def generate(packageName: String, resourceDir: String): Unit = {
    val nf = getPrintWriter(s"$resourceDir/TargetRHS.json")

    def getRhsElement(elem: (String, List[Boolean], Int)): String = elem match {
      case (rhsName: String, params: List[Boolean], o: Int) => {
        val pn = param2int(params)
        s"""  "$rhsName,$pn,$o""""
      }
    }

    nf.println("[")
    nf.println(targetRhs.map(getRhsElement).mkString(",\n"))
    nf.println("]")
    nf.flush

    // val scalaSet = "scala.collection.immutable.Set"

    // nf.println(s"""package $packageName.model""")
    // nf.println
    // nf.println(s"""import $packageName.Lexical""")
    // nf.println(s"""import $packageName.ir._""")
    // nf.println
    // nf.println(s"""object TargetRHS {""")
    // nf.println(s"""  var rhsSet: $scalaSet[(String, List[Boolean], Int)] = $scalaSet()""")
    // nf.println(s"""  init""")
    // nf.println
    // nf.println(s"""  def init: Unit = {""")
    // targetRhs.foreach(getRhsElement)
    // nf.println(s"""  }""")
    // nf.println(s"""""")
    // nf.println(s"""}""")
    // nf.flush
  }

  // helpers
  def printRhs: Unit = for ((rhsName, params, o) <- targetRhs.toList.sortWith { case ((a, _, _), (b, _, _)) => a < b }) {
    val paramStr = params.mkString(",")
    println(s"$rhsName[$paramStr][$o]")
  }

  // check valid parameters
  private def satisfyParams(params: Set[String], cond: String): Boolean = {
    if (cond == "") true
    else {
      if (cond startsWith "p") params contains (cond substring 1)
      else !(params contains (cond substring 2))
    }
  }

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
        if (!rhs.isModuleNT && satisfyParams(params, rhs.cond)) {
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