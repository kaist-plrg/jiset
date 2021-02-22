package kr.ac.kaist.jiset.util

// global unique id
trait UId {
  // assign unique ids
  val uid: Int = UId.newId

  def >>[T <: UId](f: => T): T = {
    UId.fixed = this.uid
    val result = f
    UId.fixed = -1
    result
  }

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
  private var count: Int = 0
  private var fixed: Int = -1
  private def newId: Int =
    if (fixed == -1) { val uid = count; count += 1; uid }
    else fixed
  def size: Int = count
}
