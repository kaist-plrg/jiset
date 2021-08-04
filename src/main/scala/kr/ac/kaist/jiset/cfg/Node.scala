package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.{ UId, UIdGen }
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._

// CFG nodes
trait Node extends CFGComponent with UId[Node] {
  // get simple string
  def simpleString: String = s"${getClass.getSimpleName}[$uid]"

  // conversion to string with instructions
  def beautified(
    detail: Boolean = true,
    index: Boolean = false,
    asite: Boolean = false
  ): String = {
    // load IR beautifier
    val irBeautifier = IRComponent.getBeautifier((detail, index, asite))
    import irBeautifier._

    // define appender
    val app = new Appender
    app >> this.toString
    this match {
      case Entry(_) =>
      case Normal(_, inst) => app >> " " >> inst
      case Call(_, inst) => app >> " " >> inst
      case Arrow(_, inst, fid) => app >> " " >> inst >> " [fid: " >> fid >> "]"
      case Branch(_, inst) => app >> " " >> inst
      case Exit(_) =>
    }
    app.toString
  }
}

// linear nodes
trait Linear extends Node

// entry nodes
case class Entry(uidGen: UIdGen[Node]) extends Linear

// normal nodes
case class Normal(uidGen: UIdGen[Node], inst: NormalInst) extends Linear

// call nodes
case class Call(uidGen: UIdGen[Node], inst: CallInst) extends Linear

// arrow nodes
case class Arrow(uidGen: UIdGen[Node], inst: ArrowInst, fid: Int) extends Linear

// branches
case class Branch(uidGen: UIdGen[Node], inst: CondInst) extends Node

// exit nodes
case class Exit(uidGen: UIdGen[Node]) extends Node
