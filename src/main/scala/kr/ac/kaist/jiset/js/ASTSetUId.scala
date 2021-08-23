package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset.js.ast._

class ASTSetUId extends ASTWalker {
  private var uid = 0
  private def getUId = {
    val newUId = uid
    uid += 1
    newUId
  }
  override def job(ast: AST): Unit = ast.setUId(getUId)
}
