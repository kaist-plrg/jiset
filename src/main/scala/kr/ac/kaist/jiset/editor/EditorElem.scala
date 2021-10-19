package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.util.Useful._

// Editor components
trait EditorElem {
  // conversion to string
  override def toString: String = toString(true, false, false)

  // more detailed string
  def toString(
    detail: Boolean = true,
    line: Boolean = false,
    asite: Boolean = false
  ): String = {
    val stringifier = EditorElem.getStringifier((detail, line, asite))
    import stringifier._
    stringify(this)
  }
}
object EditorElem {
  val getStringifier = {
    cached[(Boolean, Boolean, Boolean), Stringifier](key => {
      val (detail, line, asite) = key
      new Stringifier(detail, line, asite)
    })
  }
}
