package kr.ac.kaist.jiset.spec

import org.jsoup.nodes._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.extractor.SectionParser

case class Section(id: String, subs: List[Section])
object Section {
  def apply(elem: Element): Section = SectionParser(elem)
}
