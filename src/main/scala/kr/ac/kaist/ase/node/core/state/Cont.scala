package kr.ac.kaist.ase.node.core

// CORE Continuations
case class Cont(prop: Prop, insts: List[Inst], env: Env) extends CoreNode
