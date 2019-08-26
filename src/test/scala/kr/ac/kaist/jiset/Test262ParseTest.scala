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

class Test262ParseTest extends JISETTest {
  // tag name
  val tag: String = "test262ParseTest"

  // base directory
  val test262Dir = s"$TEST_DIR/test262"

  // tests for js-parser
  def parseJSTest(ast: => AST): Unit = {
    val newAST = JSParser.parse(JSParser.Script(Nil), ast.toString).get
    assert(ast == newAST)
  }

  // do nothing after all tests
  override def afterAll(): Unit = {}

  // registration
  val dir = new File(test262Dir)
  val config = FilterMeta.test262configSummary

  // initialize tests
  def init: Unit = {
    for (NormalTestConfig(filename, includes) <- shuffle(config.normal)) {
      val jsName = s"${dir.toString}/test/$filename"
      val name = removedExt(jsName).drop(dir.toString.length + 1)
      check("Test262Parse", name, {
        val jsConfig = aseConfig.copy(fileNames = List(jsName))
        parseJSTest(Parse((), jsConfig))
      })
    }
  }

  init
}
