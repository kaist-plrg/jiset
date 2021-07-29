package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.cfg.Beautifier

trait CFGComponent {
  def beautified: String = beautified()
  def beautified(
    detail: Boolean = true,
    index: Boolean = false,
    asite: Boolean = false
  ): String = {
    val beautifier = CFGComponent.getBeautifier((detail, index, asite))
    import beautifier._
    beautify(this)
  }
}
object CFGComponent {
  private val getBeautifier = {
    cached[(Boolean, Boolean, Boolean), Beautifier](key => {
      val (detail, index, asite) = key
      new Beautifier(detail, index, asite)
    })
  }
}
