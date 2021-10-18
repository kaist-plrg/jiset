package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.{ LINE_SEP }
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.util.JvmUseful._
import scala.collection.mutable.{ Map => MMap }

// recorder for visited CFG nodes
case class VisitRecorder(
  fileMap: VisitRecorder.FileMap
) extends CheckerElem {
  import VisitRecorder._

  // Data for CSV
  def funcData: List[String] = (for {
    (file, nodeMap) <- fileMap
  } yield List(file, (for {
    (_, (func, count)) <- nodeMap
  } yield func).toSet.size).mkString(",")).toList

  def nodeData: List[String] = (for {
    (file, nodeMap) <- fileMap
  } yield List(file, nodeMap.size).mkString(",")).toList

  def rawData: List[String] = (for {
    (file, nodeMap) <- fileMap
    (node, (func, count)) <- nodeMap
  } yield List(file, node, func, count).mkString(",")).toList

  def dumpCsv(dirname: String): Unit = {
    dumpFile(
      "Visited-nodes-func.csv",
      (List("File", "# Func").mkString(",") :: funcData).mkString(LINE_SEP),
      s"$dirname/visited-nodes-func.csv"
    )
    dumpFile(
      "Visited-nodes-node.csv",
      (List("File", "# Node").mkString(",") :: nodeData).mkString(LINE_SEP),
      s"$dirname/visited-nodes-node.csv"
    )
  }
}
object VisitRecorder {
  // internal types
  type FileMap = Map[String, NodeMap]
  type NodeMap = Map[Node, (Function, Int)]

  // load VisitRecorder
  def apply(dirname: String, passfile: String): VisitRecorder = {
    import cfg.jsonProtocol._
    val passTests = readFile(passfile).split(LINE_SEP).toSet
    VisitRecorder(
      (for {
        file <- walkTree(s"$dirname")
        if jsonFilter(file.getName)
        pair = readJson[(String, Map[Node, (Function, Int)])](file.toString)
        if passTests(pair._1)
      } yield pair).toMap
    )
  }
}
