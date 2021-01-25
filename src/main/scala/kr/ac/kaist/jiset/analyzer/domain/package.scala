package kr.ac.kaist.jiset.analyzer

package object domain {
  // concrete domain
  val Infinite = concrete.Infinite
  val Finite = concrete.Finite
  val Zero = concrete.Zero
  val One = concrete.One
  val Many = concrete.Many

  // abstract states
  val AbsState: state.Domain = state.BasicDomain
  type AbsState = AbsState.Elem
}
