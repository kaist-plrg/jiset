package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.UIdGen
import kr.ac.kaist.jiset.util.Useful._

class BuildSmallTest extends CFGTest {
  // test name
  val name: String = "cfgBuildTest"

  // registration
  def init: Unit = {
    for (version <- VERSIONS) check(version, {
      val baseDir = s"$CFG_TEST_DIR/$version"
      val fidGen = new UIdGen
      val spec = getSpec(version)
      var map = Map[String, String]() // XXX remove resolving duplicated cases
      for (algo <- spec.algos) {
        map += algo.name -> Translator(algo, fidGen).toDot
      }
      for ((name, result) <- map) {
        val answer = readFile(s"$baseDir/$name.dot")
        assert(result == answer)
      }
    })
  }
  init
}
