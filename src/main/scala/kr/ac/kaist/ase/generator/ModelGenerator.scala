package kr.ac.kaist.ase.generator

import kr.ac.kaist.ase._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.spec._

object ModelGenerator {
  def apply(spec: Spec): Unit = {
    val methods = spec.globalMethods
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
    nf.println(methods.map(i =>
      s"""    Id("$i") -> $i.func""").mkString("," + LINE_SEP))
    nf.println(s"""  ) ++ Map(""")
    nf.println(consts.map(i =>
      s"""    Id("$i") -> NamedAddr("$i")""").mkString("," + LINE_SEP))
    nf.println(s"""  ) ++ ManualModel.initGlobal""")
    nf.println(s"""  lazy val initHeap: Heap = Heap(Map(""")
    nf.println(consts.map(i =>
      s"""    NamedAddr("$i") -> Singleton("$i")""").mkString("," + LINE_SEP))
    nf.println(s"""  ))""")
    nf.println(s"""  lazy val tyMap: Map[String, Map[Value, Value]] = Map(""")
    nf.println(tys.map {
      case ((tname, _)) => s"""    ("$tname" -> $tname.map)"""
    }.mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""}""")
    nf.close()
  }
}
