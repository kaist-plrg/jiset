package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec._
import java.io.File

object AlgoGenerator {
  def apply(packageName: String, modelDir: String, spec: Spec): Unit = {
    val autoSet = spec.globalMethods.map(getScalaName _).toSet
    val manualSet = (for {
      file <- walkTree(s"$RESOURCE_DIR/$VERSION/manual/algorithm")
      filename = file.getName if scalaFilter(filename)
    } yield removedExt(filename)).toSet
    val allList = (autoSet ++ manualSet).toList.sorted

    val nf = getPrintWriter(s"$modelDir/algorithm/Algorithm.scala")
    nf.println(s"package $packageName.model")
    nf.println
    nf.println(s"import $packageName.ir.Func")
    nf.println
    nf.println(s"trait Algorithm {")
    nf.println(s"  val name: String")
    nf.println(s"  val length: Int")
    nf.println(s"  val lang: Boolean")
    nf.println(s"  val func: Func")
    nf.println(s"}")
    nf.println
    nf.println(s"object Algorithm {")
    nf.println(s"  lazy val languages: List[Algorithm] = all.filter(_.lang)")
    nf.println(s"  lazy val all: List[Algorithm] = List(")
    nf.println(allList.map("    " + _).mkString("," + LINE_SEP))
    nf.println(s"  )")
    nf.println(s"}")
    nf.close()
  }
}
