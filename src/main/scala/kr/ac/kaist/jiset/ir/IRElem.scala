package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.Useful._

// IR components
trait IRElem {
  // conversion to string
  def beautified: String = beautified()
  def beautified(
    detail: Boolean = true,
    line: Boolean = false,
    asite: Boolean = false
  ): String = {
    val beautifier = IRElem.getBeautifier((detail, line, asite))
    import beautifier._
    beautify(this)
  }
}
object IRElem {
  val getBeautifier = {
    cached[(Boolean, Boolean, Boolean), Beautifier](key => {
      val (detail, line, asite) = key
      new Beautifier(detail, line, asite)
    })
  }
}
