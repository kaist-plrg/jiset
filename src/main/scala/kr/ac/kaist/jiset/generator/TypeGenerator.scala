package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._

object TypeGenerator {
  def apply(tname: String, methods: Map[String, String]): Unit = {
    val nf = getPrintWriter(s"$MODEL_DIR/type/$tname.scala")
    nf.println(s"""package kr.ac.kaist.jiset.model""")
    nf.println(s"""""")
    nf.println(s"""import kr.ac.kaist.jiset.core._""")
    nf.println(s"""object $tname {""")
    nf.println(s"""  val map: Map[Value, Value] = Map(""")
    nf.println(methods.map {
      case (key, value) =>
        s"""    (Str("$key") -> ${getScalaName(value)}.func)"""
    }.mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""}""")
    nf.close()
  }
}
