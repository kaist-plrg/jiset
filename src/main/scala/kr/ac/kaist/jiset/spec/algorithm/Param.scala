package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec.SpecComponent

case class Param(
  name: String,
  kind: Param.Kind = Param.Kind.Normal
) extends SpecComponent {
  import Param.Kind._

  def toOptional: Param = Param(name, Optional)
}
object Param {
  type Kind = Kind.Value
  object Kind extends Enumeration {
    val Normal, Optional, Variadic = Value
  }
}
