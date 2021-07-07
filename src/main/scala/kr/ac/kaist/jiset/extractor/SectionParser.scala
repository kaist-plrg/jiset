package kr.ac.kaist.jiset.extractor

import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.spec.Section
import org.jsoup.nodes._

object SectionParser {
  def apply(elem: Element): Section = {
    val id = elem.id
    val subs = for {
      child <- toArray(elem.children).toList
      if child.tagName == "emu-clause"
    } yield apply(child)
    Section(id, subs)
  }
}
