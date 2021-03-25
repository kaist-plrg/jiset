package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.AbsSemantics._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._

class DotPrinter {
  // for pure cfg
  def apply(func: Function): DotPrinter = {
    this >> "digraph {"
    func.nodes.foreach(doNode(_, (REACH, NORMAL), false))
    func.edges.foreach(doEdge(_))
    this >> "}"
  }

  def showPrev(rp: ReturnPoint, depth: Int): Unit = {
    var visited = Set[(Function, View)]()
    def aux(rp: ReturnPoint, depth: Int): Unit = if (depth > 0) {
      val entry = NodePoint(rp.func.entry, rp.view)
      for ((np, _) <- getRetEdges(rp)) {
        val func = funcOf(np)
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

  // for debugging analysis
  def apply(
    cur: Option[ControlPoint],
    depth: Option[Int] = None
  ): DotPrinter = {
    this >> "digraph {"

    // print functions
    (cur, depth) match {
      case (Some(cp), depthOpt) =>
        val depth = depthOpt.getOrElse(0)
        val func = funcOf(cp)
        val view = cp.view
        doCluster((func, view), cur)

        // print call edges only for one call depth
        val rp = ReturnPoint(func, view)
        showPrev(rp, depth)
      case (Some(cp), None) =>
        val func = funcOf(cp)
        val view = cp.view
        doCluster((func, view), cur)

        // print call edges only for one call depth
        val rp = ReturnPoint(func, view)
        showPrev(rp, 0)
      case _ =>
        val funcs: Set[(Function, View)] =
          getAllControlPoints.map(cp => (funcOf(cp), cp.view))
        funcs.foreach(doCluster(_, cur))

        // print call edges
        retEdges.foreach {
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
    func.edges.foreach(doEdge(_, true)(Some(view)))
    this >> s"""  }"""
  }

  // print edges
  def doEdge(
    edge: Edge,
    analysis: Boolean = false
  )(
    implicit
    view: Option[View] = None
  ): DotPrinter = {
    // change node to string depending on view
    def str(n: Node)(implicit v: Option[View]): String = v match {
      case None => node2str(n)
      case Some(v) => np2str(NodePoint(n, v))
    }

    def color(from: Node, to: Node): String = (analysis, view) match {
      case (false, _) | (_, None) => REACH
      case (true, Some(view)) =>
        val fromNP = NodePoint(from, view)
        val toNP = NodePoint(to, view)
        if (AbsSemantics(fromNP).isBottom || AbsSemantics(toNP).isBottom) NON_REACH
        else REACH
    }

    // print edge
    edge match {
      case LinearEdge(f, n) =>
        val c = color(f, n)
        doEdge(str(f), str(n), c, "")
      case BranchEdge(f, tn, en) =>
        val tc = color(f, tn)
        val ec = color(f, en)
        doEdge(str(f), str(tn), tc, s"label=<<font color=$tc>true</font>>")
        doEdge(str(f), str(en), ec, s"label=<<font color=$ec>false</font>>")
    }
  }
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
      else if (!AbsSemantics(np).isBottom) (REACH, NORMAL)
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
      case Block(_, insts) =>
        this >> s"""  $name [shape=none, margin=0, label=<<font color=$color>"""
        this >> s"""    <table border="0" cellborder="1" cellspacing="0" cellpadding="10">"""
        insts.foreach(inst => {
          this >> s"""      <tr><td align="left">${norm(inst, useUId)}</td></tr>"""
        })
        this >> s"""    </table>""" >>
          s"""  </font>> color=$color fillcolor=$bgColor style=filled]"""
      case Call(_, inst) =>
        this >> s"""  $name [shape=cds, label=<<font color=$color>${norm(inst, useUId)}</font>> color=$color fillcolor=$bgColor style=filled]"""
      case Branch(_, cond) =>
        this >> s"""  $name [shape=diamond, label=<<font color=$color>${norm(cond, useUId)}</font>> color=$color fillcolor=$bgColor style=filled]"""
    }
  }

  // Appender
  private val app: Appender = new Appender

  def >>(str: String): DotPrinter = { app >> str >> LINE_SEP; this }

  override def toString: String = app.toString

  // normalize beautified ir nodes

  private def norm(node: IRNode, useUId: Boolean): String =
    escapeHtml(node.beautified(index = !useUId))
  // normalize beautified view
  private val normPattern = """[\[\](),\s~?"]""".r
  private def norm(view: View): String = normPattern.replaceAllIn(view.toString, "")

  // implicit convertion from Node to String
  private implicit def node2str(n: Node): String = s"node${n.uid}"
  private def np2str[T <: Node](np: NodePoint[T]): String = s"${node2str(np.node)}_${norm(np.view)}"
}
