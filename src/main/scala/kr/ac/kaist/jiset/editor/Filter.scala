package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.editor.JsonProtocol._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.{ EDITOR_LOG_DIR, LOG, LINE_SEP }

// filtering js programs using its size and spec coverage
object Filter {
  // files
  mkdir(s"$EDITOR_LOG_DIR")
  mkdir(s"$EDITOR_LOG_DIR/cached")
  val cached = s"$EDITOR_LOG_DIR/cached/filter.json"
  val nfLog = getPrintWriter(s"$EDITOR_LOG_DIR/log")
  def close(): Unit = nfLog.close()

  // mapping from node to shortest program
  type NodeMap = Array[Option[JsProgram]]
  var nodeMap: NodeMap = Array.fill(cfg.nodes.size)(None)

  // reference counting for each js program
  type RcMap = Map[JsProgram, Int]
  private var rcMap: RcMap = Map()
  private def incRc(p: JsProgram): Unit =
    rcMap += (p -> (rcMap.getOrElse(p, 0) + 1))
  private def decRc(p: JsProgram): Unit =
    rcMap(p) match {
      case cnt if cnt <= 1 => rcMap -= p
      case cnt => rcMap += (p -> (cnt - 1))
    }

  // touched nodes by programs put in filter
  def touchedSize: Int = nodeMap.count(!_.isEmpty)

  // get # of programs after filtering
  def programSize: Int = rcMap.size

  // get programs touching a node
  def getTouchedPrograms(n: Node): Set[JsProgram] = for {
    p <- rcMap.keySet if p.touched(n.uid)
  } yield p

  // put one js program to filter
  var putCount = 0
  def put(p: JsProgram): Unit = {
    // logging
    putCount += 1
    var printed = false
    def log(nid: Int): Unit = {
      if (!printed) {
        nfLog.println("!!!", p.toString)
        printed = true
      }
      nfLog.println(s"$putCount, ${nid}, ${touchedSize}")
    }

    // update nodeMap
    def update(nid: Int): Unit = {
      nodeMap(nid).foreach(decRc(_))
      nodeMap(nid) = Some(p)
      incRc(p)
    }

    // update shortest programs by node
    p.touched.zipWithIndex.foreach {
      case (true, nid) =>
        nodeMap(nid) match {
          case Some(p0) if p0.size <= p.size =>
          case _ =>
            if (LOG) log(nid)
            update(nid)
        }
      case _ =>
    }
  }

  // select one program and try to reduce its size
  def tryReduce(): Boolean = {
    // TODO how to select target program?

    // TODO how to reduce program?

    ???
  }

  // dump
  def dump() = {
    val data = (putCount, nodeMap, rcMap)
    dumpJson(data, cached, true)
  }

  // load
  type FilterData = (Int, NodeMap, RcMap)
  def load(): Unit = {
    val data = readJson[FilterData](cached)
    putCount = data._1; nodeMap = data._2; rcMap = data._3
  }

  // filter program using a given syntactic view
  // def apply(ast: AST, view: SyntacticView): Boolean =
  //   ast.contains(view.ast)

  // dump stats
  def dumpStats(): Unit = ???

  def dumpCSV(): Unit = {
    // get node s.t. nodeMap[node] == p
    def getCovered(p: JsProgram): Array[Int] = (for {
      (p0Opt, nid) <- nodeMap.zipWithIndex
      p0 <- p0Opt if p0 == p
    } yield nid).sorted

    val nf = getPrintWriter(s"$EDITOR_LOG_DIR/Covered.csv")
    val header = "Program, Size, #Covered"
    nf.println(header)
    mkdir(s"$EDITOR_LOG_DIR/Covered")
    for { (p, rc) <- rcMap } {
      nf.println(s"${p.uid},${p.size},${rc}")
      val covernf = getPrintWriter(s"$EDITOR_LOG_DIR/Covered/${p.uid}.log")
      covernf.println(getCovered(p).mkString(LINE_SEP))
      covernf.close
    }
    nf.close
  }
}
