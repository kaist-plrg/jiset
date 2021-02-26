package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util.Useful._
import scala.collection.mutable.Queue

class Semantics(
  inst: Inst,
  initState: State = State(Env(), Heap())
) {
  // remaining instructions
  var remains: Queue[Inst] = Queue(inst)

  // state
  var state: State = initState
}
