package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.extractor.ECMAScriptParser
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Useful._

// ECMASCript specifications
case class ECMAScript(
  version: String,
  grammar: Grammar,
  algos: List[Algo],
  consts: Set[String],
  intrinsics: Set[String],
  symbols: Set[String],
  aoids: Set[String],
  section: Section
) extends SpecComponent {
  // mapping from ids to instructions
  val idMap: Map[Int, Inst] = {
    val setter = new InstIdSetter
    for (algo <- algos) setter.walk(algo.body)
    setter.getIdMap
  }

  // completed/incompleted algorithms
  lazy val (completedAlgos, incompletedAlgos): (List[Algo], List[Algo]) =
    algos.partition(_.isComplete)

  // get parent of id
  lazy val parentMap: Map[String, String] = {
    var map: Map[String, String] = Map()
    def aux(section: Section): Unit = {
      val Section(id, subs) = section
      for (sub <- subs) { map += sub.id -> id; aux(sub) }
    }
    aux(section)
    map
  }
  def getParent(id: String): Option[String] = parentMap.get(id)

  // get ancestors of id
  lazy val ancestorMap: Map[String, Set[String]] = {
    var map: Map[String, Set[String]] = Map()
    def get(id: String): Set[String] = map.get(id).getOrElse {
      val set = parentMap.get(id) match {
        case None => Set(id)
        case Some(parent) => get(parent) + id
      }
      map += id -> set
      set
    }
    for (id <- parentMap.keySet) get(id)
    map
  }
  def getAncestors(id: String): Set[String] = parentMap.get(id) match {
    case None => Set(id)
    case Some(parent) => getAncestors(parent) + id
  }
}
