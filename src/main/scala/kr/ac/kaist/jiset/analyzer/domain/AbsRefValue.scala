package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.analyzer.AnalyzerElem
import kr.ac.kaist.jiset.ir._

// basic abstract reference values
sealed trait AbsRefValue extends AnalyzerElem
case class AbsRefId(id: Id) extends AbsRefValue
case class AbsRefProp(base: AbsValue, prop: AbsValue) extends AbsRefValue
