package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.util.Useful._

object ModelHelper {
  def flattenStList(s: StatementList): List[StatementListItem] = s match {
    case StatementList0(x0, _) => List(x0)
    case StatementList1(x0, x1, _) => flattenStList(x0) :+ x1
  }

  def flattenStatement(s: Script) = s match {
    case Script0(Some(ScriptBody0(stlist, _)), _) =>
      flattenStList(stlist)
    case _ => List()
  }

  def mergeStatement(l: List[StatementListItem]): Script = Script0(l match {
    case a :: rest => Some(ScriptBody0(rest.foldLeft[StatementList](StatementList0(a, List(false, false, false))) {
      case (x, y) => StatementList1(x, y, List(false, false, false))
    }, List(false, false, false)))
    case Nil => None
  }, List(false, false, false))

  def addBuiltin(
    map: Map[Addr, Obj],
    builtinMethods: List[(String, Func)]
  ): Map[Addr, Obj] = (map /: builtinMethods) {
    case (m, (name, func)) =>
      val base = removedExt(name)
      val prop = getExt(name)
      val addr = NamedAddr(name)
      val baseAddr =
        if (base == "GLOBAL") NamedAddr("GLOBAL")
        else NamedAddr(s"$base.SubMap")
      val descAddr = NamedAddr(s"DESC:$base.$prop")
      (m.get(baseAddr) match {
        case Some(CoreMap(ty, map)) => m ++ List(
          baseAddr -> CoreMap(ty, map + (Str(prop) -> descAddr)),
          descAddr -> CoreMap(Ty("PropertyDescriptor"), Map(
            Str("Value") -> addr,
            Str("Writable") -> Bool(true),
            Str("Enumerable") -> Bool(false),
            Str("Configurable") -> Bool(true)
          ))
        )
        case _ => m
      }) + (m.get(addr) match {
        case Some(CoreMap(ty, map)) =>
          addr -> CoreMap(ty, map ++ Map(
            Str("Extensible") -> Bool(true),
            Str("ScriptOrModule") -> Null,
            Str("Realm") -> NamedAddr("REALM")
          ))
        case _ =>
          addr -> CoreMap(Ty("BuiltinFunctionObject"), BuiltinFunctionObject.map ++ Map(
            Str("Code") -> func,
            Str("Prototype") -> NamedAddr("GLOBAL.Function.prototype"),
            Str("Extensible") -> Bool(true),
            Str("ScriptOrModule") -> Null,
            Str("Realm") -> NamedAddr("REALM"),
            Str("SubMap") -> NamedAddr(s"$name.SubMap")
          ))
      }) ++ List(
        NamedAddr(s"$name.SubMap") -> m.getOrElse(NamedAddr(s"$name.SubMap"), CoreMap(Ty("SubMap"), Map(
          Str("name") -> NamedAddr(s"DESC:$name.name")
        ))),
        NamedAddr(s"DESC:$name.name") -> m.getOrElse(NamedAddr(s"DESC:$name.name"), CoreMap(Ty("PropertyDescriptor"), Map(
          Str("Value") -> Str(prop),
          Str("Writable") -> Bool(false),
          Str("Enumerable") -> Bool(false),
          Str("Configurable") -> Bool(true)
        )))
      )
  }
}
