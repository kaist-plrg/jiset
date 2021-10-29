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
  mkdir(s"$EDITOR_LOG_DIR")

  // mapping from node to shortest program
  type NodeMap = Map[Node, JsProgram]
  var nodeMap: NodeMap = Map()

  // file writer for log
  val nfLog = getPrintWriter(s"$EDITOR_LOG_DIR/put.log")

  // put one js program to filter
  var putCount = 0
  def put(p: JsProgram): Unit = {
    putCount += 1

    // create interp object
    val interp = new Interp(p.initState, useHook = true)

    // subscribe step event in interp
    var printed = false
    interp.subscribe(Interp.Event.Step, { st =>
      val cursor = st.context.cursorOpt.get
      cursor match {
        case NodeCursor(n) =>
          // update touched
          p.touched(n.uid) = true

          // update nodeMap
          nodeMap.get(n) match {
            case Some(p0) if p0.size <= p.size =>
            case _ =>
              nodeMap += (n -> p)

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
  }

  // get programs touching a node
  def getTouched(n: Node): Set[JsProgram] = for {
    p <- nodeMap.values.toSet if p.touched(n.uid)
  } yield p

  def getCovered(p: JsProgram): Set[Node] = for {
    n <- nodeMap.keySet if nodeMap(n).uid == p.uid
  } yield n

  // select one program and try to reduce its size
  def tryReduce(): Boolean = ???

  // filter program using a given syntactic view
  // def apply(ast: AST, view: SyntacticView): Boolean =
  //   ast.contains(view.ast)

  // dump stats
  def dumpStats(): Unit = ???

  def dumpCSV(): Unit = {
    val nf = getPrintWriter(s"$EDITOR_LOG_DIR/Covered.csv")
    val header = "Program, Size, #Covered"
    nf.println(header)
    mkdir(s"$EDITOR_LOG_DIR/Covered")
    for {
      p: JsProgram <- nodeMap.values.toSet
    } {
      val data = s"${p.uid},${p.size},${getCovered(p).size}"
      nf.println(data)

      val covernf = getPrintWriter(s"$EDITOR_LOG_DIR/Covered/${p.uid}.log")
      covernf.println(getCovered(p).mkString)
      covernf.close
    }
    nf.close
  }

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

  // close
  def close(): Unit = nfLog.close()
}
