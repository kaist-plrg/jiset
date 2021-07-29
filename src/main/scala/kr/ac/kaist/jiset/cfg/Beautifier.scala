package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// CFG Beautifier
class Beautifier(
  detail: Boolean = true,
  index: Boolean = false,
  asite: Boolean = false
) {
  val irBeautifier = new ir.Beautifier(detail, index, asite)
  import irBeautifier._

  // CFG components
  implicit lazy val CFGComponentApp: App[CFGComponent] = (app, comp) => comp match {
    case comp: CFG => CFGApp(app, comp)
    case comp: Function => FunctionApp(app, comp)
    case comp: Node => NodeApp(app, comp)
    case comp: Origin => OriginApp(app, comp)
  }

  // TODO CFGs
  implicit lazy val CFGApp: App[CFG] = ???

  // TODO CFG functions
  implicit lazy val FunctionApp: App[Function] = ???

  // CFG nodes
  implicit lazy val NodeApp: App[Node] = (app, node) => {
    app >> node.toString
    node match {
      case Entry(_) => app
      case Normal(_, inst) => app >> inst
      case Call(_, inst) => app >> inst
      case Arrow(_, inst, fid) => app >> inst >> " [fid: " >> fid >> "]"
      case Branch(_, inst) => app >> inst
      case Exit(_) => app
    }
  }

  // TODO CFG origins
  implicit lazy val OriginApp: App[Origin] = ???
}
