package kr.ac.kaist.jiset

import java.io._
import scala.io.Source
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

class Test262Test extends JISETTest {
  // tag name
  val tag: String = "test262Test"

  // base directory
  val test262Dir = s"$TEST_DIR/test262"

  // print results after all tests
  override def afterAll(): Unit = {
    val suffix = new SimpleDateFormat("yyMMddHHmm").format(new Date())
    val filename = s"$TEST_DIR/result/${tag}_${suffix}"

    val jpw = getPrintWriter(filename)
    resMap("Test262Eval").toList.sortBy { case (k, v) => k }.foreach {
      case (k, v) => jpw.println(s"$k: $v")
    }
    jpw.close()
  }

  // tests for js-interpreter
  def evalJSTest(st: => State): Unit = {
    st.context.locals.get(st.context.retId) match {
      case Some(addr: Addr) => st.heap(addr, Str("Type")) match {
        case (addr: Addr) =>
          assert(addr == st.globals.getOrElse(Id("CONST_normal"), Absent))
          st.heap(NamedAddr("REALM"), Str("printStr")) match {
            case Str(v) => if (v contains "AsyncTestFailure") fail(s"print test failure: $v") else ()
            case _ => ()
          }
        case v => fail(s"invalid completion type: $v")
      }
      case Some(v) => fail(s"return not an address: $v")
      case None => fail("no return value")
    }
  }

  // registration
  val dir = new File(test262Dir)
  val config = FilterMeta.test262configSummary
  val initInclude = List("assert.js", "sta.js").foldLeft(Map[String, List[StatementListItem]]()) {
    case (imm, s) => {
      val includeName = s"${dir.toString}/harness/$s"
      val jsConfig = aseConfig.copy(fileNames = List(includeName))
      val stmtList = ModelHelper.flattenStatement(Parse((), jsConfig))
      imm + (s -> stmtList)
    }

  }
  val includeMap: Map[String, List[StatementListItem]] = config.normal.foldLeft(initInclude) {
    case (im, NormalTestConfig(_, includes)) =>
      includes.foldLeft(im) {
        case (imm, s) => if (imm contains s) {
          imm
        } else {
          val includeName = s"${dir.toString}/harness/$s"
          val jsConfig = aseConfig.copy(fileNames = List(includeName))
          val stmtList = ModelHelper.flattenStatement(Parse((), jsConfig))
          imm + (s -> stmtList)
        }
      }
  }

  def init: Unit = {
    val initStList = includeMap("assert.js") ++ includeMap("sta.js")
    val noParseSet = NoParse.failed.toSet ++ NoParse.long.toSet
    val failedSet = (
      if (PREVDIFF) Source.fromFile(PREVFILE).getLines.filter(_ contains ": false").map((s) => s.takeWhile(_ != ':').replace("//", "/").split("test/").last).toList
      else Nil
    )
    for (NormalTestConfig(filename, includes) <- shuffle(config.normal)) {
      val jsName = s"${dir.toString}/test/$filename".replace("//", "/")
      val name = removedExt(jsName).drop(dir.toString.length)
      if ((!(noParseSet contains name)) && ((!PREVDIFF) || (failedSet contains name.replace("//", "/").split("test/").last))) check("Test262Eval", name, {
        val jsConfig = aseConfig.copy(fileNames = List(jsName))

        val ast = Parse((), jsConfig)
        ModelHelper.checkSupported(ast)

        val stList = includes.foldLeft(initStList) {
          case (li, s) => li ++ includeMap(s)
        } ++ ModelHelper.flattenStatement(ast)
        val st = EvalCore(Load(ModelHelper.mergeStatement(stList), jsConfig), jsConfig)
        evalJSTest(st)
      })
    }
  }

  init
}
