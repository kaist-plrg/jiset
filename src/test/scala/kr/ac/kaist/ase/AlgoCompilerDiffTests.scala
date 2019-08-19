package kr.ac.kaist.ase

import java.io._
import kr.ac.kaist.ase.algorithm._
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.model._
import kr.ac.kaist.ase.util.Useful._
import org.scalatest._
import scala.util.Random.shuffle
import scala.util.{ Failure, Success, Try }

class AlgoCompilerDiffTest extends CoreTest {
  // tag name
  val tag: String = "algoCompilerDiffTest"

  // algorithm files
  val algo1Dir = s"$RESOURCE_DIR/$VERSION1/auto/algorithm"
  val algo2Dir = s"$RESOURCE_DIR/$VERSION2/auto/algorithm"

  object CheckNotYetImplWalker extends UnitWalker {
    override def walk(expr: Expr): Unit = expr match {
      case ENotYetImpl(msg) => fail(s"[ENotYetImpl] $msg")
      case e => super.walk(e)
    }
  }

  def countPass[A](k: Map[A, Boolean]): (Int, Int) = (
    k.filter { case (k, v) => v }.size, k.size
  )

  // registration
  def init: Unit = {
    var algoMap: Map[String, String] = Map()
    var firstStepMap: Map[String, Boolean] = Map()
    var firstAlgoMap: Map[String, Boolean] = Map()
    for (file <- shuffle(walkTree(new File(algo1Dir)))) {
      val filename = file.getName
      if (jsonFilter(filename)) {
        val name = file.toString
        val algo = Algorithm(name)
        algoMap += algo.filename.split("/").last -> algo.steps.toString
        val lineCount = algo.lineCount
        lazy val compiler = AlgoCompiler("", algo)
        lazy val (func, failed) = compiler.result
        firstAlgoMap += name -> (failed.size == 0)
        (0 until lineCount).foreach((k) =>
          firstStepMap += s"$name$k" -> !(failed contains k))
      }
    }

    println(s"first step: ${countPass(firstStepMap)}")
    println(s"first algo: ${countPass(firstAlgoMap)}")

    var nextStepMap: Map[String, Boolean] = Map()
    var nextAlgoMap: Map[String, Boolean] = Map()

    var diffStepMap: Map[String, Boolean] = Map()
    var diffAlgoMap: Map[String, Boolean] = Map()

    for (file <- shuffle(walkTree(new File(algo2Dir)))) {
      val filename = file.getName
      if (jsonFilter(filename)) {
        val name = file.toString
        val algo = Algorithm(name)
        val isDiff = algoMap.get(algo.filename.split("/").last).map(_ != algo.steps.toString).getOrElse(true)
        val lineCount = algo.lineCount
        lazy val compiler = AlgoCompiler("", algo)
        lazy val (func, failed) = compiler.result
        nextAlgoMap += name -> (failed.size == 0)
        if (isDiff) diffAlgoMap += name -> (failed.size == 0)
        (0 until lineCount).foreach((k) => {
          nextStepMap += s"$name$k" -> !(failed contains k)
          if (isDiff) diffStepMap += s"$name$k" -> !(failed contains k)
        })
      }
    }

    println(s"next step: ${countPass(nextStepMap)}")
    println(s"next algo: ${countPass(nextAlgoMap)}")

    println(s"diff step: ${countPass(diffStepMap)}")
    println(s"diff algo: ${countPass(diffAlgoMap)}")

  }
  init
}
