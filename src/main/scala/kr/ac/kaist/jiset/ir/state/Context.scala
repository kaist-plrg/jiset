package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.js.{ RETURN, TOP_LEVEL }
import scala.collection.mutable.{ Map => MMap }

case class Context(
  val retId: Id = Id(RETURN),
  val name: String = TOP_LEVEL,
  var insts: List[Inst] = Nil,
  val locals: MMap[Id, Value] = MMap()
) extends IRNode {
  def copied: Context = copy(locals = MMap.from(locals))
}
