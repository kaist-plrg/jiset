package kr.ac.kaist.ase

import java.io._
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.model.{ AST, ASTParser }
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.phase._
import org.scalatest._
import scala.util.Random.shuffle

class JSTest extends CoreTest {
  // base directory
  val jsDir = s"$TEST_DIR/js"

  // tests for js-parser
  def parseJSTest(ast: => AST): Unit = {
    val newAST = ASTParser.fromString(ast.toString)
    assert(ast == newAST)
  }

  // tests for js-interpreter
  def evalJSTest(st: => State): Unit = st.retValue match {
    case Some(addr: Addr) => st.heap(addr, Str("Type")) match {
      case (addr: Addr) =>
        assert(addr == st.globals.getOrElse(Id("normal"), Absent))
      case v => fail(s"invalid completion type: $v")
    }
    case Some(v) => fail(s"return not an address: $v")
    case None => fail("no return value")
  }

  val js2core = changeExt("js", "core")

  // registration
  for (file <- shuffle(walkTree(new File(jsDir)))) {
    val filename = file.getName
    if (jsFilter(filename)) {
      lazy val name = removedExt(filename)
      lazy val jsName = file.toString
      lazy val jsConfig = aseConfig.copy(fileNames = List(jsName))

      lazy val ast = Parse((), jsConfig)
      test(s"[JSParse] $name") { parseJSTest(ast) }

      lazy val st = EvalCore(Load(ast, jsConfig), jsConfig)
      test(s"[JSEval] $name") { evalJSTest(st) }

      lazy val coreName = js2core(jsName)
      lazy val coreConfig = aseConfig.copy(fileNames = List(coreName))

      lazy val pgm = ParseCore((), coreConfig)
      lazy val coreSt = EvalCore(st.copy(insts = pgm.insts), coreConfig)
      test(s"[JSCheck] $name") {
        parseCoreTest(pgm)
        evalCoreTest(coreSt)
      }
    }
  }
}
