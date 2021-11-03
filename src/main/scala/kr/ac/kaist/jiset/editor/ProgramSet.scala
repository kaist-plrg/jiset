package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.editor.JsonProtocol._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.parser.MetaParser
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.{ cfg => CFG, _ }
import scala.collection.mutable.ArrayBuffer

// js program set
trait ProgramSet {
  // programs
  def programs: Array[JsProgram]

  // size
  def size: Int = programs.size

  // append a program
  def +=(p: JsProgram): Unit

  // stat for programs (pid, execTime, size)
  def programStat: List[(Int, Int, Int)] =
    programs.map(p => (p.uid, p.execTime, p.size)).toList

  // stat for nodes
  def nodeStat: List[Int] = {
    val counter = Array.fill(cfg.nodes.size)(0)
    for {
      p <- programs
      nid <- p.touchedNIds
    } { counter(nid) += 1 }
    counter.toList
  }

  // box-plot
  def getBoxPlots = {
    val (_, times, sizes) = programStat.unzip3
    val touchCnts = nodeStat.filter(_ != 0)
    (BoxPlot(times), BoxPlot(sizes), BoxPlot(touchCnts))
  }

  // base directory
  private var _dumpDir: Option[String] = None
  def setDumpDir(d: String): this.type =
    { mkdir(d); _dumpDir = Some(d); this }
  def dumpDir: String = _dumpDir.get

  // dump stats
  def dumpStats(): Unit = {
    // dump program data
    val nfProgram = getPrintWriter(s"$dumpDir/program.csv")
    nfProgram.println("pid,execution time,size")
    programStat
      .sortWith(_._1 < _._1)
      .foreach { case (pid, execTime, size) => nfProgram.println(s"$pid,$execTime,$size") }
    nfProgram.close

    // dump node data
    val nfNode = getPrintWriter(s"$dumpDir/node.csv")
    nfNode.println("nid,# of programs")
    nodeStat
      .zipWithIndex
      .foreach { case (cnt, nid) => nfNode.println(s"$nid,$cnt") }
    nfNode.close
  }
}

// simple program set
class SimpleProgramSet extends ProgramSet {
  // internal programs buffer
  protected val _programs: ArrayBuffer[JsProgram] = ArrayBuffer()

  // append a program
  def +=(p: JsProgram): Unit =
    _programs += p.setUId(_programs.size)

  // union
  def union(that: SimpleProgramSet): SimpleProgramSet = {
    that.programs.foreach(p => { this += p })
    this
  }

  // programs
  def programs: Array[JsProgram] = _programs.toArray
}
object SimpleProgramSet {
  // raw program set
  private class RawProgramSet(
    tag: String,
    baseDir: String,
    fromFile: String => Script = (f: String) => parseJsFile(f),
    cachedDir: Option[String] = Some(s"$EDITOR_CACHED_DIR")
  ) extends SimpleProgramSet {
    // append a program
    def +=(name: String, uid: Int): Unit = {
      val cached = cachedDir.map(d => s"$d/$tag$uid.json")
      val filename = s"$baseDir/$name"

      // get js program by cached
      val program = cached match {
        // read js program from cached
        case Some(cached) if exists(cached) =>
          readJson[JsProgram](cached)
        case _ =>
          val script = fromFile(filename)
          val p = JsProgram.fromScript(script)
          cached.foreach(dumpJson(p, _, true)) // cache
          p
      }

      // append
      this += program
    }
  }

  type MetaList = List[(String, Int)]
  implicit def raw2simple(r: RawProgramSet): SimpleProgramSet =
    r.asInstanceOf[SimpleProgramSet]
  // program set builder
  private def fromList(
    kind: String,
    pset: RawProgramSet,
    metas: MetaList
  ): SimpleProgramSet = {
    ProgressBar(s"load $kind program set", metas).foreach {
      case (name, id) => { pset += (name, id) }
    }
    pset
  }
  private def getList(path: String): MetaList =
    readFile(path).split(LINE_SEP).toList.zipWithIndex
  // from test262
  def fromTest262(): SimpleProgramSet = {
    val metas = getList(s"$DATA_DIR/test262-list")
    val pset = new RawProgramSet("T", s"$TEST262_TEST_DIR", f => {
      Test262.loadTest(parseJsFile(f), MetaParser(f).includes)
    })
    fromList("test262", pset, metas)
  }
  // from jest
  def fromJest(): SimpleProgramSet = {
    val metas = getList(s"$DATA_DIR/jest-list")
    val pset = new RawProgramSet("J", s"$DATA_DIR/jest")
    fromList("jest", pset, metas)
  }
  // from custom
  def fromCustom(): SimpleProgramSet = {
    val metas = getList(s"$DATA_DIR/custom-list")
    val pset = new RawProgramSet("J", s"$DATA_DIR/custom")
    fromList("custom", pset, metas)
  }
}

// filtered program set
class FilteredProgramSet extends ProgramSet {
  import FilteredProgramSet._
  // mapping from node to shortest program
  private var nodeMap: Array[Option[JsProgram]] =
    Array.fill(cfg.nodes.size)(None)

  // mapping from shortest program to node
  private var programMap: Map[JsProgram, Set[Int]] = Map()

  // programs
  def programs: Array[JsProgram] = programMap.keys.toArray

  // touched size
  def touchedSize: Int = nodeMap.count(!_.isEmpty)

  // get nodes, which uniquely covered by a given program
  def getUniqueNIds(p: JsProgram): Set[Int] = programMap.getOrElse(p, Set())

  // add a program
  private var _pid = 0
  private def nextPId: Int = { val prev = _pid; _pid += 1; prev }
  def +=(p: JsProgram): Unit = {
    // update uid of program
    p.setUId(nextPId)

    // update nodeMap
    def update(nid: Int): Unit = {
      nodeMap(nid).foreach(p0 => {
        val nids = programMap(p0)
        if (nids.size <= 1) programMap -= p0
        else programMap += (p0 -> (nids - nid))
      })
      nodeMap(nid) = Some(p)
      programMap += (p -> (programMap.getOrElse(p, Set()) + nid))
    }

    // update shortest programs by node
    p.touched.zipWithIndex.foreach {
      case (true, nid) =>
        nodeMap(nid) match {
          case Some(p0) if p0.size <= p.size =>
          case _ => update(nid)
        }
      case _ =>
    }
  }

  // dump
  def PROGRAM_DIR = s"$dumpDir/programs"
  def dump() = {
    // mkdir
    mkdir(PROGRAM_DIR)

    // dump programs
    for { p <- programs } {
      dumpJson(p, s"$PROGRAM_DIR/${p.uid}.json", true)
      dumpFile(p.raw, s"$PROGRAM_DIR/${p.uid}.js")
    }

    // dump filtered set data
    val nmap = nodeMap.map(_.map(p => p.uid))
    val pmap = programMap.map { case (p, nids) => p.uid -> nids }.toMap
    val data = (_pid, nmap, pmap)
    dumpJson(data, s"$dumpDir/data.json", true)
  }

  // print stats
  def printStats(detail: Boolean = false): Unit = {
    println(s"${size} for ${touchedSize}")
    if (detail) {
      val (bTime, bSize, bTouched) = getBoxPlots
      println("* execution time")
      println(bTime.summary)
      println("* program size")
      println(bSize.summary)
      println("* touched counts")
      println(bTouched.summary)
    }
  }

  // dump reduced stats
  def dumpReducedStats(): Unit = {
    val (bTime, bSize, bTouched) = getBoxPlots
    val csvHeader = "Min, Q1, Median, Q3, Max, Avg, Size"

    //dump time stat
    val nfTime = getPrintWriter(s"$REDUCED_DIR/time.csv")
    nfTime.println(csvHeader)
    nfTime.println(bTime.csvSummary)
    nfTime.close()

    // dump size stat
    val nfSize = getPrintWriter(s"$REDUCED_DIR/size.csv")
    nfSize.println(csvHeader)
    nfSize.println(bSize.csvSummary)
    nfSize.close()

    // dump touched stat
    val nfTouched = getPrintWriter(s"$REDUCED_DIR/touched.csv")
    nfTouched.println(csvHeader)
    nfTouched.println(bTouched.csvSummary)
    nfTouched.close()
  }
}
object FilteredProgramSet {
  def apply(pset: SimpleProgramSet): FilteredProgramSet = {
    val fset = new FilteredProgramSet
    ProgressBar(s"filtering programs", pset.programs).foreach { p => fset += p }
    fset
  }

  // load
  type FilterData = (Int, Array[Option[Int]], Map[Int, Set[Int]])
  def load(dumpDir: String): FilteredProgramSet = {
    val fset = (new FilteredProgramSet).setDumpDir(dumpDir)

    // load programs
    val programs = (for {
      file <- walkTree(fset.PROGRAM_DIR) if jsonFilter(file.getName)
      p = readJson[JsProgram](file.toString)
    } yield p.uid -> p).toMap

    // load data
    val data = readJson[FilterData](s"${fset.dumpDir}/data.json")
    fset._pid = data._1
    fset.nodeMap = data._2.map(_.map(pid => programs(pid)))
    fset.programMap = data._3.map { case (pid, nids) => (programs(pid) -> nids) }.toMap

    fset
  }
}
