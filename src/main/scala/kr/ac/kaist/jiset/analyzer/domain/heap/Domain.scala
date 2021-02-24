package kr.ac.kaist.jiset.analyzer.domain.heap

import kr.ac.kaist.jiset.ir.Heap
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.ops._

// heap abstract domain
trait Domain extends AbsDomain[Heap] with EmptyValue
