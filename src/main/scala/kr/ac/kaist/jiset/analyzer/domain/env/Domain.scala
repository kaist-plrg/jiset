package kr.ac.kaist.jiset.analyzer.domain.env

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

// object abstract domain
trait Domain extends AbsDomain[Env] {
  val Empty: Elem
}
