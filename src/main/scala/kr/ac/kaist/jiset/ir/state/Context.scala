package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.checker._
import kr.ac.kaist.jiset.js.{ RETURN, TOP_LEVEL }
import kr.ac.kaist.jiset.js.ast.AST
import kr.ac.kaist.jiset.spec.algorithm._
import scala.collection.mutable.{ Map => MMap }

case class Context(
  var cursorOpt: Option[Cursor] = None,
  val retId: Id = Id(RETURN),
  val name: String = TOP_LEVEL,
  val astOpt: Option[AST] = None,
  val algo: Option[Algo] = None,
  val locals: MMap[Id, Value] = MMap(),
  val viewOpt: Option[View] = None
) extends IRComponent {
  def copied: Context = copy(locals = MMap.from(locals))
  def isBuiltin: Boolean = algo.fold(false)(_.isBuiltin)
  // save previous cursor for stack frame info
  private var prevCursorOpt: Option[Cursor] = None
  // move cursor
  def moveNext: Unit = {
    prevCursorOpt = cursorOpt
    cursorOpt = cursorOpt.flatMap(_.next)
  }
  // debugger info
  def getAlgoName: String = algo match {
    case Some(algo) => algo.name
    case None => name
  }
  private def getLine(cur: Option[Cursor]): Int = cur.flatMap(_.inst) match {
    case Some(inst) => inst.line.getOrElse(-1)
    case None => -1
  }
  def getInfo(
    fromPrev: Boolean = false
  ): (String, Int, List[(String, String)]) = (
    getAlgoName,
    getLine(if (fromPrev) prevCursorOpt else cursorOpt),
    locals.toList.map {
      case (Id(name), v) => (name, v.beautified)
    }
  )
  // check if AST evaluation
  def isAstEvaluation: Boolean =
    astOpt.nonEmpty && algo.fold(false)(_.head match {
      case s: SyntaxDirectedHead => s.methodName == "Evaluation"
      case _ => false
    })
}
