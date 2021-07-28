package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.spec.algorithm.Algo
import scala.collection.mutable.{ Map => MMap }

// Walker for setting ids for IR instructions
class InstIdSetter extends UnitWalker {
  // counter for ids
  private var counter: Int = 0

  // get a new id
  private def getId: Int = { val id = counter; counter += 1; id }

  // mapping from ids to instructions
  private var idMap: Map[Int, Inst] = Map()
  def getIdMap: Map[Int, Inst] = idMap

  // set ids to instructions
  override def walk(inst: Inst): Unit = {
    val uid = getId
    inst.setUId(uid)
    idMap += uid -> inst
    super.walk(inst)
  }
}
