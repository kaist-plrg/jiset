package kr.ac.kaist.ase.core

// CORE Objects
sealed trait Obj extends CoreNode {
  // types
  def ty: Ty
}

// CORE symbols
case class CoreSymbol(desc: String) extends Obj {
  val ty: Ty = Ty("Symbol")

  // getters
  def apply(key: Value): Value = key match {
    case Str("Description") => Str(desc)
    case v => error(s"an invalid symbol field access: $v")
  }
}

// CORE maps
case class CoreMap(ty: Ty, props: Map[Value, Value]) extends Obj {
  // getters
  def apply(prop: Value): Value = props.getOrElse(prop, Absent)

  // setters
  def updated(prop: Value, value: Value): CoreMap = copy(props = props + (prop -> value))

  // deletes
  def deleted(prop: Value): CoreMap = copy(props = props - prop)
}

// CORE lists
case class CoreList(values: Vector[Value]) extends Obj {
  // types
  def ty: Ty = Ty("List")

  // getters
  def apply(key: Value): Value = key match {
    case INum(long) =>
      val idx = long.toInt
      if (0 <= idx && idx < values.length) values(idx)
      else Absent
    case Str("length") => INum(values.length)
    case v => error(s"not an integer key: $v")
  }

  // appends
  def append(value: Value): CoreList = CoreList(values :+ value)

  // prepends
  def prepend(value: Value): CoreList = CoreList(value +: values)

  // pops
  def pop(idx: Value): (Value, CoreList) = idx match {
    case INum(long) =>
      val k = long.toInt
      if (k < 0 || k >= values.length) error(s"Out of range: $k of $this")
      (values(k), CoreList(values.slice(0, k) ++ values.slice(k + 1, values.length)))
    case v =>
      error(s"not an integer index: $v of $this")
  }
}

// CORE not supported objects
case class CoreNotSupported(name: String) extends Obj {
  val ty: Ty = Ty("")
}
