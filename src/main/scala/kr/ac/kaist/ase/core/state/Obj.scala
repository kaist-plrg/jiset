package kr.ac.kaist.ase.core

// CORE Objects
trait Obj extends CoreNode {
  // types
  def ty: Ty
}

// CORE CoreMap
case class CoreMap(ty: Ty, props: Map[Value, Value]) extends Obj {
  // existence check
  def contains(prop: Value): Boolean = props contains prop

  // getters
  def apply(prop: Value): Value = props.getOrElse(prop, error(s"free property: $prop"))

  // setters
  def updated(prop: Value, value: Value): Obj = copy(props = props + (prop -> value))

  // deletes
  def deleted(prop: Value): Obj = copy(props = props - prop)
}

// Core CoreList
case class CoreList(values: Vector[Value]) extends Obj {
  // types
  def ty: Ty = Ty("list")

  // getters
  def apply(key: Value): Value = key match {
    case INum(long) =>
      val idx = long.toInt
      if (0 <= idx && idx < values.length) values(idx)
      else error(s"index out of bound: $idx")
    case Str("length") => INum(values.length)
    case v => error(s"not an integer key: $v")
  }
}
