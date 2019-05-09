package kr.ac.kaist.ase.util

sealed abstract class FileKind
object FileKind {
  def apply(fileName: String): FileKind = {
    if (fileName.endsWith(".js")) JSFile
    else if (fileName.endsWith(".js.err")) JSErrFile
    else if (fileName.endsWith(".js.todo")) JSTodoFile
    else NormalFile
  }
}

case object JSFile extends FileKind
case object JSErrFile extends FileKind
case object JSTodoFile extends FileKind
case object NormalFile extends FileKind
