package kr.ac.kaist.jiset.viewer

import kr.ac.kaist.jiset.util.Useful._

// Viewer components
trait ViewerElem {
  // conversion to string
  override def toString: String = toString(true, false, false)

  // more detailed string
  def toString(
    detail: Boolean = true,
    line: Boolean = false,
    asite: Boolean = false
  ): String = {
    val stringifier = ViewerElem.getStringifier((detail, line, asite))
    import stringifier._
    stringify(this)
  }
}
object ViewerElem {
  val getStringifier = {
    cached[(Boolean, Boolean, Boolean), Stringifier](key => {
      val (detail, line, asite) = key
      new Stringifier(detail, line, asite)
    })
  }
}
