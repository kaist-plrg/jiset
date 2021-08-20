package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.cfg.{ DotPrinter => _, _ }
import kr.ac.kaist.jiset.util.Appender

case class Graph(
  curOpt: Option[ControlPoint],
  depthOpt: Option[Int],
  pathOpt: Option[Path]
) {
  // conversion to DOT
  lazy val toDot: String = {
    val app = new Appender
    (app >> "digraph").wrap((curOpt, depthOpt, pathOpt) match {
      case (Some(cp), _, Some(path)) =>
        val func = sem.funcOf(cp)
        val view = cp.view
        val dot = ViewDotPrinter(view)
        var eid = dot.getId(func.entry)
        dot.addFunc(func, app)
        for (np <- path) {
          val func = sem.funcOf(np)
          val view = np.view
          val dot = ViewDotPrinter(view)
          dot.addFunc(func, app)
          dot.addCall(np.node, eid, app)
          eid = dot.getId(func.entry)
        }
      case (Some(cp), Some(depth), _) =>
        val func = sem.funcOf(cp)
        val view = cp.view
        val dot = ViewDotPrinter(view)
        val rp = ReturnPoint(func, view)
        dot.addFunc(func, app)
        showPrev(rp, dot, depth, app)
      case _ =>
        val funcs: Set[(Function, View)] =
          sem.getAllControlPoints.map(cp => (sem.funcOf(cp), cp.view))
        for ((func, view) <- funcs) {
          val dot = ViewDotPrinter(view)
          dot.addFunc(func, app)
        }

        // print call edges
        for ((ReturnPoint(func, returnView), calls) <- sem.retEdges) {
          val eid = ViewDotPrinter(returnView).getId(func.entry)
          for ((callNp @ NodePoint(call, callView), _) <- calls) {
            ViewDotPrinter(callView).addCall(call, eid, app)
          }
        }
    })
    app.toString
  }

  // show previous traces with depth
  def showPrev(
    rp: ReturnPoint,
    dot: DotPrinter,
    depth: Int,
    app: Appender
  ): Unit = {
    var visited = Set[ReturnPoint]()
    def aux(
      rp: ReturnPoint,
      dot: DotPrinter,
      depth: Int
    ): Unit = if (depth > 0) {
      val entry = rp.func.entry
      val entryNp = NodePoint(entry, rp.view)
      val eid = dot.getId(entry)
      for ((callNp @ NodePoint(call, callView), _) <- sem.getRetEdges(rp)) {
        val func = sem.funcOf(callNp)
        val callRp = ReturnPoint(func, callView)
        val callDot = ViewDotPrinter(callView)
        if (!(visited contains callRp)) {
          visited += callRp
          callDot.addFunc(func, app)
          aux(callRp, callDot, depth - 1)
        }
        callDot.addCall(call, eid, app)
      }
    }
    aux(rp, dot, depth)
  }

  private case class ViewDotPrinter(view: View) extends DotPrinter {
    def getId(func: Function): String = s"cluster${func.uid}_${norm(view)}"
    def getId(node: Node): String = s"node${node.uid}_${norm(view)}"
    def getName(func: Function): String = {
      val viewName = view.toString.replaceAll("\"", "\\\\\"")
      s"$func:$viewName"
    }
    def getColor(node: Node): String = {
      val np = NodePoint(node, view)
      if (!sem(np).isBottom) REACH
      else NON_REACH
    }
    def getColor(from: Node, to: Node): String = {
      val fromNP = NodePoint(from, view)
      val toNP = NodePoint(to, view)
      if (sem(fromNP).isBottom || sem(toNP).isBottom) NON_REACH
      else REACH
    }
    def getBgColor(node: Node): String = {
      val np = NodePoint(node, view)
      if (Some(np) == curOpt) CURRENT
      else if (worklist has np) SELECTED
      else NORMAL
    }
    def apply(app: Appender): Unit = {}
    def addCall(
      call: Call,
      entryId: String,
      app: Appender
    ): Unit = addEdge(getId(call), entryId, REACH, Some("call"), app)
  }
}
