package kr.ac.kaist.ase.generator

import kr.ac.kaist.ase._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.algorithm.Algorithm
import kr.ac.kaist.ase.model.AlgoCompiler

object MethodGenerator {
  def apply(name: String): Unit = {
    val scalaName = getScalaName(name)
    val algo = Algorithm(s"$RESOURCE_DIR/$VERSION/algorithm/$name.json")
    val func = AlgoCompiler(name, algo).result

    val nf = getPrintWriter(s"$MODEL_DIR/algorithm/$scalaName.scala")
    nf.println(s"""package kr.ac.kaist.ase.model""")
    nf.println(s"""""")
    nf.println(s"""import kr.ac.kaist.ase.core._""")
    nf.println(s"""object $scalaName {""")
    nf.println(s"""  val func: Func = $func""")
    nf.println(s"""}""")
    nf.close()
  }
}
