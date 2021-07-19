package kr.ac.kaist.jiset.util

import java.util.concurrent.atomic.AtomicInteger

// unique id
trait UId {
  val uidGen: UIdGen

  // assign unique ids
  val uid: Int = uidGen.newId

  // override equality comparison using unique ids
  override def equals(that: Any): Boolean = that match {
    case that: UId => (
      (this.uidGen eq that.uidGen) &&
      (this.uid == that.uid)
    )
    case _ => false
  }

  // override hashCode using unique ids
  override def hashCode: Int = uid
}

// unique id generator
class UIdGen {
  // private uid counter
  private[util] val counter = new AtomicInteger
  private[util] def newId: Int = counter.getAndIncrement
  def size: Int = counter.get
}
