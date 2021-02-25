package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.ir.Beautifier._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.util.Useful._

class DotPrinter {
  // for control points
  def apply(cp: ControlPoint, sem: AbsSemantics): DotPrinter = {
    val func = sem.funcOf(cp)
    this(func, Option((sem, cp.view)))
  }

  // for functions
  def apply(
    func: Function,
    sem: Option[(AbsSemantics, View)] = None
  ): DotPrinter = {
    add(s"""digraph {""")
    func.nodes.foreach(node => {
      val (color, fillcolor) = sem.fold((REACH, NORMAL))(getColor(node, _))
      this(node, color, fillcolor)
    })
    func.edges.foreach(edge => {
      val color = sem.fold(REACH)(getColor(edge, _))
      this(edge, color)
    })
    add(s"""}""")
  }

  // colors
  val REACH = "black"
  val NON_REACH = "gray"
  val NORMAL = "white"
  val CURRENT = "powderblue"
  val IN_WORKLIST = "gray"

  // get colors
  def getColor(node: Node, pair: (AbsSemantics, View)): (String, String) = {
    val (sem, view) = pair
    val worklist = sem.worklist
    val np = NodePoint(node, view)
    val (color, fillcolor): (String, String) =
      if (worklist.headOption contains np) (REACH, CURRENT)
      else if (worklist has np) (REACH, IN_WORKLIST)
      else if (!sem(np).isBottom) (REACH, NORMAL)
      else (NON_REACH, NORMAL)
    (color, fillcolor)
  }

  def getColor(edge: Edge, pair: (AbsSemantics, View)): String = {
    val (sem, view) = pair
    val np = NodePoint(edge.from, view)
    if (sem(np).isBottom) NON_REACH
    else REACH
  }

  // for nodes
  def apply(
    node: Node,
    gcolor: String,
    gbgColor: String
  ): DotPrinter = {
    val uid = node.uid
    val color = s""""$gcolor""""
    val bgColor = s""""$gbgColor""""
    node match {
      case Entry() =>
        add(s"""  node$uid [shape=point color=$color fillcolor=$bgColor style=filled]""")
      case Exit() =>
        add(s"""  node$uid [shape=point color=$color fillcolor=$bgColor style=filled]""")
      case Block(insts) =>
        add(s"""  node$uid [shape=none, margin=0, label=<<font color=$color>""")
        add(s"""    <table border="0" cellborder="1" cellspacing="0" cellpadding="10">""")
        insts.foreach(inst => {
          add(s"""      <tr><td align="left">${norm(inst)}</td></tr>""")
        })
        add(s"""    </table>""")
        add(s"""  </font>> color=$color fillcolor=$bgColor style=filled]""")
      case Call(inst) =>
        add(s"""  node$uid [shape=cds, label=<<font color=$color>${norm(inst)}</font>> color=$color fillcolor=$bgColor style=filled]""")
      case Branch(cond) =>
        add(s"""  node$uid [shape=diamond, label=<<font color=$color>${norm(cond)}</font>> color=$color fillcolor=$bgColor style=filled]""")
    }
  }

  // for edges
  def apply(edge: Edge, gcolor: String): DotPrinter = {
    val color = s""""$gcolor""""
    edge match {
      case LinearEdge(from, next) =>
        add(s"""  node${from.uid} -> node${next.uid} [color=$color]""")
      case BranchEdge(from, thenNext, elseNext) =>
        add(s"""  node${from.uid} -> node${thenNext.uid} [label=<<font color=$color>true</font>> color=$color]""")
        add(s"""  node${from.uid} -> node${elseNext.uid} [label=<<font color=$color>false</font>> color=$color]""")
    }
  }

  // string builder
  private val sb: StringBuilder = new StringBuilder

  // normalize beautified ir nodes
  private def norm(node: IRNode): String = escapeHtml(beautify(node, index = true))

  // add to StringBuilder
  private def add(str: String): DotPrinter = { sb.append(str + LINE_SEP); this }

  // conversion to string
  override def toString: String = sb.toString
}
