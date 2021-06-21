package kr.ac.kaist.jiset.js.builtin

import kr.ac.kaist.jiset.ir._

// map structure for internal properties
case class IMap(map: Map[Key, Value]) {
  // converstion types for heap
  def toPair(name: String, typeName: String): (Addr, Obj) = {
    val addr = NamedAddr(name)
    val valueMap = map.map { case (k, v) => k.toValue -> v }
    val subMapPair = Str(SUB_PROP) -> NamedAddr(subName(name))
    val obj = IRMap(typeName)(valueMap + subMapPair)
    addr -> obj
  }
}
object IMap {
  def apply(pairs: (String, Value)*): IMap = IMap(pairs.map {
    case (s, v) if s startsWith "@" => SymbolKey(s.drop(1)) -> v
    case (s, v) => StrKey(s) -> v
  }.toMap[Key, Value])
}
