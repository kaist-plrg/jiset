package kr.ac.kaist.jiset.ir

// environments
case class Env(map: Map[String, Value]) {
  def define(id: String, v: Value): Env = copy(map + (id -> v))
  def deleted(id: String): Env = copy(map = (map - id))
  def apply(id: String): Value = map.getOrElse(id, Absent)
}
object Env { def apply(seq: (String, Value)*): Env = Env(seq.toMap) }
