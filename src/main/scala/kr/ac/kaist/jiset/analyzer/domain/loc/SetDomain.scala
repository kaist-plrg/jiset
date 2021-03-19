package kr.ac.kaist.jiset.analyzer.domain.loc

import kr.ac.kaist.jiset.ir.Loc
import kr.ac.kaist.jiset.analyzer.domain._

object SetDomain extends generator.SetDomain[Loc]
  with loc.Domain
