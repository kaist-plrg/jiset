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

  def countPass[A](k: Map[A, Boolean]): (Int, Int) = (k.filter(_._2).size, k.size)
  def getCountString(pass: Int, total: Int): String = {
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

    var (apass, atotal, dapass, datotal, spass, stotal, dspass, dstotal) = (0, 0, 0, 0, 0, 0, 0, 0)

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
          // if (algo.kind == Builtin) {
          if (algo.kind == Language) {
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
      }
      algoMap = algoMap2
      firstStepMap = nextStepMap
      firstAlgoMap = nextAlgoMap

      val (ap, at) = countPass(nextAlgoMap)
      val (dap, dat) = countPass(diffAlgoMap)
      val (sp, st) = countPass(nextStepMap)
      val (dsp, dst) = countPass(diffStepMap)

      apass += ap; atotal += at
      dapass += dap; datotal += dat
      spass += sp; stotal += st
      dspass += dsp; dstotal += dst

      println(s"$version algo: ${getCountString(ap, at)}")
      println(s"     Δ algo: ${getCountString(dap, dat)}")
      println(s"$version step: ${getCountString(sp, st)}")
      println(s"     Δ step: ${getCountString(dsp, dst)}")
      println(s"----------------------------------------")
    })
    println(s"  algo: ${getCountString(apass, atotal)}")
    println(s"Δ algo: ${getCountString(dapass, datotal)}")
    println(s"  step: ${getCountString(spass, stotal)}")
    println(s"Δ step: ${getCountString(dspass, dstotal)}")
  }
  init
}
