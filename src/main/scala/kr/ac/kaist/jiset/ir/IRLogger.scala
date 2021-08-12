package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.checker._
import kr.ac.kaist.jiset.util.JvmUseful._

object IRLogger {
  // touch counter for original algorithms
  private var _originalTouchCnt: Map[String, Int] = Map()
  def originalTouchCnt: Map[String, Int] = _originalTouchCnt
  def touchAlgoOriginal(name: String): Unit =
    _originalTouchCnt += name -> (_originalTouchCnt.getOrElse(name, 0) + 1)
  def originalTouchCntString: String = (for {
    (name, k) <- originalTouchCnt
  } yield s"$k - $name").mkString(LINE_SEP)
  def dumpOriginalTouchCnt(dirname: String): Unit =
    dumpFile(originalTouchCntString, s"$dirname/touched-algos-original")

  // touch counter for partial algorithm
  private var _partialTouchCnt: Map[String, Map[View, Int]] = Map()
  def partialTouchCnt: Map[String, Map[View, Int]] = _partialTouchCnt
  def touchAlgoPartial(name: String, viewOpt: Option[View]): Unit = viewOpt match {
    case Some(view) => {
      var viewMap = partialTouchCnt.getOrElse(name, Map())
      viewMap += view -> (viewMap.getOrElse(view, 0) + 1)
      _partialTouchCnt += name -> viewMap
    }
    case None =>
  }
  def partialTouchCntString: String = (for {
    (name, viewMap) <- partialTouchCnt
    (view, k) <- viewMap
  } yield s"$k - $name - $view").mkString(LINE_SEP)
  def dumpPartialTouchCnt(dirname: String): Unit =
    dumpFile(partialTouchCntString, s"$dirname/touched-algos-partial")

  def touchAlgo(name: String, viewOpt: Option[View]): Unit = {
    touchAlgoOriginal(name)
    if (PARTIAL) touchAlgoPartial(name, viewOpt)
  }

  // temporary checking code
  def checkPartial: Map[String, Boolean] = (for {
    (fname, viewTouchMap) <- partialTouchCnt
    touchPar = viewTouchMap.values.sum
    touchOri = originalTouchCnt.getOrElse(fname, -1)
  } yield (fname -> (touchOri == touchPar))).toMap
  def checkPartialString: String = (for {
    (name, k) <- checkPartial
  } yield s"$k - $name").mkString(LINE_SEP)
  def dumpCheckPartial(dirname: String): Unit =
    dumpFile(checkPartialString, s"$dirname/touched-algos-check")

  // iteration counter
  private var _iterSum: Long = 0L
  private var _iterMap: Map[String, Int] = Map()
  def iterAvg: Double = iterSum.toDouble / iterMap.size
  def iterSum: Long = _iterSum
  def iterMap: Map[String, Int] = _iterMap
  def recordIter(fnameOpt: Option[String], iter: Int): Unit = {
    fnameOpt.map(fname => {
      _iterMap += fname -> iter
      _iterSum += iter
    })
  }
  def iterMapString: String = (for {
    (fname, iter) <- iterMap
  } yield s"$fname: $iter").mkString(LINE_SEP)
  def dumpIterMap(dirname: String): Unit =
    dumpFile(iterMapString, s"$dirname/iters")

  // callstack depth
  private var _depthMap: Map[String, Int] = Map()
  def depthSum: Int = _depthMap.toList.map(_._2).reduce(_ + _)
  def depthAvg: Double = depthSum.toDouble / _depthMap.size
  def recordCallDepth(fnameOpt: Option[String], depth: Int): Unit = {
    fnameOpt.map(fname => {
      _depthMap += fname -> depth
    })
  }
  def dumpDepthMap(dirname: String): Unit = {
    val depthMapStr = (for {
      (fname, depth) <- _depthMap
    } yield s"$fname: $depth").mkString(LINE_SEP)
    dumpFile(depthMapStr, s"$dirname/call-depths")
  }

  // visit map
  val visitRecorder: VisitRecorder = VisitRecorder()

  //dump to a directory
  lazy val jsonProtocol = new checker.JsonProtocol(js.cfg)
  def dumpVisitRecorder(dirname: String): Unit = {
    import jsonProtocol._
    dumpFile(visitRecorder, s"$dirname/visited-nodes")
    dumpJson(visitRecorder, s"$dirname/visited-nodes.json")
  }

  //TODO : refactoring
  // # displayed lines of function (original)
  def displayedLinesOriginal: Map[String, Int] = (for {
    (fname, touch) <- originalTouchCnt
    func <- js.cfg.funcMap.get(fname)
    line <- js.partialModel.originalSpec.get(func)
  } yield (fname -> touch * line)).toMap
  def displayedLinesOriginalString: String = (for {
    (name, k) <- displayedLinesOriginal
  } yield s"$k - $name").mkString(LINE_SEP)
  def dumpDisplayedLinesOriginal(dirname: String): Unit =
    dumpFile(displayedLinesOriginalString, s"$dirname/displayed-lines-original")

  //TODO : refactoring
  // # displayed lines of function (partial)
  def displayedLinesPartial: Map[String, Map[View, Int]] = (for {
    (fname, viewTouchMap) <- partialTouchCnt
    func <- js.cfg.funcMap.get(fname)
    viewDisplayMap = (for {
      (view, touch) <- viewTouchMap
      viewLineMap <- js.partialModel.partialSpec.get(func)
      line <- viewLineMap.get(view)
    } yield (view -> touch * line)).toMap
  } yield (fname -> viewDisplayMap)).toMap
  def displayedLinesPartialString: String = (for {
    (name, viewMap) <- displayedLinesPartial
    (view, k) <- viewMap
  } yield s"$k - $name - $view").mkString(LINE_SEP)
  def dumpDisplayedLinesPartial(dirname: String): Unit =
    dumpFile(displayedLinesPartialString, s"$dirname/displayed-lines-partial")

  def dumpDisplayedLines(dirname: String): Unit = {
    dumpDisplayedLinesPartial(dirname)
    dumpDisplayedLinesOriginal(dirname)
  }

  // partial evaluation result
  def targetAlgos: Set[String] = checkPartial.filter { case (_, v) => v }.keySet
  def totalLinesOriginal: Int = displayedLinesOriginal.filter { case (k, _) => targetAlgos(k) }.values.sum
  def totalLinesPartial: Int = displayedLinesPartial.filter { case (k, _) => targetAlgos(k) }.foldLeft(0) { case (b, (_, v)) => b + v.values.sum }
  def compareLinesString: String = (
    f"""- Original:
       |  - lines   : ${totalLinesOriginal}
       |- Partial:
       |  - lines   : ${totalLinesPartial} (${100.0 * totalLinesPartial / totalLinesOriginal}%.2f%% of original)""".stripMargin
  )
  def dumpCompareLines(dirname: String): Unit =
    dumpFile(compareLinesString, s"$dirname/compare-lines")

  // summary
  def summaryString: String = (
    f"""- iteration:
       |  - min   : ${iterMap.toList.map(_._2).reduce(_ min _)}
       |  - max   : ${iterMap.toList.map(_._2).reduce(_ max _)}
       |  - total : $iterSum
       |  - avg.  : $iterAvg%.2f
       |- callstack depth:
       |  - min   : ${_depthMap.toList.map(_._2).reduce(_ min _)}
       |  - max   : ${_depthMap.toList.map(_._2).reduce(_ max _)}
       |  - avg.  : $depthAvg%.2f
       |- algorithms:
       |  - # algo : ${originalTouchCnt.size}
       |  - min    : ${originalTouchCnt.toList.map(_._2).reduce(_ min _)}
       |  - max    : ${originalTouchCnt.toList.map(_._2).reduce(_ max _)}
       |- visited:
       |  - # func : ${visitRecorder.func}
       |  - # view : ${visitRecorder.view}
       |  - # node : ${visitRecorder.node}""".stripMargin
  )

  def dumpSummary(dirname: String): Unit =
    dumpFile(summaryString, s"$dirname/summary")

  // partial model
  def dumpPartialModel(dirname: String): Unit = {
    import jsonProtocol._
    val partialModel = CFGPartialModel(visitRecorder)
    dumpJson(partialModel, s"$dirname/partial.json", true)
  }

  // dump to a directory
  def dumpTo(dirname: String): Unit = if (iterSum > 0) {
    mkdir(dirname)
    dumpOriginalTouchCnt(dirname)
    dumpIterMap(dirname)
    dumpDepthMap(dirname)
    dumpVisitRecorder(dirname)
    dumpSummary(dirname)
    dumpPartialModel(dirname)
    if (PARTIAL) {
      dumpPartialTouchCnt(dirname)
      dumpDisplayedLines(dirname)
      dumpCheckPartial(dirname)
      dumpCompareLines(dirname)
    }
  }
}
