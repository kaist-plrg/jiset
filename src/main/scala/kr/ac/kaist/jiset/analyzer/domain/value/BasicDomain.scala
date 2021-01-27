package kr.ac.kaist.jiset.analyzer.domain.value

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object BasicDomain extends generator.SetDomain[Value] with value.Domain {
  def getBooleans(elem: Elem): Set[Boolean] = ???
}
