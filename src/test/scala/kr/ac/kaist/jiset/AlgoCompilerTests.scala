package kr.ac.kaist.jiset

import java.io._
import kr.ac.kaist.jiset.algorithm._
import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.model._
import kr.ac.kaist.jiset.util.Useful._
import org.scalatest._
import scala.util.Random.shuffle
import scala.util.{ Failure, Success, Try }

class NotYetChecker extends UnitWalker {
  var exist = false
  override def walk(expr: Expr): Unit = expr match {
    case ENotSupported(msg) => exist = true
    case _ => super.walk(expr)
  }
}

class AlgoCompilerTest extends IRTest {
  // tag name
  val tag: String = "algoCompilerTest"

  // algorithm files
  val algoDir = s"$RESOURCE_DIR/$VERSION/auto/algorithm"

  // tests for algo-compiler
  def algoCompilerTest(size: Int, failed: Map[Int, List[Token]], name: String): Unit = {
    val tag = "AlgoCompile"
    val res = resMap.getOrElse(tag, Map())
    val newRes = (0 until size).foldLeft(res) {
      case (res, k) => res + (s"$name$k" -> (if (failed contains k) Fail else Pass))
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
        val checker = new NotYetChecker
        checker.walk(func)
        if (checker.exist) println(filename)
        test(s"[AlgoCompile] $filename") { algoCompilerTest(lineCount, failed, filename) }
        check("AlgoIRParse", filename, parseIRFuncTest(func))
      }
    }
  }

  init
}
