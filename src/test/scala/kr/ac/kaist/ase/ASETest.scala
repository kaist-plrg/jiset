package kr.ac.kaist.ase

import java.io._
import kr.ac.kaist.ase.phase._
import kr.ac.kaist.ase.util.Useful._
import org.scalatest._
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
      case Success(_) => resMap += tag -> (res + (name -> true))
      case Failure(e) => resMap += tag -> (res + (name -> false)); fail(e.toString)
    })
  }

  // get score
  def getScore(res: Map[String, Boolean]): String = {
    val s = res.count { case (k, v) => v }
    val t = res.size
    s"$s / $t"
  }

  // tag name
  val tag: String

  // sort by keys
  def sortByKey[U, V](map: Map[U, V])(implicit ord: scala.math.Ordering[U]): List[(U, V)] = map.toList.sortBy { case (k, v) => k }

  // check backward-compatibility aftera all tests
  override def afterAll(): Unit = {
    import DefaultJsonProtocol._
    val sorted =
      resMap
        .toList
        .sortBy { case (k, v) => k }
        .map { case (t, r) => (t, getScore(r)) }

    // show abstract result
    println(s"$tag:")
    sorted.foreach { case (t, r) => println(s"  $t: $r") }

    // save abstract result
    val pw = getPrintWriter(s"$TEST_DIR/result/$tag")
    sorted.foreach { case (t, r) => pw.println(s"$t: $r") }
    pw.close()

    // check backward-compatibility
    var breakCount = 0
    def error(msg: String): Unit = {
      breakCount += 1
      scala.Console.err.println(s"[Backward-Compatibility] $msg")
    }
    val filename = s"$TEST_DIR/result/$tag.json"
    val orig =
      Try(readFile(filename))
        .getOrElse("{}")
        .parseJson
        .convertTo[Map[String, Map[String, Boolean]]]
    orig.foreach {
      case (name, origM) => resMap.get(name) match {
        case Some(curM) => origM.foreach {
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
      val jpw = getPrintWriter(filename)
      jpw.println(resMap.toJson.sortedPrint)
      jpw.close()
    }
    // val file = new File(jsonName)
    // val bw = new BufferedWriter(new FileWriter(file))
    // val pw = new PrintWriter(bw)
    // val pre = preciseList.sorted
    // val impre = impreciseList.sorted
    // val todo = todoList.sorted
    // val slow = slowList.sorted
    // val fail = (testList.toSet -- pre.toSet -- impre.toSet).toList.sorted
    // pw.println("#######################")
    // pw.println("# SUMMARY")
    // pw.println("#######################")
    // pw.println("# TOTAL : " + (testList.length + todo.length + slow.length))
    // pw.println("# TEST : " + testList.length)
    // pw.println("# - FAIL : " + fail.length)
    // pw.println("# - PRECISE : " + pre.length)
    // pw.println("# - IMPRECISE : " + impre.length)
    // pw.println("# - TOTAL ITERATION: " + totalIteration.toString)
    // pw.println("# TODO : " + todo.length)
    // pw.println("# SLOW : " + slow.length)
    // pw.println("#######################")
    // pw.println()
    // pw.println("FAIL: " + fail.length)
    // fail.foreach(fn => pw.println(fn))
    // pw.println()
    // pw.println("PRECISE: " + pre.length)
    // pre.foreach(fn => pw.println(fn))
    // pw.println()
    // pw.println("IMPRECISE: " + impre.length)
    // impre.foreach(fn => pw.println(fn))
    // pw.println()
    // pw.println("TODO: " + todo.length)
    // todo.foreach(fn => pw.println(fn))
    // pw.println("SLOW: " + slow.length)
    // slow.foreach(fn => pw.println(fn))
    // pw.close()
  }
}
