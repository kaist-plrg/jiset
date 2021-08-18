package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.JvmUseful._

object NativeHelper {
  // dump ECMAScript to directory
  def dumpSpec(spec: ECMAScript, dirname: String): Unit = {
    val ECMAScript(version, grammar, algos, intrinsics, symbols, aoids, section) = spec

    mkdir(dirname)
    dumpFile(version, s"$dirname/version")
    dumpFile(grammar, s"$dirname/grammar")
    mkdir(s"$dirname/algorithm")
    for (algo <- algos) dumpFile(algo, s"$dirname/algorithm/${algo.name}.algo")
    dumpFile(intrinsics.toList.sorted.mkString(LINE_SEP), s"$dirname/intrinsics")
    dumpFile(symbols.toList.sorted.mkString(LINE_SEP), s"$dirname/symbols")
    dumpFile(aoids.toList.sorted.mkString(LINE_SEP), s"$dirname/aoids")
    dumpFile(section, s"$dirname/section")
  }

  // load ECMAScript from filename
  def loadSpec(filename: String): ECMAScript = {
    if (jsonFilter(filename)) readJson[ECMAScript](filename)
    else {
      val dirname = filename
      val bugMap: Map[String, String] = BUGTRIGGER match {
        case Some(bugdir) => (for {
          bugfile <- walkTree(s"$VERSION_DIR/bugtrigger/$bugdir/algo")
          bugfilename = bugfile.getName
          if algoFilter(bugfilename)
          bugname = bugfile.toString
        } yield (bugfilename -> bugname)).toMap
        case None => Map()
      }
      ECMAScript(
        version = readFile(s"$dirname/version"),
        grammar = Grammar.fromFile(s"$dirname/grammar"),
        algos = (for {
          file <- walkTree(s"$dirname/algorithm")
          filename = file.getName
          if algoFilter(filename)
          name = bugMap.getOrElse(filename, file.toString)
        } yield Algo.fromFile(name)).toList.sortBy(_.name),
        intrinsics = readFile(s"$dirname/intrinsics").split(LINE_SEP).toSet,
        symbols = readFile(s"$dirname/symbols").split(LINE_SEP).toSet,
        aoids = readFile(s"$dirname/aoids").split(LINE_SEP).toSet,
        section = Section.fromFile(s"$dirname/section"),
      )
    }
  }
}
