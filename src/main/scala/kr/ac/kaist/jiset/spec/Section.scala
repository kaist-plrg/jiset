package kr.ac.kaist.jiset.spec

import org.jsoup.nodes._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.extractor.SectionParser
import kr.ac.kaist.jiset.spec.Parser

case class Section(id: String, subs: List[Section]) extends SpecComponent
object Section extends Parser[Section] {
  def apply(elem: Element): Section = SectionParser(elem)

  implicit lazy val SectionApp: App[Section] = (app, section) => {
    app >> "[" >> section.id >> "] "
    app.listWrap(section.subs)
  }
}
