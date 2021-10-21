package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir.JsonProtocol._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.util.JvmUseful._
import scala.collection.mutable.{ Map => MMap }

object Logger {
  // result for each interp
  case class MResult(
    name: String,
    var maxDepth: Int = 1,
    touched: MMap[Node, Int] = MMap()
  ) {
    // update maximum call depth
    def updateDepth(st: State): Unit = {
      val d = st.ctxtStack.size + 1
      if (d > maxDepth) maxDepth = d
    }

    // update touched
    def updateTouched(st: State): Unit = {
      val cursor = st.context.cursorOpt.get
      cursor match {
        case NodeCursor(node) =>
          val count = touched.getOrElse(node, 0) + 1
          touched += (node -> count)
        case _ =>
      }
    }

    // convert to fixed result object
    def into(iter: Int): Result =
      Result(name, iter, maxDepth, Map(touched.toSeq: _*))
  }

  // result counter
  private var rid = 0
  private def incRid(): Int = { val prev = rid; rid += 1; prev }

  // interp result
  case class Result(
    name: String,
    iter: Int,
    maxDepth: Int,
    touched: Map[Node, Int]
  )

  // log IR interp
  def log(interp: Interp): Unit = {
    // set mutable result object
    val name = interp.st.fnameOpt.getOrElse("UNKNOWN")
    val mresult = MResult(name)

    // subscribe call, cont and update depth
    interp.subscribe(
      List(Interp.Event.Call, Interp.Event.Cont),
      mresult.updateDepth
    )

    // subscribe each step and update touched
    interp.subscribe(Interp.Event.Step, mresult.updateTouched)

    // subscribe termination and dump result
    interp.subscribe(Interp.Event.Terminate, { st =>
      // dump result
      val result = mresult.into(interp.getIter)
      dumpJson(result, s"$logDir/${incRid()}.json", true)
    })
  }

  // set base directory to dump
  private var _logDir: Option[String] = None
  def logDir: String = _logDir.get
  def setBase(logDir: String) = {
    mkdir(logDir)
    _logDir = Some(logDir)
  }
}
