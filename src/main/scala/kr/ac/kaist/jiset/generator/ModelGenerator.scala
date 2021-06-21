package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.spec.grammar.Grammar

case class ModelGenerator(spec: ECMAScript, parser: Boolean) {
  val ECMAScript(grammar, algos, consts, intrinsics, symbols, _, _) = spec

  // generate model/DEFAULT_VERSION.json in resource directory
  dumpJson(spec, s"$MODEL_DIR/$DEFAULT_VERSION.json")

  // generate js/ast/*.scala in source code directory
  ASTGenerator(grammar)

  // generate js/Parser.scala in source code directory
  if (parser) ParserGenerator(grammar)

  // generate js/ASTWalker.scala in source code directory
  WalkerGenerator(grammar)

  // generate js/ASTDiff.scala in source code directory
  DiffGenerator(grammar)
}
