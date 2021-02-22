package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.util.Useful._

class DotPrinter {
  // for functions
  def apply(func: Function): DotPrinter = {
    val Function(_, entry, exit, nodes) = func
    add(s"""digraph {""")
    nodes.foreach(apply)

    nodes.foreach {
      case (n: LinearNode) => apply(n, NormalEdge, n.next.get)
      case n @ Branch(_, t, f) =>
        apply(n, CondEdge(true), t.get)
        apply(n, CondEdge(false), f.get)
      case _ =>
    }

    add(s"""}""")
    this
  }

  // for nodes
  def apply(node: Node): DotPrinter = {
    val uid = node.uid
    node match {
      case Entry(_) =>
        add(s"""  node$uid [shape=point]""")
      case Exit() =>
        add(s"""  node$uid [shape=point]""")
      case Block(insts, _) =>
        add(s"""  node$uid [shape=none, margin=0, label=<""")
        add(s"""    <table border="0" cellborder="1" cellspacing="0" cellpadding="10">""")
        insts.foreach(inst => {
          add(s"""      <tr><td align="left">${norm(inst)}</td></tr>""")
        })
        add(s"""    </table>""")
        add(s"""  >]""")
      case Call(inst, _) =>
        add(s"""  node$uid [shape=cds, label=<${norm(inst)}>]""")
      case Branch(cond, _, _) =>
        add(s"""  node$uid [shape=diamond, label="${norm(cond)}"]""")
    }
    this
  }

  // for edges
  def apply(from: Node, edge: Edge, to: Node): DotPrinter = {
    val opt = edge match {
      case NormalEdge => ""
      case CondEdge(pass) => s""" [label="$pass"]"""
    }
    add(s"""  node${from.uid} -> node${to.uid}$opt""")
    this
  }

  // string builder
  private val sb: StringBuilder = new StringBuilder

  // normalize beautified ir nodes
  private def norm(node: IRNode): String = escapeHtml(beautify(node, index = true))

  // add to StringBuilder
  private def add(str: String): Unit =
    sb.append(str + LINE_SEP)

  // conversion to string
  override def toString: String = sb.toString
}
