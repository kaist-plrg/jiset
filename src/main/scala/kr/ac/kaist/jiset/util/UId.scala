package kr.ac.kaist.jiset.util

import kr.ac.kaist.jiset.error.WrongUId

// unique id
trait UId[T <: UId[T]] { _: T =>
  val uidGen: UIdGen[T]

  // assign unique ids
  val uid: Int = uidGen.newId

  // store this to uidMap
  uidGen.uidMap += uid -> this

  // get simple string
  def uidString: String = s"${getClass.getSimpleName}[$uid]"

  // override equality comparison using unique ids
  override def equals(that: Any): Boolean = that match {
    case that: UId[T] => (
      (this.uidGen eq that.uidGen) &&
      (this.uid == that.uid)
    )
    case _ => false
  }

  // override hashCode using unique ids
  override def hashCode: Int = uid
}

// unique id generator
class UIdGen[T <: UId[T]] {
  // private uid counter
  private[util] var count = 0
  private[util] def newId: Int = { val uid = count; count += 1; uid }
  private[util] var uidMap: Map[Int, T] = Map()
  def get(uid: Int): T = uidMap.getOrElse(uid, throw WrongUId(uid))
  def size: Int = count
}
