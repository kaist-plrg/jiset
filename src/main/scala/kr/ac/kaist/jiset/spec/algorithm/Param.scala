package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.jiset.util.Conversion._
import kr.ac.kaist.jiset.util.{ InfNum, PInf }

case class Param(name: String, kind: Param.Kind = Param.Kind.Normal) {
  import Param.Kind._

  // count arity
  lazy val count: (InfNum, InfNum) = kind match {
    case Normal => (1, 1)
    case Optional => (0, 1)
    case Variadic => (0, PInf)
  }

  // conversion to string
  override def toString: String = {
    kind match {
      case Normal => name
      case Optional => name + "?"
      case Variadic => "..." + name
    }
  }
}
object Param {
  type Kind = Kind.Value
  object Kind extends Enumeration {
    val Normal, Optional, Variadic = Value
  }
}
