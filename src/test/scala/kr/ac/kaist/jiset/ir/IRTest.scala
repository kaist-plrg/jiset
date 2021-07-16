package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.JISETTest
import kr.ac.kaist.jiset.ir._

trait IRTest extends JISETTest {
  def category: String = "ir"

  // eval IR codes
  def irEval(st: State): State = Runtime(st)
  def irEval(str: String): State = Runtime(State(Program(str)))
  def irEvalFile(filename: String): State = {
    val _ = JISETTest.spec
    Runtime(State(Program.fromFile(filename)))
  }

  // tests for IR parser
  def irParseTest(program: Program): Program = {
    val newProgram = Program(program.beautified)
    assert(program == newProgram)
    program
  }
  def irParseTest(str: String): Program = irParseTest(Program(str))
  def irParseTestFile(filename: String): Program = irParseTest(Program.fromFile(filename))
}
