package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.spec.grammar.Grammar

case class TsModelGenerator(spec: ECMAScript) {
  val grammar = spec.grammar

  // dump spec.json
  val specPath = s"$TSBASE_DIR/resources/js/spec.json"
  dumpJson("specification", spec, specPath)

  // generate js/ast/*.ts in source code directory
  TsASTGenerator(grammar)
}
