package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._

class DotPrinter {
  // colors
  val REACH = """"black""""
  val NON_REACH = """"gray""""
  val NORMAL = """"white""""
  val CURRENT = """"powderblue""""
  val IN_WORKLIST = """"gray""""

  // for pure cfg
  def apply(func: Function): DotPrinter = {
    this >> "digraph {"
    func.nodes.foreach(doNode(_, (REACH, NORMAL), false))
    func.edges.foreach(doEdge(_))
    this >> "}"
  }

  // print nodes
  def doNode(
    node: Node,
    colors: (String, String),
    useUId: Boolean
  ): DotPrinter =
    doNode(node, node, colors, useUId)
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

  // print edges
  def doEdge(
    edge: Edge,
    analysis: Boolean = false
  ): DotPrinter = {
    // print edge
    edge match {
      case LinearEdge(f, n) =>
        val c = REACH
        doEdge(f, n, c, "")
      case BranchEdge(f, tn, en) =>
        val tc = REACH
        val ec = REACH
        doEdge(f, tn, tc, s"label=<<font color=$tc>true</font>>")
        doEdge(f, en, ec, s"label=<<font color=$ec>false</font>>")
    }
  }
  def doEdge(from: String, to: String, color: String, label: String): DotPrinter =
    this >> s"""  $from -> $to [$label color=$color]"""

  // conversion to string
  override def toString: String = app.toString

  // string appender
  private val app: Appender = new Appender
  def >>(str: String): DotPrinter = { app >> str >> LINE_SEP; this }

  // normalize beautified ir nodes
  private def norm(node: IRNode, useUId: Boolean): String =
    escapeHtml(node.beautified(index = !useUId))
  // normalize beautified view
  private val normPattern = """[\[\](),\s~?"]""".r

  // implicit convertion from Node to String
  private implicit def node2str(n: Node): String = s"node${n.uid}"
}
