package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.util.Useful._

// analyzer components
trait AnalyzerComponent {
  // conversion to string
  override def toString: String = beautified()

  // more detailed beautifier
  def beautified(
    detail: Boolean = true,
    index: Boolean = false,
    asite: Boolean = false
  ): String = {
    val beautifier = AnalyzerComponent.getBeautifier((detail, index, asite))
    import beautifier._
    beautify(this)
  }
}
object AnalyzerComponent {
  val getBeautifier = {
    cached[(Boolean, Boolean, Boolean), Beautifier](key => {
      val (detail, index, asite) = key
      new Beautifier(detail, index, asite)
    })
  }
}
