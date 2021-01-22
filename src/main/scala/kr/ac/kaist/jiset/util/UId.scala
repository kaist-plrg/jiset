package kr.ac.kaist.jiset.util

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
private object UId {
  // private uid counter
  private var count: Int = 0
  private def newId: Int = { val uid = count; count += 1; uid }
}
