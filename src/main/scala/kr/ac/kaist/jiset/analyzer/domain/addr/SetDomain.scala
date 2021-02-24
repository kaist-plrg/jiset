package kr.ac.kaist.jiset.analyzer.domain.addr

import kr.ac.kaist.jiset.ir.state.Addr
import kr.ac.kaist.jiset.analyzer.domain._

object SetDomain extends generator.SetDomain[Addr]
  with addr.Domain
