package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._

class DotPrinter {
  // for pure cfg
  def apply(func: Function): DotPrinter = {
    this >> s"""digraph {"""
    func.nodes.foreach(doNode(_, (REACH, NORMAL)))
    func.edges.foreach(doEdge(_))
    this >> s"""}"""
  }

  // for debugging analysis
  def apply(sem: AbsSemantics, cur: Option[ControlPoint]): DotPrinter = {
    this >> s"""digraph {"""

    // print functions
    val funcs: Set[(Function, View)] =
      sem.getControlPoints.map(cp => (sem.funcOf(cp), cp.view))
    funcs.foreach(doCluster(_, sem, cur))

    // print call edges
    sem._getRetEdges.foreach {
      case (ReturnPoint(func, rv), calls) => {
        val entry = NodePoint(func.entry, rv)
        for ((np, _) <- calls) {
          doEdge(np2str(np), np2str(entry), REACH, "")
        }
      }
    }

    this >> s"""}"""
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
    sem: AbsSemantics,
    cur: Option[ControlPoint] = None
  ): DotPrinter = {
    val (func, view) = pair
    this >> s"""  subgraph cluster${func.uid}_${norm(view)} {"""
    this >> s"""    label = "${func.name}:$view""""
    this >> s"""    style = rounded"""
    func.nodes.foreach(doNode(_, view, sem, cur))
    func.edges.foreach(doEdge(_, Some(sem))(Some(view)))
    this >> s"""  }"""
  }

  // print edges
  def doEdge(
    edge: Edge,
    sem: Option[AbsSemantics] = None
  )(
    implicit
    view: Option[View] = None
  ): DotPrinter = {
    // change node to string depending on view
    def str(n: Node)(implicit v: Option[View]): String = v match {
      case None => node2str(n)
      case Some(v) => np2str(NodePoint(n, v))
    }

    // get color by semantics and view
    val color = (sem, view) match {
      case (None, _) | (_, None) => REACH
      case (Some(sem), Some(view)) =>
        if (sem(NodePoint(edge.from, view)).isBottom) NON_REACH
        else REACH
    }

    // print edge
    edge match {
      case LinearEdge(f, n) => doEdge(str(f), str(n), color, "")
      case BranchEdge(f, tn, en) =>
        doEdge(str(f), str(tn), color, s"label=<<font color=$color>true</font>>")
        doEdge(str(f), str(en), color, s"label=<<font color=$color>false</font>>")
    }
  }
  def doEdge(from: String, to: String, color: String, label: String): DotPrinter =
    this >> s"""  $from -> $to [$label color=$color]"""

  // print nodes
  def doNode(
    node: Node,
    view: View,
    sem: AbsSemantics,
    cur: Option[ControlPoint] = None
  ): DotPrinter = {
    val np = NodePoint(node, view)
    val colors = {
      if (Some(np) == cur) (REACH, CURRENT)
      else if (sem.worklist has np) (REACH, IN_WORKLIST)
      else if (!sem(np).isBottom) (REACH, NORMAL)
      else (NON_REACH, NORMAL)
    }
    doNode(node, np2str(np), colors)
  }
  def doNode(node: Node, colors: (String, String)): DotPrinter =
    doNode(node, node2str(node), colors)
  def doNode(node: Node, name: String, colors: (String, String)): DotPrinter = {
    val (color, bgColor) = colors
    node match {
      case Entry() =>
        this >> s"""  $name [shape=circle label=" " color=$color fillcolor=$bgColor style=filled]"""
      case Exit() =>
        this >> s"""  $name [shape=circle label=" " color=$color fillcolor=$bgColor style=filled]"""
      case Block(insts) =>
        this >> s"""  $name [shape=none, margin=0, label=<<font color=$color>""" >>
          s"""    <table border="0" cellborder="1" cellspacing="0" cellpadding="10">"""
        insts.foreach(inst => {
          this >> s"""      <tr><td align="left">${norm(inst)}</td></tr>"""
        })
        this >> s"""    </table>""" >>
          s"""  </font>> color=$color fillcolor=$bgColor style=filled]"""
      case Call(inst) =>
        this >> s"""  $name [shape=cds, label=<<font color=$color>${norm(inst)}</font>> color=$color fillcolor=$bgColor style=filled]"""
      case Branch(cond) =>
        this >> s"""  $name [shape=diamond, label=<<font color=$color>${norm(cond)}</font>> color=$color fillcolor=$bgColor style=filled]"""
    }
  }

  // Appender
  private val app: Appender = new Appender

  def >>(str: String): DotPrinter = { app >> str >> LINE_SEP; this }

  override def toString: String = app.toString

  // normalize beautified ir nodes
  private def norm(node: IRNode): String = escapeHtml(node.beautified(index = true))
  // normalize beautified view
  private val normPattern = """[\[\](),\s~?]""".r
  private def norm(view: View): String = normPattern.replaceAllIn(view.toString, "")

  // implicit convertion from Node to String
  private implicit def node2str(n: Node): String = s"node${n.uid}"
  private def np2str[T <: Node](np: NodePoint[T]): String = s"${node2str(np.node)}_${norm(np.view)}"
}
