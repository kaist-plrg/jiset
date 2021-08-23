package kr.ac.kaist.jiset.util

// weak unique ids
trait WeakUId {
  // unique ids
  protected var uidOpt: Option[Int] = None
  def setUId(uid: Int): this.type = { uidOpt = Some(uid); this }
  def uid: Int = uidOpt.getOrElse(-1)

  // get simple string
  def uidString: String =
    getClass.getSimpleName + uidOpt.fold("")(k => s"[$k]")
}
