package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.{ cfg => _, _ }
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.checker.{ cfg => _, _ }
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.js._

object IRLogger {
  // touch counter for original algorithms
  private var _originalNames: Map[String, Int] = Map()
  def originalNames: Map[String, Int] = _originalNames
  def touchOriginal(name: String): Unit =
    _originalNames += name -> (_originalNames.getOrElse(name, 0) + 1)
  def originalNamesString: String = (for {
    (name, k) <- originalNames
  } yield s"$k - $name").mkString(LINE_SEP)
  def dumpOriginalNames(dirname: String): Unit =
    dumpFile(originalNamesString, s"$dirname/touched-algos-original")

  // touch counter for partial algorithm
  private var _partialNames: Map[String, Map[View, Int]] = Map()
  def partialNames: Map[String, Map[View, Int]] = _partialNames
  def touchPartial(name: String, viewOpt: Option[View]): Unit = viewOpt match {
    case Some(view) => {
      var viewMap = partialNames.getOrElse(name, Map())
      viewMap += view -> (viewMap.getOrElse(view, 0) + 1)
      _partialNames += name -> viewMap
    }
    case None =>
  }
  def partialNamesString: String = (for {
    (name, viewMap) <- partialNames
    (view, k) <- viewMap
  } yield s"$k - $name - $view").mkString(LINE_SEP)
  def dumpPartialNames(dirname: String): Unit =
    dumpFile(partialNamesString, s"$dirname/touched-algos-partial")

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
  def displayedOriginal: Map[String, Int] = (for {
    (fname, touch) <- originalNames
    func <- cfg.funcMap.get(fname)
    line <- partialModel.originalSpec.get(func)
  } yield (fname -> touch * line)).toMap
  def displayedOriginalString: String = (for {
    (name, k) <- displayedOriginal
  } yield s"$k - $name").mkString(LINE_SEP)
  def dumpDisplayedOriginal(dirname: String): Unit =
    dumpFile(displayedOriginalString, s"$dirname/displayed-lines-original")

  //TODO : refactoring
  // # displayed lines of function (partial)
  def displayedPartial: Map[String, Map[View, Int]] = (for {
    (fname, viewTouchMap) <- partialNames
    func <- cfg.funcMap.get(fname)
    viewDisplayMap = (for {
      (view, touch) <- viewTouchMap
      viewLineMap <- partialModel.partialSpec.get(func)
      line <- viewLineMap.get(view)
    } yield (view -> touch * line)).toMap
  } yield (fname -> viewDisplayMap)).toMap
  def displayedPartialString: String = (for {
    (name, viewMap) <- displayedPartial
    (view, k) <- viewMap
  } yield s"$k - $name - $view").mkString(LINE_SEP)
  def dumpDisplayedPartial(dirname: String): Unit =
    dumpFile(displayedPartialString, s"$dirname/displayed-lines-partial")

  def dumpDisplayedLines(dirname: String): Unit = {
    dumpDisplayedPartial(dirname)
    dumpDisplayedOriginal(dirname)
  }
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
       |  - # algo : ${originalNames.size}
       |  - min    : ${originalNames.toList.map(_._2).reduce(_ min _)}
       |  - max    : ${originalNames.toList.map(_._2).reduce(_ max _)}
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
    dumpOriginalNames(dirname)
    dumpPartialNames(dirname)
    dumpIterMap(dirname)
    dumpDepthMap(dirname)
    dumpVisitRecorder(dirname)
    dumpSummary(dirname)
    dumpPartialModel(dirname)
    dumpDisplayedLines(dirname)
  }
}
