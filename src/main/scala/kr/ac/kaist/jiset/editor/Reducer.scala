package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._

// reduce a filtered program set
case class Reducer(
  fset: FilteredProgramSet,
  loopMax: Int,
  reduceLoop: Int
) {
  final def loop(): Unit = {
    // start loop
    ProgressBar("reducing", 1 to loopMax).foreach(iter => {
      // logging
      if (LOG) {
        nfLog.println(s"========================================")
        nfLog.println(s"[loop#$iter]")
      }

      // select
      val selected = select(fset.programs)

      // reduce
      for { reduced <- reduce(selected, fset.getUniqueNIds(selected)) } {
        fset += reduced
      }

      // dump filtered set results
      if (iter % 100 == 0)
        fset.setDumpDir(s"$REDUCED_DIR/$iter").dump()
      dumpStats(iter)

    })

    // dump final result
    fset.setDumpDir(s"$REDUCED_DIR/$loopMax").dump()
    close()
  }

  // select one js program to reduce
  def select(ps: Array[JsProgram]): JsProgram = {
    val selected = weightedChoose(ps.map(p => (p, p.size)))
    if (LOG) {
      nfLog.println(s"----------------------------------------")
      nfLog.println(s"!!! selected(size: ${selected.size})")
      nfLog.println(selected.raw)
    }
    selected
  }

  // reduce a given js program
  def reduce(p: JsProgram, nids: Set[Int]): Option[JsProgram] = {
    // final reduced program
    var reduced: Option[JsProgram] = None
    val mutators: List[Mutator] = List(
      RandomMutator1(p, nids),
      RandomMutator2(p, nids),
      RandomMutator3(p, nids),
    )

    // reduce by mutation
    var tried = 0
    while (tried < reduceLoop) {
      val mutator = choose(mutators)
      val target = reduced.getOrElse(p)
      val mutated = mutator.mutate
      if (!mutated.isEmpty) { reduced = mutated }
      tried += 1
    }

    // reduced.foreach(r => println(r.raw))
    if (LOG) {
      nfLog.println(s"----------------------------------------")
      reduced match {
        case Some(r) => nfLog.println("!!! success", p.size, r.size)
        case None => nfLog.println("!!! failed")
      }
      nfLog.flush
    }
    reduced
  }

  // log
  rmdir(REDUCED_DIR)
  mkdir(REDUCED_DIR)
  val nfLog = getPrintWriter(s"$REDUCED_DIR/reducer.log")
  val nfTime = getPrintWriter(s"$REDUCED_DIR/time.csv")
  val nfSize = getPrintWriter(s"$REDUCED_DIR/size.csv")
  val nfTouched = getPrintWriter(s"$REDUCED_DIR/touched.csv")
  val nfStats = List(nfTime, nfSize, nfTouched)
  val startTime = System.currentTimeMillis
  def getTime: Double = (System.currentTimeMillis - startTime).toDouble / 1000

  // write header to stat files
  val csvHeader = "# of iter, Time, Min, Q1, Median, Q3, Max, Avg, Size"
  nfStats.foreach(_.println(csvHeader))

  // dump stats
  def dumpStats(iter: Int): Unit = {
    (nfStats zip fset.getBoxPlots).foreach {
      case (nf, bp) =>
        nf.println(f"$iter,$getTime%2.2f,${bp.csvSummary}")
        nf.flush
    }
  }

  // close file handles
  def close(): Unit = (nfLog :: nfStats).foreach(_.close)
}
