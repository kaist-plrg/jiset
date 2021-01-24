package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.util.UId
import kr.ac.kaist.jiset.util.Useful._

// CFG functions
case class Function(
    entry: Entry,
    exit: Exit,
    nodes: Set[Node],
    forwards: Map[Node, Set[(Edge, Node)]]
) extends UId {
  // conversion to DOT
  def toDot: String = {
    val sb = new StringBuilder
    def add(str: String): Unit = sb.append(str + LINE_SEP)
    add(s"""digraph {""")
    add(s"""  graph [fontname="consolas"]""")
    add(s"""  node [fontname="consolas"]""")
    add(s"""  edge [fontname="consolas"]""")
    nodes.foreach(node => {
      val uid = node.uid
      node match {
        case Entry() =>
          add(s"""  node$uid [shape=point]""")
        case Exit() =>
          add(s"""  node$uid [shape=point]""")
        case Block(insts) =>
          add(s"""  node$uid [shape=none, margin=0, label=<""")
          add(s"""    <table border="0" cellborder="1" cellspacing="0" cellpadding="10">""")
          insts.foreach(inst => {
            add(s"""      <tr><td align="left">${norm(inst)}</td></tr>""")
          })
          add(s"""    </table>""")
          add(s"""  >]""")
        case Call(inst) =>
          add(s"""  node$uid [shape=cds, label=<${norm(inst)}>]""")
        case Branch(cond) =>
          add(s"""  node$uid [shape=diamond, label="${norm(cond)}"]""")
      }
    })
    forwards.foreach(_ match {
      case (from, set) => set.foreach {
        case (edge, to) =>
          val opt = edge match {
            case NormalEdge => ""
            case CondEdge(pass) => s""" [label="$pass"]"""
          }
          add(s"""  node${from.uid} -> node${to.uid}$opt""")
      }
    })
    add(s"""}""")
    sb.toString
  }

  // normalize beautified ires nodes
  def norm(node: IRNode): String = toSpecialCodes(beautify(node))
}
