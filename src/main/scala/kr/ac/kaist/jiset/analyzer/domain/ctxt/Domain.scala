package kr.ac.kaist.jiset.analyzer.domain.ctxt

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.ops._

// heap abstract domain
trait Domain extends AbsDomain[Ctxt] with EmptyValue
