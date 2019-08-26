package kr.ac.kaist.jiset

import java.io._
import kr.ac.kaist.jiset.core._
import kr.ac.kaist.jiset.error.NotSupported
import kr.ac.kaist.jiset.model.{ Parser => JSParser, StatementListItem, ModelHelper, NoParse }
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.phase._
import org.scalatest._
import scala.util.Random.shuffle

import java.text.SimpleDateFormat
import java.util.Date

import spray.json._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.TestConfigJsonProtocol._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

class Test262AllParseTest extends JISETTest {
  // tag name
  val tag: String = "test262AllParseTest"

  // base directory
  val test262Dir = s"$TEST_DIR/test262"

  // all tests
  val test262all = s"$test262Dir/test"

  // tests for js-parser
  def parseJSTest(ast: => AST): Unit = {
    val newAST = JSParser.parse(JSParser.Script(Nil), ast.toString).get
    assert(ast == newAST)
  }

  // do nothing after all tests
  override def afterAll(): Unit = {}

  // initialize tests
  def init: Unit = {
    for (file <- shuffle(walkTree(new File(test262all)))) {
      val filename = file.getName
      if (jsFilter(filename)) {
        val name = removedExt(filename)
        val jsName = file.toString
        val jsConfig = aseConfig.copy(fileNames = List(jsName))
        val testName = jsName.drop(test262all.length)

        lazy val ast = Parse((), jsConfig)
        check("Test262AllParse", testName, parseJSTest(ast))
      }
    }
  }

  init
}
