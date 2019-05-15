package kr.ac.kaist.ase.core

// CORE Continuations
case class Cont(prop: Prop, insts: List[Inst], env: Env) extends CoreNode
