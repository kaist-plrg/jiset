package kr.ac.kaist.jiset.analyzer

package object domain {
  val AbsState: state.Domain = state.BasicDomain
  type AbsState = AbsState.Elem
}
