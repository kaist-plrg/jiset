package kr.ac.kaist.jiset.spec.algorithm

case class Param(name: String, kind: Param.Kind = Param.Kind.Normal) {
  import Param.Kind._

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
