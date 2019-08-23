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

  def splitAlgo(algo: Algorithm): List[List[Token]] = (for {
    step <- algo.getSteps(Nil)
  } yield (List[Token]() /: step.tokens) {
    case (l, StepList(_)) => Out :: In :: l
    case (l, x) => x :: l
  }.reverse)

  case class Memoized[A1, A2, B](f: (A1, A2) => B) extends ((A1, A2) => B) {
    val cache = scala.collection.mutable.Map.empty[(A1, A2), B]
    def apply(x: A1, y: A2) = cache.getOrElseUpdate((x, y), f(x, y))
  }

  lazy val lcsM: Memoized[List[List[Token]], List[List[Token]], List[List[Token]]] = Memoized {
    case (_, Nil) => Nil
    case (Nil, _) => Nil
    case (x :: xs, y :: ys) if x == y => x :: lcsM(xs, ys)
    case (x :: xs, y :: ys) => {
      (lcsM(x :: xs, ys), lcsM(xs, y :: ys)) match {
        case (xs, ys) if xs.length > ys.length => xs
        case (xs, ys) => ys
      }
    }
  }

  def findDiffStepSet(a: List[List[Token]], b: List[List[Token]]): List[Int] = {
    val commonSeq = lcsM(a, b)
    def aux(comm: List[List[Token]], b: List[List[Token]], i: Int, li: List[Int]): List[Int] = (comm, b, i, li) match {
      case (_, Nil, _, li) => li
      case (Nil, x :: rest, i, li) => aux(Nil, rest, i + 1, li :+ i)
      case (cx :: crest, x :: rest, i, li) => {
        val newCx = if (cx == x) crest else cx :: crest
        val newLi = if (cx == x) li else (li :+ i)
        aux(newCx, rest, i + 1, newLi)
      }
    }
    aux(commonSeq, b, 0, Nil)
  }

  // registration
  def init: Unit = {
    def show(name: String, filter: Algorithm => Boolean): Unit = check("AlgoCompilerDiffTest", name, {
      println
      println(s"========================================")
      println(name)
      println(s"========================================")
      var algoMap: Map[String, List[List[Token]]] = Map()
      var algoMap2: Map[String, List[List[Token]]] = Map()
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
            if (filter(algo)) {
              algoMap2 += algo.filename.split("/").last -> splitAlgo(algo)
              val diffStepSet = algoMap.get(algo.filename.split("/").last).map((x) => findDiffStepSet(x, splitAlgo(algo))).getOrElse((0 until algo.lineCount).toList)
              val isDiff = !(diffStepSet.isEmpty)
              val lineCount = algo.lineCount
              lazy val compiler = GeneralAlgoCompiler("", algo)
              lazy val (func, failed) = compiler.result
              nextAlgoMap += name -> (failed.size == 0)
              if (isDiff) diffAlgoMap += name -> (failed.size == 0)
              (0 until lineCount).foreach((k) => {
                nextStepMap += s"$name$k" -> !(failed contains k)
                if (diffStepSet contains k) diffStepMap += s"$name$k" -> !(failed contains k)
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
        spass += sp; stotal += st
        if (version != "es2016") {
          dapass += dap; datotal += dat
          dspass += dsp; dstotal += dst
        }

        println(s"$version step: ${getCountString(sp, st)}")
        println(s"     Δ step: ${getCountString(dsp, dst)}")
        println(s"----------------------------------------")
      })
      println(s"  step: ${getCountString(spass, stotal)}")
      println(s"Δ step: ${getCountString(dspass, dstotal)}")
      println(s"========================================")
    })
    show("All", x => true)
    show("Language", _.kind == Language)
    show("Builtin", _.kind == Builtin)
  }
  init
}
