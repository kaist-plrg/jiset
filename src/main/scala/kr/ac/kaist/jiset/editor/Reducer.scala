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
  // select-reduce loop
  final def loop(): Unit = {
    // trim
    nfLog.println(s"========================================")
    nfLog.println(s"[trimming]")
    for {
      p <- fset.programs
      trimmed <- trim(p, 0)
    } { fset += trimmed }

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
      for {
        reduced <- reduce(selected, iter)
        trimmed = trim(reduced, iter)
      } { fset += trimmed.getOrElse(reduced) }

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
  def reduce(p: JsProgram, iter: Int): Option[JsProgram] = {
    // nodes to preserve
    val nids = fset.getUniqueNIds(p)

    // final reduced program
    var reduced: Option[JsProgram] = None

    // reduce by mutation
    var tried = 0
    while (tried < reduceLoop) {
      val target = reduced.getOrElse(p)
      val mutators: List[Mutator] = List(
        RandomMutator1(target, nids),
        RandomMutator2(target, nids),
        RandomMutator3(target, nids),
      // TrimTraceMutator(target, nids)
      )
      val mutator = choose(mutators)
      val mutated = mutator.mutate
      // TODO reset tried counter when mutation succeed?
      if (!mutated.isEmpty) {
        logSuccess(iter, mutator, target, mutated.get)
        nfMutator.flush
        reduced = mutated
      }
      tried += 1
    }

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

  // trim js program
  def trim(p: JsProgram, iter: Int): Option[JsProgram] = {
    val nids = fset.getUniqueNIds(p)
    val mutator = TrimTraceMutator(p, nids)
    val trimmed = mutator.mutate
    trimmed.foreach(logSuccess(iter, mutator, p, _))
    trimmed
  }

  // log
  rmdir(REDUCED_DIR)
  mkdir(REDUCED_DIR)
  val nfLog = getPrintWriter(s"$REDUCED_DIR/reducer.log")
  val nfTime = getPrintWriter(s"$REDUCED_DIR/time.csv")
  val nfSize = getPrintWriter(s"$REDUCED_DIR/size.csv")
  val nfTouched = getPrintWriter(s"$REDUCED_DIR/touched.csv")
  val nfMutator = getPrintWriter(s"$REDUCED_DIR/mutator.csv")
  val nfStats = List(nfTime, nfSize, nfTouched)
  val startTime = System.currentTimeMillis
  def getTime: Double = (System.currentTimeMillis - startTime).toDouble / 1000

  // write header
  val statHeader = "# of iter, Time, Min, Q1, Median, Q3, Max, Avg, Size"
  nfStats.foreach(_.println(statHeader))
  nfMutator.println("# of iter, Time, name, from, to")

  // dump stats
  def dumpStats(iter: Int): Unit = {
    (nfStats zip fset.getBoxPlots).foreach {
      case (nf, bp) =>
        nf.println(f"$iter,$getTime%2.2f,${bp.csvSummary}")
        nf.flush
    }
  }
  def logSuccess(iter: Int, mutator: Mutator, from: JsProgram, to: JsProgram) = {
    nfMutator.println(
      f"$iter,$getTime%2.2f,${mutator.name},${from.size},${to.size}"
    )
    nfMutator.flush
  }

  // close file handles
  def close(): Unit = (nfLog :: nfMutator :: nfStats).foreach(_.close)
}
