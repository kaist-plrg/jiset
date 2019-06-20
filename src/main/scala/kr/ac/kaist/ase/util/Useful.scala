package kr.ac.kaist.ase.util

import java.io.{ Reader, File, PrintWriter }
import kr.ac.kaist.ase.error.NoFileError
import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import scala.io.Source

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
  def walkTree(file: File): Iterable[File] = {
    val children = new Iterable[File] {
      def iterator: Iterator[File] =
        if (file.isDirectory) file.listFiles.iterator
        else Iterator.empty
    }
    Seq(file) ++: children.flatMap(walkTree(_))
  }

  // extention filter
  def extFilter(ext: String): String => Boolean = _.endsWith(s".$ext")
  lazy val coreFilter = extFilter("core")
  lazy val jsFilter = extFilter("js")
  lazy val jsonFilter = extFilter("json")

  // file writer
  def getPrintWriter(filename: String): PrintWriter =
    new PrintWriter(new File(filename))

  // read file
  def readFile(filename: String): String =
    Source.fromFile(filename).mkString

  // get first filename
  def getFirstFilename(aseConfig: ASEConfig, job: String): String =
    aseConfig.fileNames.headOption.getOrElse(throw NoFileError(job))

  // delete files
  def deleteFile(filename: String): Unit = new File(filename).delete

  // change extension
  def changeExt(from: String, to: String): String => String =
    filename => filename.substring(0, filename.length - from.length) + to

  // get name without extension
  def removedExt(filename: String): String =
    filename.split('.').dropRight(1).mkString(".")
}
