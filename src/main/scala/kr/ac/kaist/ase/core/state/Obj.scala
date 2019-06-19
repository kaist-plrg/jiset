package kr.ac.kaist.ase.core

// CORE Objects
sealed trait Obj extends CoreNode {
  // types
  def ty: Ty
}

// CORE Singleton
case class Singleton(name: String) extends Obj {
  val ty: Ty = Ty(name)
}

// CORE CoreMap
case class CoreMap(ty: Ty, props: Map[Value, Value]) extends Obj {
  // existence check
  def contains(prop: Value): Boolean = !(this(prop) == Absent)

  // getters
  def apply(prop: Value): Value = props.getOrElse(prop, Absent)

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

  // pushses
  def push(value: Value): CoreList = CoreList(values :+ value)

  // pops
  def pop: (Value, CoreList) = values match {
    case newValues :+ last => (last, CoreList(newValues))
    case _ => error(s"empty list: $this")
  }
}
