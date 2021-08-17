package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.checker._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._

// printer to DOT format
trait DotPrinter {
  // helpers
  def getId(func: Function): String
  def getId(node: Node): String
  def getName(func: Function): String
  def getColor(node: Node): String
  def getColor(from: Node, to: Node): String
  def getBgColor(node: Node): String
  def apply(app: Appender): Unit

  // colors
  val REACH = """"black""""
  val NON_REACH = """"gray""""
  val NORMAL = """"white""""
  val CURRENT = """"powderblue""""

  // conversion to string
  override def toString: String = {
    val app = new Appender
    (app >> "digraph ").wrap {
      this(app)
    }
    app.toString
  }

  // functions
  def addFunc(func: Function, app: Appender): Unit = {
    val id = getId(func)
    val name = getName(func)
    (app :> s"subgraph $id ").wrap {
      app :> s"""label = "$name"""" >> LINE_SEP
      app :> s"""style = rounded""" >> LINE_SEP
      for (node <- func.nodes) addNode(node, app)
      for ((f, t) <- func.nexts) addLinear(f, t, app)
      for ((f, (t, e)) <- func.branches) addBranch(f, t, e, app)
    } >> LINE_SEP
  }

  // nodes
  def addNode(node: Node, app: Appender): Unit = {
    val id = getId(node)
    val color = getColor(node)
    val bgColor = getBgColor(node)
    app :> s"""${id}_name [shape=none, label=<<font color=$color>${node.uidString}</font>>]""" >> LINE_SEP
    app :> s"""${id}_name -> $id [arrowhead=none, color=$color, style=dashed]""" >> LINE_SEP
    app :> (node match {
      case Entry(_) =>
        s"""$id [shape=circle label=" " color=$color fillcolor=$bgColor style=filled]"""
      case Exit(_) =>
        s"""$id [shape=circle label=" " color=$color fillcolor=$bgColor style=filled]"""
      case Normal(_, inst) =>
        s"""$id [shape=box label=<<font color=$color>${norm(inst)}</font>> color=$color fillcolor=$bgColor style=filled]"""
      case Call(_, inst) =>
        s"""$id [shape=cds label=<<font color=$color>${norm(inst)}</font>> color=$color fillcolor=$bgColor style=filled]"""
      case Arrow(_, inst, fid) =>
        s"""$id [shape=hexagon label=<<font color=$color>${norm(inst)} [fid: $fid]</font>> color=$color fillcolor=$bgColor style=filled]"""
      case Branch(_, inst) =>
        s"""$id [shape=diamond label=<<font color=$color>${norm(inst.cond)}</font>> color=$color fillcolor=$bgColor style=filled]"""
      case LoopCont(_) =>
        s"""$id [shape=circle label=" " color=$color fillcolor=$bgColor style=filled]"""
    }) >> LINE_SEP
  }

  // edegs
  def addLinear(
    from: Linear,
    to: Node,
    app: Appender
  ): Unit = {
    val color = getColor(from, to)
    val fid = getId(from)
    val tid = getId(to)
    addEdge(fid, tid, color, None, app)
  }
  def addBranch(
    branch: Branch,
    thenNode: Node,
    elseNode: Node,
    app: Appender
  ): Unit = {
    val tcolor = getColor(branch, thenNode)
    val ecolor = getColor(branch, elseNode)
    val bid = getId(branch)
    val tid = getId(thenNode)
    val eid = getId(elseNode)
    addEdge(bid, tid, tcolor, Some("true"), app)
    addEdge(bid, eid, ecolor, Some("false"), app)
  }

  def addEdge(
    fid: String,
    tid: String,
    color: String,
    labelOpt: Option[String],
    app: Appender
  ): Unit = {
    app :> s"$fid -> $tid ["
    labelOpt.map(label => app >> s"label=<<font color=$color>$label</font>> ")
    app >> s"color=$color]" >> LINE_SEP
  }

  // normalize strings for ir nodes
  def norm(node: IRElem): String = {
    escapeHtml(node.toString(detail = false, line = true))
      .replaceAll("\u0000", "U+0000")
  }
}
