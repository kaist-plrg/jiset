package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.parser.ESValueParser
import scala.collection.mutable.{ Map => MMap }

// IR Objects
sealed trait Obj extends IRElem {
  // types
  def ty: Ty

  // copy of object
  def copied: Obj
}

// IR symbols
case class IRSymbol(desc: PureValue) extends Obj {
  val ty: Ty = Ty("Symbol")

  // getters
  def apply(key: PureValue): PureValue = key match {
    case Str("Description") => desc
    case v => error(s"an invalid symbol field access: $v")
  }

  // copy of object
  def copied: IRSymbol = IRSymbol(desc)
}

// IR maps
case class IRMap(
  var ty: Ty,
  props: MMap[PureValue, (Value, Long)],
  var size: Long
) extends Obj {
  // get pairs
  def pairs: Map[PureValue, Value] = props.foldLeft(Map[PureValue, Value]()) {
    case (m, (k, (v, _))) => m + (k -> v)
  }

  // getters
  def apply(prop: PureValue): Value = (props.get(prop), ty, prop) match {
    case (Some((value, _)), _, _) => value
    case _ => Absent
  }

  // setters
  def findOrUpdate(prop: PureValue, value: Value): this.type = {
    props.get(prop) match {
      case Some(_) => this
      case _ => update(prop, value)
    }
  }

  // updates
  def update(prop: PureValue, value: Value): this.type = {
    val id = props
      .get(prop)
      .map { case (_, v) => v }
      .getOrElse({ size += 1; size })
    props += prop -> (value, id)
    this
  }

  // deletes
  def delete(prop: PureValue): this.type =
    { props -= prop; this }

  // copy of object
  def copied: IRMap = {
    val newProps = MMap[PureValue, (Value, Long)]()
    newProps ++= props
    IRMap(ty, newProps, size)
  }

  // keys of map
  def keys(intSorted: Boolean): Vector[PureValue] = {
    if (!intSorted) {
      if (ty.name == "SubMap") props
        .toVector
        .sortBy(_._2._2)
        .map(_._1)
      else props.toVector.map(_._1).sortBy(_.toString)
    } else (for {
      (Str(s), _) <- props.toVector
      d = ESValueParser.str2num(s)
      if toStringHelper(d) == s
      i = d.toLong // should handle unsigned integer
      if d == i
    } yield (s, i))
      .sortBy(_._2)
      .map { case (s, _) => Str(s) }
  }
}
object IRMap {
  def apply(tyname: String)(pairs: Iterable[(PureValue, PureValue)]): IRMap = {
    val irMap = IRMap(Ty(tyname))
    for ((prop, value) <- pairs) irMap.update(prop, value)
    irMap
  }
  def apply(ty: Ty): IRMap = IRMap(ty, ty.methods, 0L)
}

// IR lists
case class IRList(var values: Vector[PureValue] = Vector()) extends Obj {
  // types
  def ty: Ty = Ty("List")

  // getters
  def apply(key: PureValue): PureValue = key match {
    case INum(long) =>
      val idx = long.toInt
      if (0 <= idx && idx < values.length) values(idx)
      else Absent
    case Str("length") => INum(values.length)
    case v => error(s"invalid key: $v")
  }

  // appends
  def append(value: PureValue): this.type =
    { values :+= value; this }

  // prepends
  def prepend(value: PureValue): this.type =
    { values +:= value; this }

  // pops
  def pop(idx: PureValue): PureValue = idx match {
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
