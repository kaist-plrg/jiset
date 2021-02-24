package kr.ac.kaist.jiset.ir.state

// objects
sealed trait Obj
case class SymbolObj(desc: String) extends Obj
case class MapObj(props: Map[String, Value]) extends Obj
object MapObj { def apply(seq: (String, Value)*): MapObj = MapObj(seq.toMap) }
case class ListObj(values: List[Value]) extends Obj
object ListObj { def apply(seq: Value*): ListObj = ListObj(seq.toList) }
