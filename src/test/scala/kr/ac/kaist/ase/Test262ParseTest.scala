package kr.ac.kaist.ase

import java.io._
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.error.NotSupported
import kr.ac.kaist.ase.model.{ Parser => JSParser, StatementListItem, ModelHelper, NoParse }
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.phase._
import org.scalatest._
import scala.util.Random.shuffle

import java.text.SimpleDateFormat
import java.util.Date

import spray.json._
import kr.ac.kaist.ase.util._
import kr.ac.kaist.ase.util.TestConfigJsonProtocol._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

class Test262ParseTest extends ASETest {
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
    val noParseSet = NoParse.failed.toSet ++ NoParse.long.toSet
    for (NormalTestConfig(filename, includes) <- shuffle(config.normal)) {
      val jsName = s"${dir.toString}/test/$filename"
      val name = removedExt(jsName).drop(dir.toString.length + 1)
      if (!(noParseSet contains name)) check("Test262Parse", name, {
        val jsConfig = aseConfig.copy(fileNames = List(jsName))
        parseJSTest(Parse((), jsConfig))
      })
    }
  }

  init
}
