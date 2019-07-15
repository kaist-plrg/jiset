package kr.ac.kaist.ase

import java.io._
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.model.{ AST, Parser => JSParser, StatementListItem, ModelHelper }
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

class Test262Test extends ASETest {
  // tag name
  val tag: String = "test262Test"

  // base directory
  val test262Dir = s"$TEST_DIR/test262"

  // tests for js-parser
  // def parseJSTest(ast: => AST): Unit = {
  //   val timeoutMs: Long = 60000
  //   try {
  //     Await.result(Future(ast), timeoutMs milliseconds)
  //   } catch {
  //     case e: TimeoutException => fail("timeout")
  //   }
  //   val newAST = JSParser.fromString(ast.toString)
  //   assert(ast == newAST)
  // }
  // check backward-compatibility aftera all tests
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
    st.retValue match {
      case Some(addr: Addr) => st.heap(addr, Str("Type")) match {
        case (addr: Addr) =>
          assert(addr == st.globals.getOrElse(Id("CONST_normal"), Absent))
        case v => fail(s"invalid completion type: $v")
      }
      case Some(v) => fail(s"return not an address: $v")
      case None => fail("no return value")
    }
  }

  // registration
  val dir = new File(test262Dir)
  val config = readFile(s"$TEST_DIR/test262.json").parseJson.convertTo[Test262ConfigSummary]
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
    for (NormalTestConfig(filename, includes) <- shuffle(config.normal)) {
      val jsName = s"${dir.toString}/$filename"
      val name = removedExt(jsName).drop(dir.toString.length + 1)
      check("Test262Eval", name, {
        val jsConfig = aseConfig.copy(fileNames = List(jsName))

        val ast = Parse((), jsConfig)
        // check("Test262Parse", name, {
        //   println(name)
        //   parseJSTest(ast)
        // })

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
