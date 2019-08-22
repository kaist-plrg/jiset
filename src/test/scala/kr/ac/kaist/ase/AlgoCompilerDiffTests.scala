package kr.ac.kaist.ase

import java.io._
import kr.ac.kaist.ase.algorithm._
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.model._
import kr.ac.kaist.ase.util.Useful._
import org.scalatest._
import scala.Console.{ RESET, GREEN }
import scala.util.Random.shuffle
import scala.util.{ Failure, Success, Try }

class AlgoCompilerDiffTest extends CoreTest {
  // tag name
  val tag: String = "algoCompilerDiffTest"

  def countPass[A](k: Map[A, Boolean]): String = {
    val pass = k.filter(_._2).size
    val total = k.size
    val rate = pass.toDouble / total.toDouble * 100
    f"$GREEN[$rate%2.2f%%]$RESET $pass / $total"
  }

  // registration
  def init: Unit = {
    var algoMap: Map[String, String] = Map()
    var algoMap2: Map[String, String] = Map()
    var firstStepMap: Map[String, Boolean] = Map()
    var firstAlgoMap: Map[String, Boolean] = Map()

    var nextStepMap: Map[String, Boolean] = Map()
    var nextAlgoMap: Map[String, Boolean] = Map()

    var diffStepMap: Map[String, Boolean] = Map()
    var diffAlgoMap: Map[String, Boolean] = Map()

    DIFFLIST.foreach((version) => {
      val algoversionDir = s"$RESOURCE_DIR/$version/auto/algorithm"
      algoMap2 = Map()
      nextStepMap = Map()
      nextAlgoMap = Map()
      diffStepMap = Map()
      diffAlgoMap = Map()

      for (file <- shuffle(walkTree(new File(algoversionDir)))) {
        val filename = file.getName
        if (jsonFilter(filename)) {
          val name = file.toString
          val algo = Algorithm(name)
          algoMap2 += algo.filename.split("/").last -> algo.steps.toString
          val isDiff = algoMap.get(algo.filename.split("/").last).map(_ != algo.steps.toString).getOrElse(true)
          val lineCount = algo.lineCount
          lazy val compiler = GeneralAlgoCompiler("", algo)
          lazy val (func, failed) = compiler.result
          nextAlgoMap += name -> (failed.size == 0)
          if (isDiff) diffAlgoMap += name -> (failed.size == 0)
          (0 until lineCount).foreach((k) => {
            nextStepMap += s"$name$k" -> !(failed contains k)
            if (isDiff) diffStepMap += s"$name$k" -> !(failed contains k)
          })
        }
      }
      algoMap = algoMap2
      firstStepMap = nextStepMap
      firstAlgoMap = nextAlgoMap

      println(s"$version algo: ${countPass(nextAlgoMap)}")
      println(s"Δ algo     : ${countPass(diffAlgoMap)}")
      println(s"$version step: ${countPass(nextStepMap)}")
      println(s"Δ step     : ${countPass(diffStepMap)}")
      println(s"----------------------------------------")
    })
  }
  init
}
