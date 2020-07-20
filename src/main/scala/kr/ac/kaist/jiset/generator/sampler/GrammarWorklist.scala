package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import scala.collection.mutable.Queue

abstract class GrammarWorklist[T](grammar: Grammar) {
  val name: String
  val resultType: String
  def getResult(elem: DepElem): String
  def preString: String

  lazy val result: Map[DepElem, T] = _result
  protected def get(elem: DepElem): Option[T]
  protected def compare(oldVal: T, newVal: T): Boolean

  protected var _result: Map[DepElem, T] = Map()
  protected val grammarDep: GrammarDep = GrammarDep(grammar)
  protected val dep: Map[DepElem, Set[DepElem]] = grammarDep.dep
  protected val prodMap: Map[String, Production] = grammarDep.prodMap
  protected val worklist: Queue[DepElem] = Queue(grammarDep.bases.toSeq: _*)

  while (!worklist.isEmpty) {
    val elem = worklist.dequeue
    get(elem) match {
      case Some(newVal) => _result.get(elem) match {
        case Some(oldVal) if !compare(oldVal, newVal) =>
        case _ =>
          _result += elem -> newVal
          dep.getOrElse(elem, Set()).foreach(worklist.enqueue(_))
      }
      case None => worklist.enqueue(elem)
    }
  }

  def generate(packageName: String, modelDir: String): Unit = {
    val nf = getPrintWriter(s"$modelDir/$name.scala")

    def get(prod: Production): Unit = {
      val Production(lhs, rhsList) = prod
      val Lhs(name, rawParams) = lhs
      val params = rawParams.map("p" + _)
      val paramsStr = params.map(_ + ": Boolean").mkString(", ")

      val partialMap = result.filter { case (elem, _) => elem.name == name }

      nf.println(s"""  def $name($paramsStr): $resultType = (${params.mkString(", ")}) match {""")
      partialMap.foreach {
        case (elem @ DepElem(_, ps), d) => {
          val conds = rawParams.map(rp => if (ps.contains(rp)) s"true" else s"false")
          val cond = conds.mkString("(", ", ", ")")
          val res = getResult(elem)
          nf.println(s"""    case $cond => $res""")
        }
      }
      nf.println(s"""  }""")
    }

    nf.println(s"""package $packageName.model""")
    nf.println
    nf.println(s"""object $name {""")
    nf.println(preString)
    grammarDep.targetProds.foreach(get)
    nf.println(s"""}""")

    nf.close()
  }
}
