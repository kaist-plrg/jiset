package kr.ac.kaist.jiset.analyzer.domain.num

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.ops._

// integer abstract domain
trait Domain extends AbsDomain[Num]
  with NumericOps
