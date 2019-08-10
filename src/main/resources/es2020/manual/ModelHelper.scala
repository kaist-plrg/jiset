package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.error.NotSupported
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

  val SYMBOL_PREFIX = "GLOBAL.Symbol."
  def getPropStr(value: Value): String = value match {
    case Str(str) => s".$str"
    case _ => s"[${beautify(value)}]"
  }

  def addBuiltin(
    map: Map[Addr, Obj],
    builtinMethods: List[(String, Int, Func)]
  ): Map[Addr, Obj] = (map /: builtinMethods) {
    case (m, (givenName, length, func)) =>
      val base = removedExt(givenName)
      val prop = getExt(givenName)
      val (propV, propName) = if (prop.startsWith("SYMBOL_")) {
        val p = prop.substring("SYMBOL_".length)
        (NamedAddr(s"GLOBAL.Symbol.$p"), s"[Symbol.$p]")
      } else (Str(prop), prop)
      val name = base + getPropStr(propV)
      val addr = NamedAddr(name)
      val baseAddr =
        if (base == "GLOBAL") NamedAddr("GLOBAL")
        else NamedAddr(s"$base.SubMap")
      val descAddr = NamedAddr(s"DESC:$name")
      (m.get(baseAddr) match {
        case Some(CoreMap(ty, map)) => m ++ List(
          baseAddr -> CoreMap(ty, map + (propV -> descAddr)),
          descAddr -> m.getOrElse(descAddr, CoreMap(Ty("PropertyDescriptor"), Map(
            Str("Value") -> addr,
            Str("Writable") -> Bool(true),
            Str("Enumerable") -> Bool(false),
            Str("Configurable") -> Bool(true)
          )))
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
          addr -> CoreMap(Ty("BuiltinFunctionObject"), BuiltinFunctionObject.map - Str("Construct") ++ Map(
            Str("Code") -> func,
            Str("Prototype") -> NamedAddr("GLOBAL.Function.prototype"),
            Str("Extensible") -> Bool(true),
            Str("ScriptOrModule") -> Null,
            Str("Realm") -> NamedAddr("REALM"),
            Str("SubMap") -> NamedAddr(s"$name.SubMap")
          ))
      }) ++ List(
        NamedAddr(s"$name.SubMap") -> (m.getOrElse(NamedAddr(s"$name.SubMap"), CoreMap(Ty("SubMap"), Map())) match {
          case CoreMap(ty, map) => CoreMap(ty, map ++ List(
            Str("name") -> map.getOrElse(Str("name"), NamedAddr(s"DESC:$name.name")),
            Str("length") -> map.getOrElse(Str("length"), NamedAddr(s"DESC:$name.length"))
          ))
          case obj => error(s"not a map: $obj")
        }),
        NamedAddr(s"DESC:$name.name") -> m.getOrElse(NamedAddr(s"DESC:$name.name"), CoreMap(Ty("PropertyDescriptor"), Map(
          Str("Value") -> Str(propName),
          Str("Writable") -> Bool(false),
          Str("Enumerable") -> Bool(false),
          Str("Configurable") -> Bool(true)
        ))),
        NamedAddr(s"DESC:$name.length") -> m.getOrElse(NamedAddr(s"DESC:$name.length"), CoreMap(Ty("PropertyDescriptor"), Map(
          Str("Value") -> Num(length),
          Str("Writable") -> Bool(false),
          Str("Enumerable") -> Bool(false),
          Str("Configurable") -> Bool(true)
        )))
      )
  }

  private val notSupportedSyntaxPrefixList = List("RegularExpression")
  def checkSupported(ast: AST): Unit = ast.exists {
    case name => notSupportedSyntaxPrefixList.exists {
      case pre =>
        if (name.startsWith(pre)) throw NotSupported(pre)
        else false
    }
  }
}
