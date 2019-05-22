package kr.ac.kaist.ase

import java.io._
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.phase._
import org.scalatest._
import scala.util.Random.shuffle
import scala.util.{ Failure, Success, Try }

class CoreTest extends ASETest {
  // basic core files
  val coreDir = s"$TEST_DIR/core"

  // tests for core-parser
  def parseCoreTest(pgm: => Program): Unit = Try(pgm) match {
    case Failure(e) => fail(s"it throws an error: $e")
    case Success(pgm) =>
      val newPgm = Parser.parseProgram(beautify(pgm))
      assert(pgm == newPgm)
  }

  // tests for core-interpreter
  def evalCoreTest(st: => State): Unit = Try(st) match {
    case Failure(e) => fail(s"it throws an error: $e")
    case Success(st) =>
  }

  // registration
  for (file <- shuffle(walkTree(new File(coreDir)))) {
    val filename = file.getName
    if (coreFilter(filename)) {
      lazy val name = file.toString
      lazy val config = aseConfig.copy(fileNames = List(name))

      lazy val pgm = ParseCore((), config)
      test(s"[ParseCore] $filename") { parseCoreTest(pgm) }

      lazy val st = EvalCore(LoadCore(pgm, config), config)
      test(s"[EvalCore] $filename") { evalCoreTest(st) }
    }
  }
}
