package kr.ac.kaist.ase.core

// CORE Objects
case class Obj(
    ty: Ty,
    props: Map[Value, Value] = Map()
) extends CoreNode {
  // existence check
  def contains(prop: Value): Boolean = props contains prop

  // getters
  def apply(prop: Value): Value = props.getOrElse(prop, error(s"free property: $prop"))

  // setters
  def updated(prop: Value, value: Value): Obj = copy(props = props + (prop -> value))

  // deletes
  def deleted(prop: Value): Obj = copy(props = props - prop)
}
