package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.core._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.algorithm.Algorithm
import kr.ac.kaist.jiset.model.AlgoCompiler

object MethodGenerator {
  def apply(name: String): Unit = {
    val scalaName = getScalaName(name)
    val algo = Algorithm(s"$RESOURCE_DIR/$VERSION/auto/algorithm/$name.json")
    val len = algo.length
    val (func, _) = AlgoCompiler(name, algo).result

    val nf = getPrintWriter(s"$MODEL_DIR/algorithm/$scalaName.scala")
    val TRIPLE = "\"\"\""
    nf.println(s"""package kr.ac.kaist.jiset.model""")
    nf.println(s"""""")
    nf.println(s"""import kr.ac.kaist.jiset.core._""")
    nf.println(s"""import kr.ac.kaist.jiset.core.Parser._""")
    nf.println(s"""""")
    nf.println(s"""object $scalaName {""")
    nf.println(s"""  val length: Int = $len""")
    nf.println(s"""  val func: Func = parseFunc($TRIPLE${beautify(func, "  ")}$TRIPLE)""")
    nf.println(s"""}""")
    nf.close()
  }
}
