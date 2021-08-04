package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.checker._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._

class DotPrinter {
  // for pure cfg
  def apply(func: Function): DotPrinter = {
    this >> "digraph {"
    func.nodes.foreach(doNode(_, (REACH, NORMAL), false))
    func.nexts.foreach { case (f, t) => doNextEdge(f, t) }
    func.branches.foreach { case (f, (t, e)) => doBranchEdge(f, t, e) }
    this >> "}"
  }

  // show previous traces with depth
  def showPrev(rp: ReturnPoint, depth: Int): Unit = {
    var visited = Set[(Function, View)]()
    def aux(rp: ReturnPoint, depth: Int): Unit = if (depth > 0) {
      val entry = NodePoint(rp.func.entry, rp.view)
      for ((np, _) <- sem.getRetEdges(rp)) {
        val func = sem.funcOf(np)
        val pair = (func, np.view)
        if (!(visited contains pair)) {
          visited += pair
          doCluster(pair, None)
          aux(ReturnPoint(func, np.view), depth - 1)
        }
        doEdge(np2str(np), np2str(entry), REACH, s"label=<call>")
      }
    }
    aux(rp, depth)
  }

  // for debugging of type checking
  def apply(
    cur: Option[ControlPoint],
    depth: Option[Int] = None,
    path: Option[Path] = None
  ): DotPrinter = {
    this >> "digraph {"

    // print functions
    (cur, depth, path) match {
      case (Some(cp), _, Some(path)) =>
        val func = sem.funcOf(cp)
        val view = cp.view
        var entry = NodePoint(func.entry, view)
        doCluster((func, view), cur)
        for (np <- path) {
          val func = sem.funcOf(np)
          val view = np.view
          val pair = (func, view)
          doCluster(pair, None)
          doEdge(np2str(np), np2str(entry), REACH, s"label=<call>")
          entry = NodePoint(func.entry, view)
        }
      case (Some(cp), Some(depth), _) =>
        val func = sem.funcOf(cp)
        val view = cp.view
        doCluster((func, view), cur)

        // print call edges only for one call depth
        val rp = ReturnPoint(func, view)
        showPrev(rp, depth)
      case _ =>
        val funcs: Set[(Function, View)] =
          sem.getAllControlPoints.map(cp => (sem.funcOf(cp), cp.view))
        funcs.foreach(doCluster(_, cur))

        // print call edges
        sem.retEdges.foreach {
          case (ReturnPoint(func, rv), calls) => {
            val entry = NodePoint(func.entry, rv)
            for ((np, _) <- calls) {
              doEdge(np2str(np), np2str(entry), REACH, s"label=<call>")
            }
          }
        }
    }

    this >> "}"
  }

  // colors
  val REACH = """"black""""
  val NON_REACH = """"gray""""
  val NORMAL = """"white""""
  val CURRENT = """"powderblue""""
  val IN_WORKLIST = """"gray""""

  // print cluster
  def doCluster(
    pair: (Function, View),
    cur: Option[ControlPoint] = None
  ): DotPrinter = {
    val (func, view) = pair
    val viewName = view.toString.replaceAll("\"", "\\\\\"")
    this >> s"""  subgraph cluster${func.uid}_${norm(view)} {"""
    this >> s"""    label = "${func.name}:$viewName""""
    this >> s"""    style = rounded"""
    func.nodes.foreach(doNode(_, view, cur))
    func.nexts.foreach {
      case (f, t) => doNextEdge(f, t, true)(Some(view))
    }
    func.branches.foreach {
      case (f, (t, e)) => doBranchEdge(f, t, e, true)(Some(view))
    }
    this >> s"""  }"""
  }

  // colors for edges
  def edgeColor(
    from: Node,
    to: Node,
    check: Boolean
  )(
    implicit
    view: Option[View] = None
  ): String = (check, view) match {
    case (false, _) | (_, None) => REACH
    case (true, Some(view)) =>
      val fromNP = NodePoint(from, view)
      val toNP = NodePoint(to, view)
      if (sem(fromNP).isBottom || sem(toNP).isBottom) NON_REACH
      else REACH
  }

  // print next edges
  def doNextEdge(f: Linear, t: Node, check: Boolean = false)(
    implicit
    view: Option[View] = None
  ): DotPrinter = {
    val c = edgeColor(f, t, check)
    doEdge(str(f), str(t), c, "")
  }

  // print branch edges
  def doBranchEdge(f: Branch, t: Node, e: Node, check: Boolean = false)(
    implicit
    view: Option[View] = None
  ): DotPrinter = {
    val tc = edgeColor(f, t, check)
    val ec = edgeColor(f, e, check)
    doEdge(str(f), str(t), tc, s"label=<<font color=$tc>true</font>>")
    doEdge(str(f), str(e), ec, s"label=<<font color=$ec>false</font>>")
  }

  // print edges
  def doEdge(from: String, to: String, color: String, label: String): DotPrinter =
    this >> s"""  $from -> $to [$label color=$color]"""

  // print nodes
  def doNode(
    node: Node,
    view: View,
    cur: Option[ControlPoint] = None
  ): DotPrinter = {
    val np = NodePoint(node, view)
    val colors = {
      if (Some(np) == cur) (REACH, CURRENT)
      else if (worklist has np) (REACH, IN_WORKLIST)
      else if (!sem(np).isBottom) (REACH, NORMAL)
      else (NON_REACH, NORMAL)
    }
    doNode(node, np2str(np), colors, true)
  }
  def doNode(
    node: Node,
    colors: (String, String),
    useUId: Boolean
  ): DotPrinter =
    doNode(node, node2str(node), colors, useUId)
  def doNode(
    node: Node,
    name: String,
    colors: (String, String),
    useUId: Boolean
  ): DotPrinter = {
    val (color, bgColor) = colors
    if (useUId) {
      this >> s"""  ${name}_name [shape=none, label=<<font color=$color>$node</font>>]"""
      this >> s"""  ${name}_name -> $name [arrowhead=none, color=$color, style=dashed]"""
    }

    node match {
      case Entry(_) =>
        this >> s"""  $name [shape=circle label=" " color=$color fillcolor=$bgColor style=filled]"""
      case Exit(_) =>
        this >> s"""  $name [shape=circle label=" " color=$color fillcolor=$bgColor style=filled]"""
      case Normal(_, inst) =>
        this >> s"""  $name [shape=box label=<<font color=$color>${norm(inst, useUId)}</font>> color=$color fillcolor=$bgColor style=filled]"""
      case Call(_, inst) =>
        this >> s"""  $name [shape=cds label=<<font color=$color>${norm(inst, useUId)}</font>> color=$color fillcolor=$bgColor style=filled]"""
      case Arrow(_, inst, fid) =>
        this >> s"""  $name [shape=hexagon label=<<font color=$color>${norm(inst, useUId, fid)}</font>> color=$color fillcolor=$bgColor style=filled]"""
      case Branch(_, inst) =>
        this >> s"""  $name [shape=diamond label=<<font color=$color>${norm(inst.cond, useUId)}</font>> color=$color fillcolor=$bgColor style=filled]"""
    }
  }

  // Appender
  private val app: Appender = new Appender

  def >>(str: String): DotPrinter = { app >> str >> LINE_SEP; this }

  override def toString: String = app.toString

  // normalize beautified ir nodes
  private def norm(node: IRElem, useUId: Boolean, fid: Int = -1): String = {
    val postfix = node match {
      case arrow: ArrowInst => s" [fid: $fid]"
      case _ => ""
    }
    escapeHtml(node.beautified(detail = false, line = !useUId))
      .replaceAll("\u0000", "U+0000") + postfix
  }

  // normalize beautified view
  private val normPattern = """[\[\](),\s~?"]""".r
  private def norm(view: View): String = normPattern.replaceAllIn(view.tys.map {
    case NormalT(RecordT(props)) => NormalT(NameT("Record"))
    case RecordT(props) => NameT("Record")
    case t => t
  }.toString, "")

  // change node to string depending on view
  def str(n: Node)(implicit v: Option[View]): String = v match {
    case None => node2str(n)
    case Some(v) => np2str(NodePoint(n, v))
  }

  // implicit convertion from Node to String
  private implicit def node2str(n: Node): String = s"node${n.uid}"
  private def np2str[T <: Node](np: NodePoint[T]): String = s"${node2str(np.node)}_${norm(np.view)}"
}
