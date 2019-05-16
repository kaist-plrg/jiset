package kr.ac.kaist.ase.generator

import kr.ac.kaist.ase.LINE_SEP
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.spec._

object GlobalGenerator {
  def apply(version: String, spec: Spec): Unit = {
    val methods = spec.globalMethods
    val tys = spec.tys
    methods.foreach(name => MethodGenerator(version, name))
    tys.foreach(ty => TypeGenerator(version, ty))

    val nf = getPrintWriter("./src/main/scala/kr/ac/kaist/ase/model/Global.scala")
    nf.println(s"""package kr.ac.kaist.ase.model""")
    nf.println(s"""""")
    nf.println(s"""import kr.ac.kaist.ase.core._""")
    nf.println(s"""object Global {""")
    nf.println(s"""  val initGlobal: Map[Id, Value] = Map(""")
    nf.println(methods.map(i =>
      s"""    (Id("$i") -> $i.func)""").mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""  val initType: Map[String, Obj] = Map(""")
    nf.println(tys.map(ty =>
      s"""    ("${ty.name}" -> ${ty.name}.obj)""").mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""}""")
    nf.close()
  }
}
