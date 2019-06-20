package kr.ac.kaist.ase

import java.io._
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.phase._
import org.scalatest._
import scala.util.Random.shuffle

class BasicCoreTest extends CoreTest {
  // basic core files
  val coreDir = s"$TEST_DIR/core"

  // registration
  for (file <- shuffle(walkTree(new File(coreDir)))) {
    val filename = file.getName
    if (coreFilter(filename)) {
      lazy val name = file.toString
      lazy val config = aseConfig.copy(fileNames = List(name))

      lazy val pgm = ParseCore((), config)
      test(s"[CoreParse] $filename") { parseCoreTest(pgm) }

      lazy val st = EvalCore(LoadCore(pgm, config), config)
      test(s"[CoreEval] $filename") { evalCoreTest(st) }
    }
  }
}
