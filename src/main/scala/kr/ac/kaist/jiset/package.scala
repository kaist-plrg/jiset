package kr.ac.kaist

package object jiset {
  // Line seperator
  val LINE_SEP = System.getProperty("line.separator")

  // Base project directory root
  val BASE_DIR = System.getenv("JISET_HOME")

  // Log directory
  val LOG_DIR = s"$BASE_DIR/logs"
  val PARSE_LOG_DIR = s"$LOG_DIR/parse"
  val ANALYZE_LOG_DIR = s"$LOG_DIR/analyze"

  // Specification directory
  val ECMA262_DIR = s"$BASE_DIR/ecma262"
  val SPEC_HTML = s"$ECMA262_DIR/spec.html"
  val RECENT_VERSION = "52bfd9f5775e368c57b8793d678fee6121797354"

  // Tests directory root
  val TEST_DIR = s"$BASE_DIR/tests"
  val ANALYZER_DIR = s"$TEST_DIR/analyzer"
  val COMPILE_DIR = s"$TEST_DIR/compile"
  val CFG_TEST_DIR = s"$TEST_DIR/cfg"
  val BASIC_COMPILE_DIR = s"$COMPILE_DIR/basic"
  val LEGACY_COMPILE_DIR = s"$COMPILE_DIR/legacy"
  val GRAMMAR_DIR = s"$TEST_DIR/grammar"
  val IR_DIR = s"$TEST_DIR/ir"

  // Result directory for CFG
  val CFG_DIR = s"$BASE_DIR/cfg"

  // Current directory root
  val CUR_DIR = System.getProperty("user.dir")

  // ECMAScript model
  val VERSIONS = List(
    "es2016", "es2017", "es2018", "es2019", "es2020", "recent"
  )

  // Resource directory
  val RESOURCE_DIR = s"$BASE_DIR/src/main/resources"

  // Debugging mode
  var DEBUG: Boolean = false

  // Checking alarm
  var CHECK_ALARM: Boolean = false

  // Logging mode
  var LOG: Boolean = false

  // Test mode
  var TEST_MODE: Boolean = false
}
