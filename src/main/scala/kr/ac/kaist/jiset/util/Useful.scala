package kr.ac.kaist.jiset.util

import java.io.{ Reader, File, PrintWriter }
import java.nio.file.{ Files, StandardCopyOption }
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.error._
import org.jsoup._
import org.jsoup.nodes._
import scala.Console.{ RESET, RED, YELLOW, GREEN, CYAN }
import scala.collection.mutable
import scala.io.Source
import scala.util.Random.shuffle
import spray.json._
import scala.sys.process._
import scala.concurrent._

object Useful {
  // file reader
  def fileReader(filename: String): Reader =
    Source.fromFile(filename).bufferedReader

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

  // walk directory
  def shuffle[T](seq: Iterable[T]): Iterable[T] = shuffle(seq)
  def walkTree(filename: String): Iterable[File] = walkTree(new File(filename))
  def walkTree(file: File): Iterable[File] = {
    val children = new Iterable[File] {
      def iterator: Iterator[File] =
        if (file.isDirectory) file.listFiles.iterator
        else Iterator.empty
    }
    Seq(file) ++ children.flatMap(walkTree(_))
  }

  // extension filter
  def extFilter(ext: String): String => Boolean = _.endsWith(s".$ext")
  lazy val irFilter = extFilter("ir")
  lazy val jsFilter = extFilter("js")
  lazy val jsonFilter = extFilter("json")
  lazy val scalaFilter = extFilter("scala")

  // file writer
  def getPrintWriter(filename: String): PrintWriter =
    new PrintWriter(new File(filename))

  // dump given data to a file
  def dumpFile(data: Any, filename: String): Unit = {
    val nf = getPrintWriter(filename)
    nf.print(data)
    nf.close()
  }

  // dump given data as JSON
  def dumpJson[T](data: T, filename: String)(implicit writer: JsonWriter[T]): Unit =
    dumpFile(data.toJson.prettyPrint, filename)

  // read file
  def readFile(filename: String): String =
    Source.fromFile(filename).mkString

  // read JSON
  def readJson[T](filename: String)(implicit reader: JsonReader[T]): T =
    readFile(filename).parseJson.convertTo[T]

  // read HTML
  def readHtml(filename: String): Document = Jsoup.parse(readFile(filename))

  // get first filename
  def getFirstFilename(jisetConfig: JISETConfig, job: String): String =
    jisetConfig.fileNames.headOption.getOrElse(throw NoFileError(job))

  // get simple file name
  def getSimpleFilename(filename: String): String = new File(filename).getName

  // delete files
  def deleteFile(filename: String): Unit = new File(filename).delete

  // change extension
  def changeExt(from: String, to: String): String => String =
    filename => filename.substring(0, filename.length - from.length) + to

  // get name without extension
  def removedExt(filename: String): String =
    filename.split('.').dropRight(1).mkString(".")

  // get extension
  def getExt(filename: String): String =
    filename.split('.').last

  // renamed filename
  def renameFile(from: String, to: String): Unit =
    new File(from).renameTo(new File(to))

  // copy file
  def copyFile(from: String, to: String): Unit = Files.copy(
    new File(from).toPath,
    new File(to).toPath,
    StandardCopyOption.REPLACE_EXISTING
  )

  // create directories
  def mkdir(name: String): Unit = new File(name).mkdirs

  // colored println
  def printColor(color: String): Any => Unit =
    x => print(color + x.toString + scala.Console.RESET)
  def printRed: Any => Unit = printColor(RED)
  def printYellow: Any => Unit = printColor(YELLOW)
  def printGreen: Any => Unit = printColor(GREEN)
  def printCyan: Any => Unit = printColor(CYAN)
  def printlnColor(color: String): Any => Unit =
    x => println(color + x.toString + scala.Console.RESET)
  def printlnRed: Any => Unit = printlnColor(RED)
  def printlnYellow: Any => Unit = printlnColor(YELLOW)
  def printlnGreen: Any => Unit = printlnColor(GREEN)
  def printlnCyan: Any => Unit = printlnColor(CYAN)

  // get name that could be used in Scala identifiers
  private val symbolRegex = "@@([^@]+)".r
  private val intrinsicRegex = "%([^%]+)%".r
  def getScalaName(str: String): String =
    str.replaceAll("\\.", "DOT") match {
      case intrinsicRegex(x) => "INTRINSIC_" + x
      case symbolRegex(x) => "SYMBOL_" + x
      case x => x
    }

  // cache for function
  def cached[A, B](f: A => B): A => B = {
    val cache = mutable.Map.empty[A, B]
    arg => cache.getOrElse(arg, {
      val res = f(arg)
      cache.update(arg, res)
      res
    })
  }

  // throw a simple error
  def error(msg: String): Nothing = throw new JISETError(msg)

  // get duration time
  def time[T](f: => T): (Long, T) = {
    val start = System.currentTimeMillis
    val result = f
    val end = System.currentTimeMillis
    (end - start, result)
  }

  // catch exceptions with Option[_]
  def optional[T](f: => T): Option[T] = try Some(f) catch {
    case e: Throwable => if (DEBUG) println(e); None
  }

  // get indentation
  def getIndent(str: String): Int =
    "[ ]+".r.findFirstIn(str).fold(-1)(_.length)

  // revert special codes in HTML
  val HTML_SPECIAL_CODE = Map(
    "&quot;" -> "\"",
    "&amp;" -> "&",
    "&apos;" -> "'",
    "&lt;" -> "<",
    "&gt;" -> ">",
    "&nbsp;" -> " ",
    "&laquo;" -> "«",
    "&raquo;" -> "»"
  )
  def revertSpecialCodes(str: String): String =
    HTML_SPECIAL_CODE.foldLeft(str) {
      case (s, (f, t)) => s.replaceAll(f, t)
    }

  // execute shell command with given dir, default to CUR_DIR
  def executeCmd(given: String, dir: String = CUR_DIR): String = {
    var cmd =
      if (DEBUG) { println(s"[SHELL] $given"); given }
      else s"$given 2> /dev/null"
    var directory = new File(dir)
    var process = Process(Seq("sh", "-c", cmd), directory)
    process !!
  }

  // change git version
  def changeVersion(target: String, dir: String = CUR_DIR): Unit =
    executeCmd(s"git checkout $target", dir)

  // get git current version
  def currentVersion(dir: String = CUR_DIR): String =
    executeCmd(s"git rev-parse HEAD", dir).trim

  // normalize string
  def norm(str: String): String = "[\\s/#]".r.replaceAllIn(str, "")

  // get Element array using queries
  def getElems(elem: Element, query: String): Array[Element] =
    elem.select(query).toArray(Array[Element]())
}
