package kr.ac.kaist.jiset.util

import java.util.concurrent.atomic.AtomicInteger

// global unique id
trait UId {
  // assign unique ids
  val uid: Int = UId.newId

  // override equality comparison using unique ids
  override def equals(that: Any): Boolean = that match {
    case that: UId => this.uid == that.uid
    case _ => false
  }

  // override hashCode using unique ids
  override def hashCode: Int = uid
}
object UId {
  // private uid counter
  private val counter = new AtomicInteger
  private def newId: Int = counter.getAndIncrement
  def size: Int = counter.get
}
