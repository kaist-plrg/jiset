package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset.js.ast.AST

object CleanStaticMap extends ASTWalker {
  override def job(ast: AST): Unit = ast.staticMap = Map()
}
