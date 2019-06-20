package kr.ac.kaist.ase

import java.io._
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.phase._
import org.scalatest._
import scala.util.Random.shuffle

class CoreTest extends ASETest {
  // tests for core-parser
  def parseCoreTest(pgm: => Program): Unit = {
    val newPgm = Parser.parseProgram(beautify(pgm))
    assert(pgm == newPgm)
  }

  // tests for core-interpreter
  def evalCoreTest(st: => State): Unit = st
}
