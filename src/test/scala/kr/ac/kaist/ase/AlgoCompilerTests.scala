package kr.ac.kaist.ase

import java.io._
import kr.ac.kaist.ase.algorithm._
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.model._
import kr.ac.kaist.ase.util.Useful._
import org.scalatest._
import scala.util.Random.shuffle
import scala.util.{ Failure, Success, Try }

class AlgoCompilerTest extends CoreTest {
  // tag name
  val tag: String = "algoCompilerTest"

  // algorithm files
  val algoDir = s"$RESOURCE_DIR/$VERSION/auto/algorithm"

  // tests for algo-compiler
  def algoCompilerTest(size: Int, failed: Map[Int, List[Token]], name: String): Unit = {
    val tag = "AlgoCompile"
    val res = resMap.getOrElse(tag, Map())
    val newRes = (res /: (0 until size)) {
      case (res, k) => res + (s"$name$k" -> !(failed contains k))
    }
    resMap += tag -> newRes
    val failedCount = failed.size
    assert(failedCount == 0)
  }

  // registration
  def init: Unit = {
    for (file <- shuffle(walkTree(new File(algoDir)))) {
      val filename = file.getName
      if (jsonFilter(filename)) {
        val name = file.toString
        val algo = Algorithm(name)
        val lineCount = algo.lineCount
        lazy val compiler = AlgoCompiler("", algo)
        lazy val (func, failed) = compiler.result
        test(s"[AlgoCompile] $filename") { algoCompilerTest(lineCount, failed, filename) }
        check("AlgoCoreParse", filename, parseCoreFuncTest(func))
      }
    }
  }

  init
}
