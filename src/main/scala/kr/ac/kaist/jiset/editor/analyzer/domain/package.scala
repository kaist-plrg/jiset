package kr.ac.kaist.jiset.editor.analyzer

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js.ast.AST
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.spec.algorithm.Algo

package object domain {
  //////////////////////////////////////////////////////////////////////////////
  // aliases
  //////////////////////////////////////////////////////////////////////////////
  lazy val T = Bool(true)
  lazy val F = Bool(false)
  //////////////////////////////////////////////////////////////////////////////
  // abstract domains
  //////////////////////////////////////////////////////////////////////////////
  // abstract states
  val AbsState = BasicState
  type AbsState = AbsState.Elem

  // abstract values
  val AbsValue = BasicValue
  type AbsValue = AbsValue.Elem

  // abstract return values and heaps
  val AbsRet = BasicRet
  type AbsRet = AbsRet.Elem

}
