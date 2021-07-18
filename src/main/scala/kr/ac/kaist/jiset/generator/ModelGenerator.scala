package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.spec.NativeHelper._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._

case class ModelGenerator(spec: ECMAScript, parser: Boolean) {
  val grammar = spec.grammar

  // generate model/VERSION in resource directory
  dumpSpec(spec, s"$VERSION_DIR/generated")

  // generate js/ast/*.scala in source code directory
  ASTGenerator(grammar)

  // generate js/Parser.scala in source code directory
  if (parser) ParserGenerator(grammar)

  // generate js/ASTWalker.scala in source code directory
  WalkerGenerator(grammar)

  // generate js/ASTDiff.scala in source code directory
  DiffGenerator(grammar)
}
