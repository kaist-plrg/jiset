package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.Useful._
import scala.collection.mutable.{ Map => MMap }

// IR Objects
sealed trait Obj extends IRComponent {
  // types
  def ty: Ty

  // copy of object
  def copied: Obj
}

// IR symbols
case class IRSymbol(desc: Value) extends Obj {
  val ty: Ty = Ty("Symbol")

  // getters
  def apply(key: Value): Value = key match {
    case Str("Description") => desc
    case v => error(s"an invalid symbol field access: $v")
  }

  // copy of object
  def copied: IRSymbol = IRSymbol(desc)
}

// IR maps
case class IRMap(
  var ty: Ty,
  props: MMap[Value, (Value, Long)],
  var size: Long
) extends Obj {
  // get pairs
  def pairs: Map[Value, Value] = props.foldLeft(Map[Value, Value]()) {
    case (m, (k, (v, _))) => m + (k -> v)
  }

  // getters
  def apply(prop: Value): Value = (props.get(prop), ty, prop) match {
    case (Some((value, _)), _, _) => value
    case _ => Absent
  }

  // setters
  def findOrUpdate(prop: Value, value: Value): this.type = {
    props.get(prop) match {
      case Some(_) => this
      case _ => update(prop, value)
    }
  }

  // getorelse
  def update(prop: Value, value: Value): this.type = {
    val id = props
      .get(prop)
      .map { case (_, v) => v }
      .getOrElse({ size += 1; size })
    props += prop -> (value, id)
    this
  }

  // deletes
  def delete(prop: Value): this.type =
    { props -= prop; this }

  // copy of object
  def copied: IRMap = {
    val newProps = MMap[Value, (Value, Long)]()
    newProps ++= props
    IRMap(ty, newProps, size)
  }
}
object IRMap {
  def apply(tyname: String)(pairs: Iterable[(Value, Value)]): IRMap = {
    val irMap = IRMap(Ty(tyname))
    for ((prop, value) <- pairs) irMap.update(prop, value)
    irMap
  }
  def apply(ty: Ty): IRMap = IRMap(ty, ty.methods, 0L)
}

// IR lists
case class IRList(var values: Vector[Value] = Vector()) extends Obj {
  // types
  def ty: Ty = Ty("List")

  // getters
  def apply(key: Value): Value = key match {
    case INum(long) =>
      val idx = long.toInt
      if (0 <= idx && idx < values.length) values(idx)
      else Absent
    case Str("length") => INum(values.length)
    case v => error(s"invalid key: $v")
  }

  // appends
  def append(value: Value): this.type =
    { values :+= value; this }

  // prepends
  def prepend(value: Value): this.type =
    { values +:= value; this }

  // pops
  def pop(idx: Value): Value = idx match {
    case INum(long) => {
      val k = long.toInt
      if (k < 0 || k >= values.length) error(s"Out of range: $k of $this")
      val v = values(k)
      values = values.slice(0, k) ++ values.slice(k + 1, values.length)
      v
    }
    case v => error(s"not an integer index: $this[$v]")
  }

  // copy of object
  def copied: IRList = IRList(values)
}

// IR not supported objects
case class IRNotSupported(tyname: String, desc: String) extends Obj {
  val ty: Ty = Ty(tyname)

  // copy of object
  def copied: IRNotSupported = IRNotSupported(tyname, desc)
}
