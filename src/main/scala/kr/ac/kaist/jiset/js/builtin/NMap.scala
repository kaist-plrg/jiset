package kr.ac.kaist.jiset.js.builtin

import kr.ac.kaist.jiset.ir._

// map structure for normal properties
case class NMap(pairs: List[(Key, Property)]) {
  // converstion types for heap
  def toPair(name: String): (Addr, Obj) = {
    val addr = NamedAddr(subName(name))
    val valueMap = pairs.map {
      case (k, _) => k.toValue -> NamedAddr(descName(name, k))
    }
    val obj = IRMap(SUB_PROP)(valueMap)
    addr -> obj
  }

  // get descriptors
  def getDescs(name: String): List[(Addr, Obj)] = pairs.map {
    case (key, prop) => getDesc(name, key, prop)
  }

  // get descriptor
  def getDesc(name: String, key: Key, prop: Property): (Addr, Obj) = {
    NamedAddr(descName(name, key)) -> IRMap("PropertyDescriptor")(prop match {
      case DataProperty(v, w, e, c) => Map(
        Str("Value") -> v,
        Str("Writable") -> Bool(w),
        Str("Enumerable") -> Bool(e),
        Str("Configurable") -> Bool(c)
      )
      case AccessorProperty(g, s, e, c) => Map(
        Str("Get") -> g,
        Str("Set") -> s,
        Str("Enumerable") -> Bool(e),
        Str("Configurable") -> Bool(c)
      )
    })
  }
}
object NMap {
  def apply(pairs: (String, Property)*): NMap = NMap(pairs.map {
    case (s, p) if s startsWith "@" => SymbolKey(s.drop(1)) -> p
    case (s, p) => StrKey(s) -> p
  }.toList)
}
