package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.util.Useful._

// CFG components
trait CFGElem {
  // conversion to string
  def beautified: String = beautified()
  def beautified(
    detail: Boolean = true,
    index: Boolean = false,
    asite: Boolean = false
  ): String = {
    val beautifier = CFGElem.getBeautifier((detail, index, asite))
    import beautifier._
    beautify(this)
  }
}
object CFGElem {
  val getBeautifier = {
    cached[(Boolean, Boolean, Boolean), Beautifier](key => {
      val (detail, index, asite) = key
      new Beautifier(detail, index, asite)
    })
  }
}
