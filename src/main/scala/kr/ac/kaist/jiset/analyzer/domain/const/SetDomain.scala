package kr.ac.kaist.jiset.analyzer.domain.const

import kr.ac.kaist.jiset.ir.Const
import kr.ac.kaist.jiset.analyzer.domain._

object SetDomain extends generator.SetDomain[Const]
  with const.Domain
