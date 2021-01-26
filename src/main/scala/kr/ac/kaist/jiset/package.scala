package kr.ac.kaist

import java.io.File

package object jiset {
  // Line seperator
  val LINE_SEP = System.getProperty("line.separator")

  // Base project directory root
  val BASE_DIR = System.getenv("JISET_HOME")

  // Log directory
  val LOG_DIR = s"${BASE_DIR}/logs"
  val PARSE_LOG_DIR = s"${LOG_DIR}/parse"

  // Specification directory
  val ECMA262_DIR = s"${BASE_DIR}/ecma262"
  val SPEC_HTML = s"${ECMA262_DIR}/spec.html"
  val RECENT_VERSION = "52bfd9f5775e368c57b8793d678fee6121797354"

  // Tests directory root
  val TEST_DIR = s"${BASE_DIR}/tests"
  val COMPILE_DIR = s"${TEST_DIR}/compile"
  val LEGACY_COMPILE_DIR = s"${COMPILE_DIR}/legacy"
  val GRAMMAR_DIR = s"${TEST_DIR}/grammar"

  // Resource directory root
  val RESOURCE_DIR = s"${BASE_DIR}/src/main/resources"

  // Model directory root
  val MODEL_DIR = s"${BASE_DIR}/ires/src/main/scala/kr/ac/kaist/ires/model"

  // Current directory root
  val CUR_DIR = System.getProperty("user.dir")

  // ECMAScript model
  val VERSION = "es2020"
  val VERSIONS = List(
    "es2016", "es2017", "es2018", "es2019", "es2020", "recent"
  )

  val DIFFLIST = List("es2016_eval", "es2017_eval", "es2018_eval", "es2019_eval")

  // Debugging mode
  var DEBUG: Boolean = false
  val DEBUG_SEMI_INSERT: Boolean = false
  val DEBUG_PARSER: Boolean = false
  val DEBUG_INTERP: Boolean = false

  // display progress in tests
  val DISPLAY_TEST_PROGRESS = false
}
