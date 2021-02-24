package kr.ac.kaist.jiset.ir

// environments
case class Env(map: Map[String, Value])
object Env { def apply(seq: (String, Value)*): Env = Env(seq.toMap) }
