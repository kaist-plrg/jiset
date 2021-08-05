package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.util.Useful._

// CFG components
trait CFGElem {
  // conversion to string
  override def toString: String = toString(true, false, false)

  // more detailed string
  def toString(
    detail: Boolean = true,
    line: Boolean = false,
    asite: Boolean = false
  ): String = {
    val stringifier = CFGElem.getStringifier((detail, line, asite))
    import stringifier._
    stringify(this)
  }
}
object CFGElem {
  val getStringifier = {
    cached[(Boolean, Boolean, Boolean), Stringifier](key => {
      val (detail, line, asite) = key
      new Stringifier(detail, line, asite)
    })
  }
}
