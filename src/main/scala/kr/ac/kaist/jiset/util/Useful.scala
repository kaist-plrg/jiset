package kr.ac.kaist.jiset.util

import scala.collection.mutable
import scala.util.Random.shuffle
import kr.ac.kaist.jiset.error._
import kr.ac.kaist.jiset._
import scala.Console._
import java.text.SimpleDateFormat
import java.util.Date

object Useful {
  // cache for function
  def cached[A, B](f: A => B): A => B = {
    val cache = mutable.Map.empty[A, B]
    arg => cache.getOrElse(arg, {
      val res = f(arg)
      cache.update(arg, res)
      res
    })
  }

  // indentation
  def indentation(s: StringBuilder, str: String, indent: Int): Unit = {
    str.split(LINE_SEP) match {
      case Array(str, rest @ _*) => {
        s.append(str)
        rest.foreach(rStr => {
          s.append(LINE_SEP)
          for (i <- 0 until indent) { s.append(" ") }
          s.append(rStr)
        })
      }
      case _ =>
    }
  }

  // throw a simple error
  def error(msg: String): Nothing = throw new JISETError(msg)

  // show a warning message
  def warn(msg: String): Unit = Console.err.println(msg)

  // get duration time
  def time[T](f: => T): (Long, T) = {
    val start = System.currentTimeMillis
    val result = f
    val end = System.currentTimeMillis
    (end - start, result)
  }
  // print duration time with loading message
  def time[T](msg: String, f: => T): (Long, T) = {
    lazy val f0 = f
    print(s"$msg...")
    val (interval, res) = time(f0)
    println(f" ($interval%,d ms)")
    (interval, res)
  }

  // colored println
  def setColor(color: String): Any => String =
    if (color == "") x => x.toString else x => color + x.toString + RESET
  def printColor(color: String): Any => Unit = x => print(setColor(color)(x))
  def printlnColor(color: String): Any => Unit = x => println(setColor(color)(x))

  // print duration time with loading message and only get data
  def showTime[T](msg: String, f: => T): T = time(f)._2

  // catch exceptions with Option[_]
  def optional[T](f: => T): Option[T] = try Some(f) catch {
    case e: Throwable => if (DEBUG) println(e); None
  }

  // get indentation
  def getIndent(str: String): Int =
    "[ ]+".r.findFirstIn(str).fold(-1)(_.length)

  // stringify
  def stringify[T](t: T)(implicit app: Appender.App[T]): String =
    app(new Appender, t).toString

  // shuffle
  def shuffle[T](seq: Iterable[T]): Iterable[T] = shuffle(seq)

  // date format string
  def dateStr: String = (new SimpleDateFormat("yyMMdd_HH_mm")
    .format(new Date()))
    .toString

  // show failure message
  def failMsg(msg: String): String = setColor(RED)("[FAIL] " + msg)
  def warnMsg(msg: String): String = setColor(YELLOW)("[WARN] " + msg)
  def passMsg(msg: String): String = setColor(GREEN)("[PASS] " + msg)

  // split lists by a separator
  def splitBy[T](list: List[T], sep: T): List[List[T]] = {
    @annotation.tailrec
    def aux(xs: List[T], revAcc: List[List[T]]): List[List[T]] = xs match {
      case Nil => revAcc.reverse
      case h :: t =>
        val (pref, suff) = (if (h == sep) xs.tail else xs).span(_ != sep)
        aux(suff, pref :: revAcc)
    }
    aux(list, Nil)
  }

  // slice list by offset and stride
  def slice[T](l: List[T], offset: Int, stride: Int): List[T] = {
    l.zipWithIndex.flatMap {
      case (e, idx) if (idx - offset) % stride == 0 => Some(e)
      case _ => None
    }
  }

  // trim only right
  def trimRight(str: String): String =
    str.reverse.span(_ == ' ')._2.reverse

  // error log
  def errorLog[T](f: => T)(msg: String): T = try f catch {
    case e: Throwable => println(msg); throw e
  }

  // normalize strings
  def normStr(str: String): String =
    str.replace("\\", "\\\\").replace("\"", "\\\"")
      .replace("\n", "\\n").replace("\b", "\\b")

  // get catched error message
  def getError[T](f: => T): Option[Throwable] =
    try { f; None } catch { case e: Throwable => Some(e) }
}
