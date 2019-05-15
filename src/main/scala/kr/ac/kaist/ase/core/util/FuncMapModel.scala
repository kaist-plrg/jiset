package kr.ac.kaist.ase.core

import java.io.File
import kr.ac.kaist.ase.LINE_SEP
import kr.ac.kaist.ase.util.Useful

// CORE Global Model Loader
object GlobalLoader {
  def apply(filename: String): Map[Id, Value] = {
    var map: Map[Id, Value] = Map()
    for (file <- walkTree(new File(filename))) {
      val filename = file.getName
      if (funcFilter(filename)) {
        val id = Id(filename.dropRight(5))
        val func = Parser.fileToFunc(file.toString)
        map += (id -> func)
      }
    }
    map
  }

  private def walkTree(file: File): Iterable[File] = Useful.walkTree(file)
  private val funcFilter = Useful.extFilter("func")
}
