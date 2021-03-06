package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.JvmUseful._

object NativeHelper {
  // dump ECMAScript to directory
  def dumpSpec(spec: ECMAScript, dirname: String): Unit = {
    val ECMAScript(version, grammar, algos, consts, intrinsics, symbols, aoids, section) = spec

    mkdir(dirname)
    dumpFile(version, s"$dirname/version")
    dumpFile(grammar.beautified, s"$dirname/grammar")
    mkdir(s"$dirname/algorithm")
    for (algo <- algos) dumpFile(algo.beautified, s"$dirname/algorithm/${algo.name}.algo")
    dumpFile(consts.toList.sorted.mkString(LINE_SEP), s"$dirname/consts")
    dumpFile(intrinsics.toList.sorted.mkString(LINE_SEP), s"$dirname/intrinsics")
    dumpFile(symbols.toList.sorted.mkString(LINE_SEP), s"$dirname/symbols")
    dumpFile(aoids.toList.sorted.mkString(LINE_SEP), s"$dirname/aoids")
    dumpFile(section.beautified, s"$dirname/section")
  }

  // load ECMAScript from filename
  def loadSpec(filename: String): ECMAScript = {
    if (jsonFilter(filename)) readJson[ECMAScript](filename)
    else {
      val dirname = filename
      ECMAScript(
        version = readFile(s"$dirname/version"),
        grammar = Grammar.fromFile(s"$dirname/grammar"),
        algos = (for {
          file <- walkTree(s"$dirname/algorithm")
          filename = file.getName
          if algoFilter(filename)
          name = file.toString
        } yield Algo.fromFile(name)).toList.sortBy(_.name),
        consts = readFile(s"$dirname/consts").split(LINE_SEP).toSet,
        intrinsics = readFile(s"$dirname/intrinsics").split(LINE_SEP).toSet,
        symbols = readFile(s"$dirname/symbols").split(LINE_SEP).toSet,
        aoids = readFile(s"$dirname/aoids").split(LINE_SEP).toSet,
        section = Section.fromFile(s"$dirname/section"),
      )
    }
  }
}
