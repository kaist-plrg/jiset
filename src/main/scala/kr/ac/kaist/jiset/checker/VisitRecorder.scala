package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.util.JvmUseful._
import scala.collection.mutable.{ Map => MMap }

// recorder for visited CFG nodes
case class VisitRecorder(
  funcMap: VisitRecorder.FuncMap
) extends CheckerElem {
  import VisitRecorder._

  // record visited nodes
  def record(
    func: Function,
    node: Node,
    fnameOpt: Option[String]
  ): Unit = {
    val fname = fnameOpt.getOrElse("UNKNOWN")
    val nodeMap = funcMap.getOrElseUpdate(func, MMap())
    val fileMap = nodeMap.getOrElseUpdate(node, MMap())
    val count = fileMap.getOrElseUpdate(fname, 0)
    fileMap += fname -> (count + 1)
  }

  // number of components
  def func: Long = funcMap.size
  def node: Long = funcMap.map(_._2.size).sum

  // Data for CSV
  def funcData: List[String] = (for {
    (func, nodeMap) <- funcMap
  } yield List(func.uid, (for {
    (_, fileMap) <- nodeMap
    (fname, _) <- fileMap
  } yield fname).size).mkString(",")).toList

  def nodeData: List[String] = (for {
    (func, nodeMap) <- funcMap
    (node, fileMap) <- nodeMap
  } yield List(node.uid, (for {
    (fname, _) <- fileMap
  } yield fname).size).mkString(",")).toList

  def rawData: List[String] = (for {
    (func, nodeMap) <- funcMap
    (node, fileMap) <- nodeMap
    (file, count) <- fileMap
  } yield List(func.uid, node.uid, file, count).mkString(",")).toList

  def dumpCsv(filename: String): Unit = {
    dumpFile(
      (List("Function", "Node", "File", "Count").mkString(",") :: rawData).mkString(LINE_SEP),
      s"$filename.csv"
    )
    dumpFile(
      (List("Function", "# File").mkString(",") :: funcData).mkString(LINE_SEP),
      s"$filename-func.csv"
    )
    dumpFile(
      (List("Node", "# File").mkString(",") :: nodeData).mkString(LINE_SEP),
      s"$filename-node.csv"
    )
  }
}
object VisitRecorder {
  // internal types
  type FuncMap = MMap[Function, NodeMap]
  type NodeMap = MMap[Node, FileMap]
  type FileMap = MMap[String, Long]

  // constructors
  def apply(pairs: (Function, NodeMap)*): VisitRecorder =
    VisitRecorder(MMap.from(pairs))
}
