package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.{ cfg => CFG, _ }
import kr.ac.kaist.jiset.js._
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

    // print programs in set which touches nid
    config.nid match {
      case Some(nid) =>
        println("----------------------------------------")
        val funcName = cfg.funcOf(cfg.nidGen.get(nid)).name
        println(s"[$funcName,$nid]")
        println("----------------------------------------")
        val ps = fset.getPrograms(nid)
        ps.foreach(p => {
          println("----------------------------------------")
          println(p.raw)
        })
      case None =>
        config.pid match {
          case Some(pid) =>
            val p = fset.programs.find(_.uid == pid).get
            println(p.raw)
            println("----------------------------------------")
            val nids = fset.getUniqueNIds(p)
            println(nids)
            fset.printFeatures(p)
            println("----------------------------------------")
            for { trimmed <- reducer.trim(p, nids, 0) } { println(trimmed.raw) }
            println("----------------------------------------")
            val reduced = reducer.reduce(p, 1)
            reduced match {
              case None => println("FAILED")
              case Some(r) =>
                println(r.raw)
                println
                println(p.size, r.size)
            }
          case _ =>
            println(fset.summary(detail = true))
            reducer.loop()
            println(fset.summary(detail = true))
        }
    }
  }

  def defaultConfig: ReduceConfig = ReduceConfig()
  val options: List[PhaseOption[ReduceConfig]] = List(
    ("loop", NumOption((c, i) => c.loopMax = i),
      "set maximum loop iteration."),
    ("reduce-loop", NumOption((c, i) => c.reduceLoop = i),
      "set maximum reduce loop depth."),
    // XXX delete
    ("pid", NumOption((c, i) => c.pid = Some(i)),
      ""),
    ("nid", NumOption((c, i) => c.nid = Some(i)),
      ""),
  )
}

// Reduce phase config
case class ReduceConfig(
  var loopMax: Int = 100,
  var reduceLoop: Int = 5,
  // XXX delete
  var pid: Option[Int] = None,
  var nid: Option[Int] = None
) extends Config
