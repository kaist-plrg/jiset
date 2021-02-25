package kr.ac.kaist.jiset.ir

// environments
case class Env(map: Map[String, Value]) {
  def define(id: String, v: Value): Env = Env(map + (id -> v))
  def apply(id: String): Value = map.getOrElse(id, Absent)
}
object Env { def apply(seq: (String, Value)*): Env = Env(seq.toMap) }
