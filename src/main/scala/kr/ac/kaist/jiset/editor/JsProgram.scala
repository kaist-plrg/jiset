package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.{ cfg => CFG, _ }
import kr.ac.kaist.jiset.ir.{ State, NodeCursor, Interp }
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.WeakUId
import kr.ac.kaist.jiset.EDITOR_LOG_DIR

// js program
case class JsProgram(
  script: Script,
  touched: Array[Boolean],
  execTime: Int
) extends WeakUId {
  // raw string of js program
  def raw: String = script.toString

  // size of js program
  def size: Int = raw.length

  // get touched node ids
  def touchedNIds: Array[Int] =
    touched.zipWithIndex.flatMap {
      case (true, nid) => Some(nid)
      case _ => None
    }

  // equals
  override def equals(that: Any): Boolean = that match {
    case that: JsProgram => this.uid == that.uid
    case _ => false
  }

  // override hashCode
  override def hashCode: Int = uid
}

object JsProgram {
  def fromScript(script: Script): JsProgram = {
    // init touched
    val touched = Array.fill(cfg.nodes.size)(false)

    // run interp and dump touched result
    val initState = Initialize(script, None, NodeCursor)
    val interp = new Interp(initState, useHook = true)

    // subscribe step event in interp
    interp.subscribe(Interp.Event.Step, { st =>
      st.context.cursorOpt.get match {
        case NodeCursor(n) => { touched(n.uid) = true }
        case _ =>
      }
    })

    // fixpoint and measure time
    val (execTime, _) = time(interp.fixpoint)

    // create js program
    JsProgram(script, touched, execTime.toInt)
  }
}
