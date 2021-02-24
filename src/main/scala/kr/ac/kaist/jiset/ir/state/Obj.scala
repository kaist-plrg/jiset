package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.Useful._

// objects
sealed trait Obj
case class SymbolObj(desc: String) extends Obj {
  def apply(key: Value): String = key match {
    case Str("Description") => desc
    case v => error(s"an invalid symbol field access: $v")
  }
}

case class MapObj(props: Map[String, Value]) extends Obj
object MapObj { def apply(seq: (String, Value)*): MapObj = MapObj(seq.toMap) }
case class ListObj(values: List[Value]) extends Obj
object ListObj { def apply(seq: Value*): ListObj = ListObj(seq.toList) }
