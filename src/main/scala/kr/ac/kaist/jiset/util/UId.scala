package kr.ac.kaist.jiset.util

import java.util.concurrent.atomic.AtomicInteger
import kr.ac.kaist.jiset.error.WrongUId

// unique id
trait UId[T <: UId[T]] { _: T =>
  val uidGen: UIdGen[T]

  // assign unique ids
  val uid: Int = uidGen.newId

  // store this to uidMap
  uidGen.uidMap += uid -> this

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
  private[util] val counter = new AtomicInteger
  private[util] def newId: Int = counter.getAndIncrement
  private[util] var uidMap: Map[Int, T] = Map()
  def get(uid: Int): T =
    uidMap.getOrElse(uid, throw WrongUId(uid))
  def size: Int = counter.get
}
