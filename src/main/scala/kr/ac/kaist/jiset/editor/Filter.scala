package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.parser.MetaParser
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.{ cfg => Cfg, _ }
import kr.ac.kaist.jiset.editor.JsonProtocol._

// filtering js programs using its size and spec coverage
object Filter {
  // files
  val PROGRAM_DIR = s"$EDITOR_LOG_DIR/programs"
  List(EDITOR_LOG_DIR, EDITOR_CACHED_DIR, PROGRAM_DIR).foreach(mkdir(_))
  val cached = s"$EDITOR_CACHED_DIR/filter_data.json"
  val nfLog = getPrintWriter(s"$EDITOR_LOG_DIR/log")
  def close(): Unit = nfLog.close()

  // program id
  private var pid = 0
  private def nextPId: Int = { val prev = pid; pid += 1; prev }
  def getPId: Int = pid

  // mapping from node to shortest program
  private var nodeMap: Array[Option[JsProgram]] =
    Array.fill(cfg.nodes.size)(None)

  // mapping from shortest program to node
  private var programMap: Map[JsProgram, Set[Int]] = Map()

  // touched nodes by programs put in filter
  def touchedSize: Int = nodeMap.count(!_.isEmpty)

  // get # of programs after filtering
  def programSize: Int = programMap.size

  // get programs touching a node
  def getTouchedPrograms(n: Node): Set[JsProgram] = for {
    p <- programMap.keySet if p.touched(n.uid)
  } yield p

  // put test 262 test
  def putTest262(meta: (String, Int)): Unit = {
    val (name, id) = meta
    val (uid, filename) = (s"T$id", s"$TEST262_TEST_DIR/$name")
    val script = Test262.loadTest(
      parseFile(filename),
      MetaParser(filename).includes
    )
    put(script, Some(s"$uid"))
  }

  // put jest program
  def putJest(meta: (String, Int)): Unit = {
    val (name, id) = meta
    val (uid, filename) = (s"J$id", s"$DATA_DIR/jest/$name")
    val script = parseFile(filename)
    put(script, Some(s"$uid"))
  }

  // put custom program
  def putCustom(meta: (String, Int)): Unit = {
    val (name, id) = meta
    val (uid, filename) = (s"C$id", s"$DATA_DIR/custom/$name")
    val script = parseFile(filename)
    put(script, Some(s"$uid"))
  }

  // put js program
  def put(script: Script, touchedFile: Option[String]): Unit =
    put(JsProgram(nextPId, script, touchedFile))
  def put(p: JsProgram): Unit = {
    // logging
    var printed = false
    def log(nid: Int): Unit = {
      if (!printed) {
        nfLog.println("!!!", p.raw)
        printed = true
      }
      nfLog.println(s"${pid}, ${nid}, ${touchedSize}")
    }

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
          case _ =>
            if (LOG) log(nid)
            update(nid)
        }
      case _ =>
    }
  }

  // select one program and try to reduce its size
  def tryReduce(): Boolean = {
    // TODO how to select target program?

    // TODO how to reduce program?

    ???
  }

  // dump
  def dump(): Unit = {
    // dump programs
    programMap.keySet.foreach(p => {
      p.saveTouched()
      dumpJson(p, s"$PROGRAM_DIR/${p.uid}.json", true)
    })

    // dump data
    val nmap = nodeMap.map(_.map(p => p.uid))
    val pmap = programMap.map { case (p, nids) => p.uid -> nids }.toMap
    val data = (pid, nmap, pmap)
    dumpJson(data, cached, true)
  }

  // load
  type FilterData = (Int, Array[Option[Int]], Map[Int, Set[Int]])
  def load(): Unit = {
    // load programs
    val programs = (for {
      file <- walkTree(PROGRAM_DIR) if jsonFilter(file.getName)
      p = readJson[JsProgram](file.toString)
    } yield p.uid -> p).toMap

    // load data
    val data = readJson[FilterData](cached)
    pid = data._1
    nodeMap = data._2.map(_.map(pid => programs(pid)))
    programMap = data._3.map { case (pid, nids) => (programs(pid) -> nids) }.toMap
  }

  // print stats
  def printStats(): Unit = println(s"${programSize}/${getPId} for ${touchedSize}")

  // dump csv files
  def dumpCsv(): Unit = {
    // dump program map to csv file
    val nf = getPrintWriter(s"$EDITOR_LOG_DIR/program_map.csv")
    for { (p, nids) <- programMap } {
      val nodesStr = if (nids.size > 0) {
        "," + nids.toArray.sorted.mkString(",")
      } else ""
      nf.println(s"${p.uid},${p.size},${nids.size}$nodesStr")
    }
    nf.close
  }

  // parse script from filename
  def parseFile(filename: String): Script =
    Parser.parse(Parser.Script(Nil), fileReader(filename)).get

  // filter program using a given syntactic view
  // def apply(ast: AST, view: SyntacticView): Boolean =
  //   ast.contains(view.ast)
}
