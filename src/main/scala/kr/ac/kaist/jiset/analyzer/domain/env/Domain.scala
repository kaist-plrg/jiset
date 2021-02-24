package kr.ac.kaist.jiset.analyzer.domain.env

import kr.ac.kaist.jiset.ir.Env
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.ops._

// object abstract domain
trait Domain extends AbsDomain[Env] with EmptyValue
