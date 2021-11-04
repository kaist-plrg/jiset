package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.editor._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.JvmUseful._

// Reduce phase
case object Reduce extends Phase[FilteredProgramSet, ReduceConfig, Unit] {
  val name: String = "reduce"
  val help: String = "Reduce a set of JS programs for ECMAScript comprehension."

  def apply(
    fset: FilteredProgramSet,
    jisetConfig: JISETConfig,
    config: ReduceConfig
  ): Unit = {
    val reducer = Reducer(fset, config.loopMax, config.reduceLoop)
    config.pid match {
      case None =>
        println(fset.summary(detail = true))
        reducer.loop()
        println(fset.summary(detail = true))
      case Some(pid) =>
        val p = fset.programs.find(_.uid == pid).get
        println(p.raw)
        // println("----------------------------------------")
        // fset.printFeatures(p)
        val reduced = reducer.reduce(p)
        println("----------------------------------------")
        reduced match {
          case None => println("FAILED")
          case Some(r) =>
            println(r.raw)
            println
            println(p.size, r.size)
        }
    }
  }

  def defaultConfig: ReduceConfig = ReduceConfig()
  val options: List[PhaseOption[ReduceConfig]] = List(
    ("loop", NumOption((c, i) => c.loopMax = i),
      "set maximum loop iteration."),
    ("reduce-loop", NumOption((c, i) => c.reduceLoop = i),
      "set maximum reduce loop depth."),
    ("pid", NumOption((c, i) => c.pid = Some(i)),
      "set maximum loop iteration."),
  )
}

// Reduce phase config
case class ReduceConfig(
  var loopMax: Int = 100,
  var reduceLoop: Int = 5,
  var pid: Option[Int] = None
) extends Config
