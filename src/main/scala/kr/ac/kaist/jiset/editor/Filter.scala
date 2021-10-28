package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.{ EDITOR_LOG_DIR, LOG, DEBUG }
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir.{ State, NodeCursor, Interp }
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.parser.MetaParser
import kr.ac.kaist.jiset.util.JvmUseful._

// filtering js programs using its size and spec coverage
object Filter {
  // filter program using a given syntactic view
  // def apply(ast: AST, view: SyntacticView): Boolean =
  //   ast.contains(view.ast)

  // js program in filtered mapping
  trait JsProgram {
    // unique id
    def uid: String

    // AST of js program
    val script: Script

    // filename
    val filename: String

    // raw string of js program
    lazy val raw: String = script.toString

    // size of js program
    def size: Int = raw.length

    // initialize state
    def initState: State =
      Initialize(script, Some(filename), NodeCursor)

    // parse script from filename
    def fromFile(filename: String): Script =
      Parser.parse(Parser.Script(Nil), fileReader(filename)).get

    // toString
    override def toString: String = s"[$uid]: $filename ($size)"
  }
  case class Test262Program(id: Int, filename: String) extends JsProgram {
    // unique id
    def uid: String = s"T$id"

    // AST of js program
    lazy val script = Test262.loadTest(
      fromFile(filename),
      MetaParser(filename).includes
    )
  }
  case class JestProgram(id: Int, filename: String) extends JsProgram {
    // unique id
    def uid: String = s"J$id"

    // AST of js program
    lazy val script = fromFile(filename)
  }
  case class CustomProgram(id: Int, filename: String) extends JsProgram {
    // unique id
    def uid: String = s"C$id"

    // AST of js program
    lazy val script = fromFile(filename)
  }
  // case class Generated(id, Int, filename: String) extends JsProgram
  // case class Reduced(uid, Int,  extends JsProgram

  // mapping from node to shortest program
  var nodeMap: Map[Node, JsProgram] = Map()

  // count put operation
  var putCount = 0

  // file writer for log
  mkdir(s"$EDITOR_LOG_DIR")
  val nfLog = getPrintWriter(s"$EDITOR_LOG_DIR/put.log")

  // put one js program to filter
  def put(p: JsProgram): Unit = {
    putCount += 1

    // create interp object
    val interp = new Interp(p.initState, useHook = true)

    // subscribe step event in interp
    var printed = false
    interp.subscribe(Interp.Event.Step, { st =>
      val cursor = st.context.cursorOpt.get
      cursor match {
        case NodeCursor(n) => nodeMap.get(n) match {
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
  }

  // select one program and try to reduce its size
  def tryReduce(): Boolean = ???

  // dump
  def dump = ???

  // load
  def load = ???

  // close
  def close(): Unit = nfLog.close()
}
