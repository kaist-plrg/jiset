package kr.ac.kaist.ase

import java.io._
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.model.{ AST, Parser => JSParser }
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.phase._
import org.scalatest._
import scala.util.Random.shuffle

import spray.json._
import kr.ac.kaist.ase.util._
import kr.ac.kaist.ase.util.TestConfigJsonProtocol._

class Test262Test extends CoreTest {
  // tag name
  val tag: String = "test262Test/test"

  // base directory
  val test262Dir = s"$TEST_DIR/test262"

  // tests for js-parser
  def parseJSTest(ast: => AST): Unit = {
    val newAST = JSParser.fromString(ast.toString)
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

  // registration
  val dir = new File(test262Dir)
  val config = readFile(s"$TEST_DIR/test262.json").parseJson.convertTo[Test262ConfigSummary]
  for (NormalTestConfig(filename, includes) <- shuffle(config.normal)) {
    lazy val jsName = s"${dir.toString}/$filename"
    lazy val name = removedExt(jsName).drop(dir.toString.length + 1)
    lazy val jsConfig = aseConfig.copy(fileNames = List(jsName))

    lazy val ast = Parse((), jsConfig)
    check("Test262Parse", name, {
      println(name)
      parseJSTest(ast)
    })

    // TODO
    // lazy val st = EvalCore(Load(ast, jsConfig), jsConfig)
    // check("Test262Eval", name, evalJSTest(st))
  }
}
