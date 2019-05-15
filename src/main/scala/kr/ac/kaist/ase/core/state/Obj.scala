package kr.ac.kaist.ase.core

// CORE Objects
case class Obj(
    ty: Ty,
    idProps: Map[Id, Value] = Map(),
    strProps: Map[String, Value] = Map()
) extends CoreNode {
  // existence check
  def contains(id: Id): Boolean = idProps contains id
  def contains(str: String): Boolean = strProps contains str

  // getters
  def apply(id: Id): Value = idProps.getOrElse(id, error(s"free identifier: $id"))
  def apply(str: String): Value = strProps.getOrElse(str, error(s"free string property: $str"))

  // setters
  def updated(id: Id, value: Value): Obj = copy(idProps = idProps + (id -> value))
  def updated(str: String, value: Value): Obj = copy(strProps = strProps + (str -> value))

  // deletes
  def deleted(id: Id): Obj = copy(idProps = idProps - id)
  def deleted(str: String): Obj = copy(strProps = strProps - str)
}
