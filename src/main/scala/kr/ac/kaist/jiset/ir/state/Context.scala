package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.js.{ RETURN, TOP_LEVEL }
import kr.ac.kaist.jiset.spec.algorithm._
import scala.collection.mutable.{ Map => MMap }

case class Context(
  var cursorOpt: Option[Cursor] = None,
  val retId: Id = Id(RETURN),
  val name: String = TOP_LEVEL,
  val algo: Option[Algo] = None,
  val locals: MMap[Id, Value] = MMap()
) extends IRComponent {
  def currentInst: Option[Inst] = cursorOpt match {
    case Some(InstCursor(cur, _)) => Some(cur)
    case _ => None
  }
  def copied: Context = copy(locals = MMap.from(locals))
  def isBuiltin: Boolean = algo.fold(false)(_.isBuiltin)
}
