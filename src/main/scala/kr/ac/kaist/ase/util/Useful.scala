package kr.ac.kaist.ase.util

import java.io.Reader
import kr.ac.kaist.ase.LINE_SEP
import scala.io.Source

object Useful {
  // file reader
  def fileReader(filename: String): Reader = Source.fromFile(filename).bufferedReader

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
}
