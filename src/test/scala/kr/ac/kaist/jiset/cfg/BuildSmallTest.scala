package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

class BuildSmallTest extends CFGTest {
  // test name
  val name: String = "cfgBuildTest"

  // registration
  def init: Unit = {
    for (version <- VERSIONS) {
      val baseDir = s"$CFG_TEST_DIR/$version"
      val (_, spec) = getSpec(version)
      for ((name, algo) <- spec.algos.map(algo => algo.name -> algo).toMap) {
        val result = Translator(algo).toDot
        val name = algo.name
        val answer = readFile(s"$baseDir/$name.dot")
        check(s"$name @ $version", assert(result == answer))
      }
    }
  }
  // init TODO
}
