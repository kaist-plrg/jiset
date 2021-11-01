package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.{ cfg => CFG, _ }
import kr.ac.kaist.jiset.ir.{ State, NodeCursor, Interp }
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.WeakUId
import kr.ac.kaist.jiset.EDITOR_LOG_DIR

// js program in filtered mapping
class JsProgram(
  script: Script,
  var touchedFile: Option[String] = None
) extends WeakUId {
  // touched nodes
  lazy val touched: Array[Boolean] =
    JsProgram.getTouched(script, touchedFile)

  // raw string of js program
  def raw: String = script.toString

  // size of js program
  def size: Int = raw.length

  // get original script
  def getScript: Script = script

  // equals
  override def equals(that: Any): Boolean = that match {
    case that: JsProgram => this.uid == that.uid
    case _ => false
  }

  // override hashCode
  override def hashCode: Int = uid

  // save touched result
  def saveTouched(): Unit = {
    val filename = s"$EDITOR_CACHED_DIR/$uid.json"
    val data = touched.zipWithIndex.flatMap {
      case (true, nid) => Some(nid)
      case _ => None
    }
    dumpJson(data, filename, true)
    touchedFile = Some(s"$uid")
  }
}
object JsProgram {
  // factory
  def apply(
    pid: Int,
    script: Script,
    touchedFile: Option[String] = None
  ): JsProgram =
    (new JsProgram(script, touchedFile)).setUId(pid)

  // get touched nodes by interp script
  def getTouched(script: Script, touchedFile: Option[String]): Array[Boolean] = {
    val _touched = Array.fill(cfg.nodes.size)(false)
    val cached = touchedFile.map(uid => s"$EDITOR_CACHED_DIR/$uid.json")

    // check if cached result exists
    cached match {
      case Some(cached) if exists(cached) =>
        // read touched from cached
        val data = readJson[Array[Int]](cached)
        data.foreach(nid => _touched(nid) = true)
        _touched
      case _ =>
        // run interp and dump touched result
        val initState = Initialize(script, None, NodeCursor)
        val interp = new Interp(initState, useHook = true)

        // subscribe step event in interp
        interp.subscribe(Interp.Event.Step, { st =>
          st.context.cursorOpt.get match {
            case NodeCursor(n) => { _touched(n.uid) = true }
            case _ =>
          }
        })

        // fixpoint
        interp.fixpoint

        // cache touched result
        cached.foreach(cached => {
          val data = _touched.zipWithIndex.flatMap {
            case (true, nid) => Some(nid)
            case _ => None
          }
          dumpJson(data, cached, true)
        })
        _touched
    }
  }
}
