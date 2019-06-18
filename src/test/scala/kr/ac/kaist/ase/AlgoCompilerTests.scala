package kr.ac.kaist.ase

import java.io._
import kr.ac.kaist.ase.algorithm._
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.model._
import kr.ac.kaist.ase.util.Useful._
import org.scalatest._
import scala.util.Random.shuffle
import scala.util.{ Failure, Success, Try }

class AlgoCompilerTest extends ASETest {
  // algorithm files
  val algoDir = s"$RESOURCE_DIR/$VERSION/algorithm"

  object CheckNotYetImplWalker extends UnitWalker {
    override def walk(expr: Expr): Unit = expr match {
      case ENotYetImpl(msg) => fail(s"[ENotYetImpl] $msg")
      case e => super.walk(e)
    }
  }

  // tests for algo-compiler
  def algoCompilerTest(func: => Func): Unit = Try(func) match {
    case Failure(e) => fail(s"it throws an error: $e")
    case Success(func) =>
      CheckNotYetImplWalker.walk(func)
  }

  // registration
  for (file <- shuffle(walkTree(new File(algoDir)))) {
    val filename = file.getName
    if (jsonFilter(filename)) {
      lazy val name = file.toString
      lazy val algo = Algorithm(name)
      lazy val func = AlgoCompiler("", algo).result
      test(s"[AlgoCompiler] $filename") { algoCompilerTest(func) }
    }
  }
}
