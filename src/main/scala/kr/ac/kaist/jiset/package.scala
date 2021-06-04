package kr.ac.kaist

import java.io.File

package object jiset {
  // Line seperator
  val LINE_SEP = System.getProperty("line.separator")

  // Base project directory root
  val BASE_DIR = System.getenv("JISET_HOME")

  // Log directory
  val LOG_DIR = s"$BASE_DIR/logs"
  val PARSE_LOG_DIR = s"$LOG_DIR/parse"

  // Specification directory
  val ECMA262_DIR = s"$BASE_DIR/ecma262"
  val SPEC_HTML = s"$ECMA262_DIR/spec.html"

  // es2021-candidate-2021-03
  val RECENT_VERSION = "fc85c50181b2b8d7d75f034800528d87fda6b654"

  // Tests directory root
  val TEST_DIR = s"$BASE_DIR/tests"
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
  val VERSIONS = List("recent")

  // Resource directory root
  val RESOURCE_DIR = s"${BASE_DIR}/src/main/resources"

  // Debugging mode
  var DEBUG: Boolean = false

  // Logging mode
  var LOG: Boolean = false

  // triple double quations marks
  val TRIPLE = "\"\"\""

  // Test mode
  var TEST_MODE: Boolean = false
}
