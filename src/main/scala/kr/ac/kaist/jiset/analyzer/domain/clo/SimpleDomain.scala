package kr.ac.kaist.jiset.analyzer.domain.clo

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

object SimpleDomain extends generator.SimpleDomain[Clo]
  with clo.Domain
