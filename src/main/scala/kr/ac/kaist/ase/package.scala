package kr.ac.kaist

import java.io.File

package object ase {
  // Line seperator
  val LINE_SEP = System.getProperty("line.separator")

  // Path seperator
  val SEP = File.separator

  // Base project directory root
  val BASE_DIR = System.getenv("ASE_HOME")

  // Base project directory root
  val CUR_DIR = System.getProperty("user.dir")
}
