package kr.ac.kaist.jiset.analyzer.domain.nullval

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object SimpleDomain extends generator.SimpleDomain(Null) with nullval.Domain
