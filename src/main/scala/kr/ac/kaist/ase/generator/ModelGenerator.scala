package kr.ac.kaist.ase.generator

import kr.ac.kaist.ase._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.spec._
import java.io.File

object ModelGenerator {
  def apply(spec: Spec): Unit = {
    val methods = spec.globalMethods
    val builtinMethods = spec.globalMethods.filter(_.startsWith("GLOBAL."))
    val symbols = spec.symbols
    val intrinsics = spec.intrinsics
    val consts = (spec.consts.toSet - "[empty]" + "emptySyntax").toList
    val grammar = spec.grammar
    val tys = spec.tys

    List("ModelHelper", "BaseGlobal", "BaseHeap", "BuiltinHeap").foreach(filename => copyFile(
      s"$RESOURCE_DIR/$VERSION/manual/$filename.scala",
      s"$MODEL_DIR/$filename.scala"
    ))

    GrammarGenerator(grammar)
    tys.foreach { case ((tname, methods)) => TypeGenerator(tname, methods) }

    methods.foreach(name => MethodGenerator(name))
    for (file <- walkTree(s"$RESOURCE_DIR/$VERSION/manual/algorithm")) {
      val filename = file.getName
      if (scalaFilter(filename)) {
        val name = file.toString
        copyFile(name, s"$MODEL_DIR/algorithm/$filename")
      }
    }

    val nf = getPrintWriter(s"$MODEL_DIR/Model.scala")
    nf.println(s"""package kr.ac.kaist.ase.model""")
    nf.println(s"""""")
    nf.println(s"""import kr.ac.kaist.ase.core._""")
    nf.println(s"""import kr.ac.kaist.ase.util.Useful._""")
    nf.println(s"""""")
    nf.println(s"""object Model {""")
    nf.println(s"""  lazy val initState: State = State(""")
    nf.println(s"""    retValue = None,""")
    nf.println(s"""    insts = Nil,""")
    nf.println(s"""    globals = initGlobal,""")
    nf.println(s"""    locals = Map(),""")
    nf.println(s"""    heap = initHeap""")
    nf.println(s"""  )""")
    nf.println(s"""  lazy val initGlobal: Map[Id, Value] = BaseGlobal.get ++ Map(""")
    symbols.foreach {
      case (k, v) =>
        nf.println(s"""    Id("${getScalaName(k)}") -> NamedAddr("GLOBAL.$v"),""")
    }
    intrinsics.foreach {
      case (k, v) =>
        nf.println(s"""    Id("${getScalaName(k)}") -> NamedAddr("$v"),""")
    }
    nf.println(methods.map(getScalaName _).map(x =>
      s"""    Id("$x") -> $x.func""").mkString("," + LINE_SEP))
    nf.println(s"""  ) ++ Map(""")
    nf.println(consts.map(i =>
      s"""    Id("CONST_$i") -> NamedAddr("CONST_$i")""").mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""  lazy val builtinMethods: List[(String, Func)] = List(""")
    nf.println(builtinMethods.map(x =>
      s"""    ("$x", ${getScalaName(x)}.func)""").mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""  lazy val initHeap: Heap = Heap(BaseHeap.get ++ BuiltinHeap.get ++ Map(""")
    nf.println(consts.map(i =>
      s"""    NamedAddr("CONST_$i") -> Singleton("$i")""").mkString("," + LINE_SEP))
    nf.println(s"""  ) match {""")
    nf.println(s"""    case m => (m /: builtinMethods) {""")
    nf.println(s"""      case (m, (name, func)) =>""")
    nf.println(s"""        val base = removedExt(name)""")
    nf.println(s"""        val prop = getExt(name)""")
    nf.println(s"""        val addr = NamedAddr(name)""")
    nf.println(s"""        val baseAddr =""")
    nf.println(s"""          if (base == "GLOBAL") NamedAddr("GLOBAL")""")
    nf.println(s"""          else NamedAddr(s"$$base.SubMap")""")
    nf.println(s"""        val descAddr = NamedAddr(s"DESC:$$base.$$prop")""")
    nf.println(s"""        (m.get(baseAddr) match {""")
    nf.println(s"""          case Some(CoreMap(ty, map)) => m ++ List(""")
    nf.println(s"""            baseAddr -> CoreMap(ty, map + (Str(prop) -> descAddr)),""")
    nf.println(s"""            descAddr -> CoreMap(Ty("PropertyDescriptor"), Map(""")
    nf.println(s"""              Str("Value") -> addr,""")
    nf.println(s"""              Str("Writable") -> Bool(true),""")
    nf.println(s"""              Str("Enumerable") -> Bool(false),""")
    nf.println(s"""              Str("Configurable") -> Bool(true)""")
    nf.println(s"""            ))""")
    nf.println(s"""          )""")
    nf.println(s"""          case _ => m""")
    nf.println(s"""        }) + (m.get(addr) match {""")
    nf.println(s"""          case Some(CoreMap(ty, map)) =>""")
    nf.println(s"""            addr -> CoreMap(ty, map + (Str("Core") -> func))""")
    nf.println(s"""          case _ =>""")
    nf.println(s"""            addr -> CoreMap(Ty("BuiltinFunctionObject"), BuiltinFunctionObject.map ++ Map(""")
    nf.println(s"""              Str("Code") -> func,""")
    nf.println(s"""              Str("Prototype") -> NamedAddr("GLOBAL.Function.prototype"),""")
    nf.println(s"""              Str("Extensible") -> Bool(true),""")
    nf.println(s"""              Str("SubMap") -> NamedAddr(s"$$name.SubMap")""")
    nf.println(s"""            ))""")
    nf.println(s"""        })""")
    nf.println(s"""    }""")
    nf.println(s"""  })""")
    nf.println(s"""  lazy val tyMap: Map[String, Map[Value, Value]] = Map(""")
    nf.println(tys.map {
      case ((tname, _)) => s"""    ("$tname" -> $tname.map)"""
    }.mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""}""")
    nf.close()
  }
}
