package kr.ac.kaist.jiset.analyzer.domain.symb

import kr.ac.kaist.jiset.ir.Symb
import kr.ac.kaist.jiset.analyzer.domain._

object SetDomain extends generator.SetDomain[Symb]
  with symb.Domain
