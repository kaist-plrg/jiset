package kr.ac.kaist.jiset.analyzer.domain.ty

import kr.ac.kaist.jiset.ir.Ty
import kr.ac.kaist.jiset.analyzer.domain._

object SetDomain extends generator.SetDomain[Ty]
  with ty.Domain {
  // constructor
  def apply(seq: String*): Elem = VSet(seq.map(Ty(_)).toSet)
}
