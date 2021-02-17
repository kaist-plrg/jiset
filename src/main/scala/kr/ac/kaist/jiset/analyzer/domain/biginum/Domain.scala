package kr.ac.kaist.jiset.analyzer.domain.biginum

import kr.ac.kaist.jiset.analyzer.domain.ops._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.ires.ir.BigINum

// big integer abstract domain
trait Domain extends AbsDomain[BigINum]
  with NumericOps
  with BitwiseOps
  with ShiftOps