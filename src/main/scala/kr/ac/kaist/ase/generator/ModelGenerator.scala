package kr.ac.kaist.ase.generator

import kr.ac.kaist.ase._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.spec._

object ModelGenerator {
  def apply(spec: Spec): Unit = {
    val methods = spec.globalMethods
    val globalObjectMethods = spec.globalMethods.filter(_.startsWith("Global.")).toSet
    val consts = spec.consts
    val grammar = spec.grammar
    val tys = spec.tys
    methods.foreach(name => MethodGenerator(name))
    GrammarGenerator(grammar)
    tys.foreach { case ((tname, methods)) => TypeGenerator(tname, methods) }

    val nf = getPrintWriter(s"$MODEL_DIR/Model.scala")
    nf.println(s"""package kr.ac.kaist.ase.model""")
    nf.println(s"""""")
    nf.println(s"""import kr.ac.kaist.ase.manualModel._""")
    nf.println(s"""import kr.ac.kaist.ase.core._""")
    nf.println(s"""object Model {""")
    nf.println(s"""  lazy val initState: State = State(""")
    nf.println(s"""    retValue = None,""")
    nf.println(s"""    insts = Nil,""")
    nf.println(s"""    globals = initGlobal,""")
    nf.println(s"""    locals = Map(),""")
    nf.println(s"""    heap = initHeap""")
    nf.println(s"""  )""")
    nf.println(s"""  lazy val initGlobal: Map[Id, Value] = Map(""")
    nf.println(s"""    Id("Global") -> NamedAddr("Global"),""")
    nf.println(methods.map(getScalaName _).map(x =>
      s"""    Id("$x") -> $x.func""").mkString("," + LINE_SEP))
    nf.println(s"""  ) ++ Map(""")
    nf.println(consts.map(i =>
      s"""    Id("$i") -> NamedAddr("$i")""").mkString("," + LINE_SEP))
    nf.println(s"""  ) ++ ManualModel.initGlobal""")
    nf.println(s"""  private lazy val EMPTY: CoreMap = CoreMap(Ty("OrdinaryObject"), tyMap("OrdinaryObject"))""")
    nf.println(s"""  lazy val initHeap: Heap = Heap(Map(""")

    (Map[String, Set[String]]() /: globalObjectMethods) {
      case (m, path) =>
        val (resM, _) = ((m, "Global") /: path.split('.').tail) {
          case ((m, base), x) =>
            val name = s"$base.$x"
            (m + (base -> (m.getOrElse(base, Set()) + x), name -> m.getOrElse(name, Set())), name)
        }
        resM
    }.foreach {
      case (name, list) =>
        nf.println(s"""    NamedAddr("$name") -> EMPTY""")
        if (globalObjectMethods contains name)
          nf.println(s"""     .updated(Str("Code"), ${getScalaName(name)}.func)""")
        nf.println(s"""     .updated(Str("SubMap"), NamedAddr("$name.SubMap")),""")
        nf.println(s"""    NamedAddr("$name.SubMap") -> CoreMap(Ty("SubMap"), Map(""")
        nf.println(list.map(x => s"""      Str("$x") -> NamedAddr("$name.$x")""").mkString("," + LINE_SEP))
        nf.println(s"""    )),""")
    }

    nf.println(consts.map(i =>
      s"""    NamedAddr("$i") -> Singleton("$i")""").mkString("," + LINE_SEP))
    nf.println(s"""  ) ++ ManualModel.initNamedHeap)""")
    nf.println(s"""  lazy val tyMap: Map[String, Map[Value, Value]] = Map(""")
    nf.println(tys.map {
      case ((tname, _)) => s"""    ("$tname" -> $tname.map)"""
    }.mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""}""")
    nf.close()
  }
}
