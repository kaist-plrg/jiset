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
    println(fset.summary(detail = true))
    val reducer = Reducer(fset, config.loopMax, config.reduceLoop)
    reducer.loop()
    println(fset.summary(detail = true))
  }

  def defaultConfig: ReduceConfig = ReduceConfig()
  val options: List[PhaseOption[ReduceConfig]] = List(
    ("loop", NumOption((c, i) => c.loopMax = i),
      "set maximum loop iteration."),
    ("reduce-loop", NumOption((c, i) => c.reduceLoop = i),
      "set maximum reduce loop depth."),
  )
}

// Reduce phase config
case class ReduceConfig(
  var loopMax: Int = 100,
  var reduceLoop: Int = 5
) extends Config
