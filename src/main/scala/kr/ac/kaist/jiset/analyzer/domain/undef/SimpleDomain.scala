package kr.ac.kaist.jiset.analyzer.domain.undef

import kr.ac.kaist.jiset.ir.Undef
import kr.ac.kaist.jiset.analyzer.domain._

object SimpleDomain extends generator.SimpleDomain(Undef) with undef.Domain
