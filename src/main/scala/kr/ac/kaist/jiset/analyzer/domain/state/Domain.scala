package kr.ac.kaist.jiset.analyzer.domain.state

import kr.ac.kaist.jiset.ir.state.State
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.ops._

// state abstract domain
trait Domain extends AbsDomain[State] with EmptyValue
