package kr.ac.kaist

import java.io.File

package object jiset {
  // Line seperator
  val LINE_SEP = System.getProperty("line.separator")

  // Base project directory root
  val BASE_DIR = System.getenv("JISET_HOME")

  // Tests directory root
  val TEST_DIR = s"${BASE_DIR}/tests"

  // Resource directory root
  val RESOURCE_DIR = s"${BASE_DIR}/src/main/resources"

  // Model directory root
  val MODEL_DIR = s"${BASE_DIR}/ires/src/main/scala/kr/ac/kaist/ires/model"

  // Current directory root
  val CUR_DIR = System.getProperty("user.dir")

  // ECMAScript model
  val VERSION = "es2019"

  val DIFFLIST = List("es2016_eval", "es2017_eval", "es2018_eval", "es2019_eval")

  // Debugging mode
  val DEBUG_SEMI_INSERT: Boolean = false
  val DEBUG_PARSER: Boolean = false
  val DEBUG_INTERP: Boolean = false

  // display progress in tests
  val DISPLAY_TEST_PROGRESS = false
}
