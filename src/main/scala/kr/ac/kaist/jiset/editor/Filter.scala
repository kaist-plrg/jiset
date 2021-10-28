package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.editor.JsonProtocol._
import kr.ac.kaist.jiset.ir.{ NodeCursor, Interp }
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.{ EDITOR_LOG_DIR, LOG, DEBUG }

// filtering js programs using its size and spec coverage
object Filter {
  import cfgJsonProtocol._

  // filter program using a given syntactic view
  // def apply(ast: AST, view: SyntacticView): Boolean =
  //   ast.contains(view.ast)

  type NodeMap = Map[Node, JsProgram]

  // mapping from node to shortest program
  var nodeMap: NodeMap = Map()

  // mapping from uid of program to touched nodes
  type Flag = Array[Boolean]
  var touchedMap: Map[String, Flag] = Map()

  // count put operation
  var putCount = 0

  // file writer for log
  mkdir(s"$EDITOR_LOG_DIR")
  val nfLog = getPrintWriter(s"$EDITOR_LOG_DIR/put.log")

  // put one js program to filter
  def put(p: JsProgram): Unit = {
    putCount += 1

    // uid of the program
    val pid = p.uid

    // create touched flag
    var flag = touchedMap.getOrElse(pid, Array.fill(cfg.nodes.size)(false))

    // create interp object
    val interp = new Interp(p.initState, useHook = true)

    // subscribe step event in interp
    var printed = false
    interp.subscribe(Interp.Event.Step, { st =>
      val cursor = st.context.cursorOpt.get
      cursor match {
        case NodeCursor(n) =>
          //flag update
          flag(n.uid) = true

          //nodeMap update
          nodeMap.get(n) match {
            case Some(p0) if p0.size <= p.size =>
            case _ =>
              nodeMap += (n -> p) // TODO save touched

              // logging
              if (LOG) {
                if (!printed) {
                  nfLog.println("!!!", p.toString)
                  printed = true
                }
                nfLog.println(s"$putCount, ${n.uid}, ${nodeMap.size}")
              }
          }
        case _ =>
      }
    })

    // fixpoint
    interp.fixpoint

    //touchedMap update
    touchedMap += (p.uid -> flag)
  }

  // select one program and try to reduce its size
  def tryReduce(): Boolean = ???

  // dump stats
  def dumpStats(): Unit = ???
  def getProgramCount: Int =
    nodeMap.values.map(_.uid).toSet.size
  def getCoveredSize: Int =
    nodeMap.size

  // dump
  def dump() =
    dumpJson(
      (putCount, nodeMap),
      s"$EDITOR_LOG_DIR/filter_data.json",
      noSpace = true
    )

  // load
  def load() = {
    val (c, m) = readJson[(Int, NodeMap)](s"$EDITOR_LOG_DIR/filter_data.json")
    putCount = c
    nodeMap = m
  }

  // ??
  //  def touchPrograms(nid: Int): Set[String] = (for {
  //    (pid, flag) <- touchedMap
  //    if flag(nid)
  //  } yield pid).toSet
  //
  //  def dumpPrograms: Unit = for {
  //    (_, nid) <- Array.fill(cfg.nodes.size)(0).zipWithIndex
  //  } {
  //    val nf = getPrintWriter(s"$EDITOR_LOG_DIR/$nid.log")
  //    val ps = touchPrograms(nid)
  //    for {
  //      p <- ps
  //    } {
  //      nf.println(s"$p")
  //    }
  //    nf.close
  //  }

  // close
  def close(): Unit = nfLog.close()
}
