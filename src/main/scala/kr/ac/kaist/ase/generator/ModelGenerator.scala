package kr.ac.kaist.ase.generator

import kr.ac.kaist.ase._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.spec._

object ModelGenerator {
  def apply(spec: Spec): Unit = {
    val methods = spec.globalMethods
    val globalObjectMethods = spec.globalMethods.filter(_.startsWith("global."))
    val intrinsics = spec.intrinsics
    val consts = spec.consts
    val grammar = spec.grammar
    val tys = spec.tys
    methods.foreach(name => MethodGenerator(name))
    for (file <- walkTree(s"$RESOURCE_DIR/$VERSION/manual/algorithm")) {
      val filename = file.getName
      if (scalaFilter(filename)) {
        val name = file.toString
        val content = readFile(name)
        val nf = getPrintWriter(s"$MODEL_DIR/algorithm/$filename")
        nf.println(content)
        nf.close()
      }
    }

    GrammarGenerator(grammar)
    tys.foreach { case ((tname, methods)) => TypeGenerator(tname, methods) }

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
    nf.println(s"""  lazy val initGlobal: Map[Id, Value] = Map(""")
    intrinsics.foreach {
      case (k, v) =>
        nf.println(s"""    Id("${getScalaName(k)}") -> NamedAddr("$v"),""")
    }
    nf.println(methods.map(getScalaName _).map(x =>
      s"""    Id("$x") -> $x.func""").mkString("," + LINE_SEP))
    nf.println(s"""  ) ++ Map(""")
    nf.println(consts.map(i =>
      s"""    Id("$i") -> NamedAddr("$i")""").mkString("," + LINE_SEP))
    nf.println(s"""  ) ++""")
    nf.println(readFile(s"$RESOURCE_DIR/$VERSION/manual/Global"))
    nf.println(s"""  lazy val globalMethods: List[String] = List(""")
    nf.println(globalObjectMethods.map(x =>
      s"""    "$x"""").mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""  lazy val initHeap: Heap = Heap(Map(""")
    nf.println(consts.map(i =>
      s"""    NamedAddr("$i") -> Singleton("$i")""").mkString("," + LINE_SEP))
    nf.println(s"""  ) ++""")
    nf.println(readFile(s"$RESOURCE_DIR/$VERSION/manual/Heap") + ") match {")
    nf.println(s"""    case Heap(m, _) => Heap((m /: globalMethods) {""")
    nf.println(s"""      case (m, name) =>""")
    nf.println(s"""        val base = NamedAddr(removedExt(name))""")
    nf.println(s"""        val prop = Str(getExt(name))""")
    nf.println(s"""        m.get(base) match {""")
    nf.println(s"""          case Some(CoreMap(ty, map)) => m + (base -> CoreMap(ty, map + (prop -> NamedAddr(name))))""")
    nf.println(s"""          case _ => m""")
    nf.println(s"""        }""")
    nf.println(s"""    })""")
    nf.println(s"""  }""")
    nf.println(s"""  lazy val tyMap: Map[String, Map[Value, Value]] = Map(""")
    nf.println(tys.map {
      case ((tname, _)) => s"""    ("$tname" -> $tname.map)"""
    }.mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""}""")
    nf.close()
  }
}
// TODO
// nf.println(s"""        (NamedAddr(name) -> CoreMap(Ty("BuiltinFunctionObject"), Map(""")
// nf.println(s"""          Str("Code") -> getScalaName(name).func""")
// nf.println(s"""        )))""")
