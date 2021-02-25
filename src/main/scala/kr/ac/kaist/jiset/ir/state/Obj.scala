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

case class ListObj(values: List[Value]) extends Obj {
  def pop(idx: Value): (Value, ListObj) = idx match {
    case INum(i) =>
      val k = i.toInt
      if (k < 0 || k >= values.length) error(s"Out of range for ListObj pop")
      (values(k), ListObj(values.slice(0, k) ++ values.slice(k + 1, values.length)))
    case _ => error(s"Non INum given as ListObj index")
  }
}
object ListObj { def apply(seq: Value*): ListObj = ListObj(seq.toList) }
