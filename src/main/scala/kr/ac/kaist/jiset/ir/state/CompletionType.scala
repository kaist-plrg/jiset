package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.ir.{ Value => IRValue }
import kr.ac.kaist.jiset.util.Useful._

// completion type
object CompletionType extends Enumeration {
  val Normal, Break, Continue, Return, Throw, NoCompl = Value

  // address map
  val addrMap: Map[Value, Addr] = Map(
    Normal -> NamedAddr("CONST_normal"),
    Break -> NamedAddr("CONST_break"),
    Continue -> NamedAddr("CONST_continue"),
    Return -> NamedAddr("CONST_return"),
    Throw -> NamedAddr("CONST_throw"),
  )

  val revAddrMap: Map[IRValue, Value] =
    for ((k, v) <- addrMap) yield v -> k

  // conversion completion type to address
  def toAddr(v: Value): Option[Addr] = addrMap.get(v)

  // conversion address to completion type
  def toType(addr: IRValue): Option[Value] = revAddrMap.get(addr)
}
