package kr.ac.kaist.ase.generator

import kr.ac.kaist.ase._
import kr.ac.kaist.ase.spec._
import kr.ac.kaist.ase.util.Useful._

object TypeGenerator {
  def apply(version: String, tname: String, methods: Map[String, String]): Unit = {
    val nf = getPrintWriter(s"$MODEL_DIR/type/$tname.scala")
    nf.println(s"""package kr.ac.kaist.ase.model""")
    nf.println(s"""""")
    nf.println(s"""import kr.ac.kaist.ase.core._""")
    nf.println(s"""object $tname {""")
    nf.println(s"""  val map: Map[Value, Value] = Map(""")
    nf.println(methods.map {
      case (key, value) =>
        s"""    (Str("$key") -> $value.func)"""
    }.mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""}""")
    nf.close()
  }
}
