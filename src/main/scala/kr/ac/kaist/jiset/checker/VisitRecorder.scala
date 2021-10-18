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
  def funcData: List[String] = {
    val fmap: MMap[Function, Set[String]] = MMap()
    for {
      (file, nodeMap) <- fileMap
      (node, (func, _)) <- nodeMap
    } {
      val fileSet = fmap.getOrElseUpdate(func, Set())
      fmap += func -> (fileSet + file)
    }
    (for {
      (func, fileset) <- fmap
    } yield List(func, fileset.size).mkString(",")).toList
  }

  def nodeData: List[String] = {
    val nmap: MMap[Node, Set[String]] = MMap()
    for {
      (file, nodeMap) <- fileMap
      (node, _) <- nodeMap
    } {
      val fileSet = nmap.getOrElseUpdate(node, Set())
      nmap += node -> (fileSet + file)
    }
    (for {
      (node, fileset) <- nmap
    } yield List(node, fileset.size).mkString(",")).toList
  }

  def dumpCsv(dirname: String): Unit = {
    dumpFile(
      "Visited-nodes-func.csv",
      (List("Func", "# File").mkString(",") :: funcData).mkString(LINE_SEP),
      s"$dirname/visited-nodes-func.csv"
    )
    dumpFile(
      "Visited-nodes-node.csv",
      (List("Node", "# File").mkString(",") :: nodeData).mkString(LINE_SEP),
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
