package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.util.Useful._

// CFG components
trait CFGElem {
  // conversion to string
  def beautified: String = beautified()
  def beautified(
    detail: Boolean = true,
    line: Boolean = false,
    asite: Boolean = false
  ): String = {
    val beautifier = CFGElem.getBeautifier((detail, line, asite))
    import beautifier._
    beautify(this)
  }
}
object CFGElem {
  val getBeautifier = {
    cached[(Boolean, Boolean, Boolean), Beautifier](key => {
      val (detail, line, asite) = key
      new Beautifier(detail, line, asite)
    })
  }
}
