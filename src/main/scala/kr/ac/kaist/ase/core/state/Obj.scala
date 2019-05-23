package kr.ac.kaist.ase.core

// CORE Objects
trait Obj extends CoreNode {
  // types
  def ty: Ty

  // existence check
  def contains(prop: Value): Boolean

  // getters
  def apply(prop: Value): Value
}

// CORE CoreMap
case class CoreMap(
    ty: Ty,
    props: Map[Value, Value] = Map()
) extends Obj {
  // existence check
  def contains(prop: Value): Boolean = props contains prop

  // getters
  def apply(prop: Value): Value = props.getOrElse(prop, error(s"free property: $prop"))

  // setters
  def updated(prop: Value, value: Value): Obj = copy(props = props + (prop -> value))

  // deletes
  def deleted(prop: Value): Obj = copy(props = props - prop)
}
