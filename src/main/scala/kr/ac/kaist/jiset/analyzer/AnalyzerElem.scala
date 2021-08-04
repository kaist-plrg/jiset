package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.util.Useful._

// analyzer components
trait AnalyzerElem {
  // conversion to string
  override def toString: String = beautified()

  // more detailed beautifier
  def beautified(
    detail: Boolean = true,
    index: Boolean = false,
    asite: Boolean = false
  ): String = {
    val beautifier = AnalyzerElem.getBeautifier((detail, index, asite))
    import beautifier._
    beautify(this)
  }
}
object AnalyzerElem {
  val getBeautifier = {
    cached[(Boolean, Boolean, Boolean), Beautifier](key => {
      val (detail, index, asite) = key
      new Beautifier(detail, index, asite)
    })
  }
}
