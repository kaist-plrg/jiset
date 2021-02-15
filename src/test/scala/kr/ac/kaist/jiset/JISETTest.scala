package kr.ac.kaist.jiset

import java.io._
import kr.ac.kaist.jiset.error.NotSupported
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.phase._
import kr.ac.kaist.jiset.util.Useful._
import org.scalatest._
import spray.json._

trait JISETTest extends FunSuite with BeforeAndAfterAll {
  // results
  trait Result
  case object Pass extends Result
  case class Yet(msg: String) extends Result
  case object Fail extends Result
  protected var resMap: Map[String, Result] = Map()
  implicit object ResultFormat extends RootJsonFormat[Result] {
    override def read(json: JsValue): Result = json match {
      case JsString(text) => Yet(text)
      case JsBoolean(bool) => if (bool) Pass else Fail
      case v => deserializationError(s"unknown Result: $v")
    }

    override def write(result: Result): JsValue = result match {
      case Pass => JsTrue
      case Fail => JsFalse
      case Yet(msg) => JsString(msg)
    }
  }

  // extract specifications
  def getInput(version: String) = JISETTest.specInputs(version)

  // count tests
  protected var count: Int = 0

  // check result
  def check[T](name: String, tester: => T): Unit = {
    count += 1
    test(s"[$tag] $name") {
      try {
        tester
        resMap += name -> Pass
      } catch {
        case e @ NotSupported(msg) =>
          resMap += name -> Yet(msg)
        case e: Throwable =>
          resMap += name -> Fail
          throw e
      }
    }
  }

  // get score
  def getScore(res: Map[String, Result]): (Int, Int) = (
    res.count { case (k, r) => r == Pass },
    res.size
  )

  // tag name
  val category: String
  lazy val tag: String = s"$category.$this"

  // sort by keys
  def sortByKey[U, V](map: Map[U, V])(implicit ord: scala.math.Ordering[U]): List[(U, V)] = map.toList.sortBy { case (k, v) => k }

  // check backward-compatibility after all tests
  override def afterAll(): Unit = {
    import DefaultJsonProtocol._

    // check backward-compatibility
    var breakCount = 0
    def error(msg: String): Unit = {
      breakCount += 1
      scala.Console.err.println(s"[Backward-Compatibility] $msg")
    }

    // show abstract result
    val filename = s"$TEST_DIR/result/$category/$this.json"
    for {
      str <- optional(readFile(filename))
      json = str.parseJson
      map = json.convertTo[Map[String, Result]]
      (name, result) <- map.toSeq.sortBy(_._1)
    } (resMap.get(name), result) match {
      case (None, _) => error(s"'[$tag] $name' test is removed")
      case (Some(Fail), Yet(_) | Pass) => error(s"'[$tag] $name' test becomes failed")
      case _ =>
    }

    // save abstract result if backward-compatible
    if (breakCount == 0) {
      val pw = getPrintWriter(s"$TEST_DIR/result/$category/$this")
      val (x, y) = getScore(resMap)
      pw.println(s"$tag: $x / $y")
      pw.close()

      val jpw = getPrintWriter(filename)
      jpw.println(resMap.toJson.sortedPrint)
      jpw.close()
    }
  }
}
object JISETTest {
  // extract specifications
  lazy val specInputs = (for (version <- VERSIONS) yield {
    version -> ECMAScriptParser.preprocess(version)
  }).toMap
}
