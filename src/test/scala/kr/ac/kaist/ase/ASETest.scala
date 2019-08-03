package kr.ac.kaist.ase

import java.io._
import kr.ac.kaist.ase.error.NotSupported
import kr.ac.kaist.ase.phase._
import kr.ac.kaist.ase.util.Useful._
import org.scalatest._
import scala.Console.{ CYAN, GREEN, RED }
import scala.io.Source
import scala.util.{ Try, Success, Failure }
import spray.json._

abstract class ASETest extends FunSuite with BeforeAndAfterAll {
  // ase configuration
  lazy val aseConfig: ASEConfig = ASEConfig(CmdBase, Nil, true)

  // result map
  protected var resMap: Map[String, Map[String, Boolean]] = Map()

  // check result
  def check[T](tag: String, name: String, t: => T): Unit = test(s"[$tag] $name") {
    val res = resMap.getOrElse(tag, Map())
    (Try(t) match {
      case Success(_) =>
        resMap += tag -> (res + (name -> true))
        if (DISPLAY_TEST_PROGRESS) printGreen("#")
      case Failure(e @ NotSupported(_)) =>
        fail(e.toString)
      case Failure(e) =>
        resMap += tag -> (res + (name -> false))
        if (DISPLAY_TEST_PROGRESS) printRed("#")
        fail(e.toString)
    })
  }

  // get score
  def getScore(res: Map[String, Boolean]): (Int, Int) = (
    res.count { case (k, v) => v },
    res.size
  )

  // tag name
  val tag: String

  // sort by keys
  def sortByKey[U, V](map: Map[U, V])(implicit ord: scala.math.Ordering[U]): List[(U, V)] = map.toList.sortBy { case (k, v) => k }

  // check backward-compatibility after all tests
  override def afterAll(): Unit = {
    import DefaultJsonProtocol._
    val sorted =
      resMap
        .toList
        .sortBy { case (k, v) => k }
        .map { case (t, r) => (t, getScore(r)) }

    // check backward-compatibility
    var breakCount = 0
    def error(msg: String): Unit = {
      breakCount += 1
      scala.Console.err.println(s"[Backward-Compatibility] $msg")
    }

    // show abstract result
    print("[info] ")
    printlnColor(CYAN)(s"$tag:")
    sorted.foreach {
      case (t, (x, y)) =>
        print("[info] ")
        printlnColor(if (x == y) GREEN else RED)(s"  $t: $x / $y")
    }
    val filename = s"$TEST_DIR/result/$tag.json"
    val orig =
      Try(readFile(filename))
        .getOrElse("{}")
        .parseJson
        .convertTo[Map[String, Map[String, Boolean]]]
        .toSeq.sortBy(_._1)
    orig.foreach {
      case (name, origM) => resMap.get(name) match {
        case Some(curM) => origM.toSeq.sortBy(_._1) foreach {
          case (k, b) => (curM.get(k), b) match {
            case (None, _) => error(s"'[$name] $k' test is removed")
            case (Some(false), true) => error(s"'[$name] $k' test becomes failed")
            case _ =>
          }
        }
        case None => error(s"'$name' tests are removed")
      }
    }

    // save abstract result if backward-compatible
    if (breakCount == 0) {
      val pw = getPrintWriter(s"$TEST_DIR/result/$tag")
      sorted.foreach { case (t, (x, y)) => pw.println(s"$t: $x / $y") }
      pw.close()

      val jpw = getPrintWriter(filename)
      jpw.println(resMap.toJson.sortedPrint)
      jpw.close()
    }
  }
}
