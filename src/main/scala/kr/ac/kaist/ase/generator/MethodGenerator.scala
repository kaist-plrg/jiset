package kr.ac.kaist.ase.generator

import kr.ac.kaist.ase._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.algorithm.Algorithm
import kr.ac.kaist.ase.algorithm.RuleCompiler

object MethodGenerator {
  def apply(version: String, name: String): Unit = {
    val objName = dotted2camel(name)
    val filename = s"$RESOURCE_DIR/$version/algorithm/$name.algorithm"
    val reader = fileReader(filename)
    val algo = Algorithm(reader, filename)
    val func = RuleCompiler(algo)

    val nf = getPrintWriter(s"$MODEL_DIR/$objName.scala")
    nf.println(s"""package kr.ac.kaist.ase.model""")
    nf.println(s"""""")
    nf.println(s"""import kr.ac.kaist.ase.core._""")
    nf.println(s"""object $objName {""")
    nf.println(s"""  val func: Func = $func""")
    nf.println(s"""}""")
    nf.close()
  }
}
