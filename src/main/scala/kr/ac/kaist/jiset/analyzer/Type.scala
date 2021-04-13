package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.ir.{ doubleEquals, Expr }
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec

sealed trait Type {
  import Type._

  // conversion to abstract type
  def abs: AbsType = Type.abs(this)

  // get root of type
  def root: Type = parent.fold(this)(_.root)

  // get ancestor types
  def ancestors: Set[Type] = parent.map(_.ancestors).getOrElse(Set()) + this
  def strictAncestors: Set[Type] = parent.map(_.ancestors).getOrElse(Set())

  // check sub typing
  @tailrec
  final def <(that: Type): Boolean = {
    if (this == that) true
    else parent match {
      case Some(parent) => parent < that
      case None => false
    }
  }

  // remove types
  def -(that: Type): Set[Type] =
    if (this < that) Set() else typeAliasMap.get(this) match {
      case Some(set) if set contains that => set - that
      case _ => Set(this)
    }

  // get base types
  def bases: Set[Type] = baseMap.getOrElse(this, Set(this))

  // get type names
  def typeNameSet: Set[String] = for {
    x <- bases
    y <- x.typeName
  } yield y

  // get instance name
  def instanceNameSet: Set[String] = this match {
    case AstT(name) => cfg.spec.grammar.recSubs.getOrElse(name, Set(name))
    case NameT(name) => recSubTypes.getOrElse(name, Set(name)) ++ ancestors.collect {
      case NameT(name) => name
    }
    case _ => Set("")
  }

  // get name of base types
  def typeName: Option[String] = optional(this match {
    case NameT(name) if name endsWith "Object" => "Object"
    case NameT("ReferenceRecord") => "Reference"
    case SymbolT => "Symbol"
    case NumT | Num(_) => "Number"
    case BigIntT | BigInt(_) => "BigInt"
    case StrT | Str(_) => "String"
    case BoolT | Bool(_) => "Boolean"
    case Undef => "Undefined"
    case Null => "Null"
  })

  // get parent types
  def parent: Option[Type] = optional(this match {
    case NormalT(t) => t.parent match {
      case Some(parent: PureType) => NormalT(parent)
      case _ => error("no parent")
    }
    case NameT("Object") => ESValueT
    case NameT(name) => infoMap.get(name) match {
      case Some(Info(_, Some(parent), _)) => NameT(parent)
      case _ => error("no parent")
    }
    case PrimT => ESValueT
    case ArithT => PrimT
    case NumericT => ArithT
    case NumT => NumericT
    case BigIntT => NumericT
    case StrT => ArithT
    case BoolT => PrimT
    case SymbolT => PrimT
    case Num(n) => NumT
    case BigInt(b) => BigIntT
    case Str(str) => StrT
    case Bool(b) => BoolT
    case Undef => PrimT
    case Null => PrimT
    case _ => error("no parent")
  })

  // conversion to completions
  def toComp: CompType = this match {
    case (t: PureType) => NormalT(t)
    case (t: CompType) => t
  }

  // escape completions
  def escaped(expr: Expr): Option[PureType] = this match {
    case (t: PureType) => Some(t)
    case NormalT(t) => Some(t)
    case AbruptT =>
      alarm(s"unchecked abrupt completions: ${expr.beautified}")
      None
  }

  // uncheck escaped completions
  def uncheckEscaped: Option[PureType] = this match {
    case (t: PureType) => Some(t)
    case NormalT(t) => Some(t)
    case AbruptT => None
  }

  // upcast
  def upcast: Type = this match {
    case NormalT(t) => NormalT(t.upcast)
    case p: PureType => p.upcast
    case _ => this
  }

  // conversion to string
  override def toString: String = this match {
    case NameT(name) => s"$name"
    case RecordT(props) => props.map {
      case (p, t) => s"$p -> $t"
    }.mkString("{ ", ", ", " }")
    case AstT(name) => s"☊($name)"
    case ConstT(name) => s"~$name~"
    case CloT(fid) => s"λ[$fid]"
    case ESValueT => s"ESValue"
    case PrimT => "prim"
    case ArithT => "arith"
    case NumericT => "numeric"
    case NumT => "num"
    case BigIntT => "bigint"
    case StrT => "str"
    case BoolT => "bool"
    case NilT => s"[]"
    case ListT(elem) => s"[$elem]"
    case MapT(elem) => s"{ _ |-> $elem }"
    case SymbolT => "symbol"
    case NormalT(t) => s"Normal($t)"
    case AbruptT => s"Abrupt"
    case Num(n) => s"$n"
    case BigInt(b) => s"${b}n"
    case Str(str) => "\"" + str + "\""
    case Bool(b) => s"$b"
    case Undef => "undef"
    case Null => "null"
    case Absent => "?"
  }
}

// completion types
sealed trait CompType extends Type
case class NormalT(value: PureType) extends CompType
case object AbruptT extends CompType

// pure types
sealed trait PureType extends Type {
  // upcast
  override def upcast: PureType = this match {
    case ListT(t) => ListT(t.upcast)
    case MapT(t) => MapT(t.upcast)
    case Num(_) => NumT
    case BigInt(_) => BigIntT
    case Str(_) => StrT
    case Bool(_) => BoolT
    case _ => this
  }
}

// ECMAScript value types
case object ESValueT extends PureType

// norminal types
case class NameT(name: String) extends PureType {
  // lookup properties
  def apply(prop: String): AbsType =
    Type.propMap.getOrElse(name, Map()).getOrElse(prop, Absent)
}

// record types
case class RecordT(props: Map[String, AbsType]) extends PureType {
  // lookup properties
  def apply(prop: String): AbsType = props.getOrElse(prop, Absent)

  // merge record types
  def ⊔(that: RecordT): RecordT = {
    val keys = this.props.keySet ++ that.props.keySet
    RecordT(keys.toList.map(k => k -> (this(k) ⊔ that(k))).toMap)
  }
}
object RecordT {
  // constructor
  def apply(pairs: (String, AbsType)*): RecordT = RecordT(pairs.toMap)
}

// AST types
case class AstT(name: String) extends PureType

// constant types
case class ConstT(name: String) extends PureType with SingleT

// closure types
case class CloT(fid: Int) extends PureType with SingleT

// list types
case object NilT extends PureType with SingleT
case class ListT(elem: PureType) extends PureType

// sub mapping types
case class MapT(elem: PureType) extends PureType

// symbol types
case object SymbolT extends PureType

// primitive types
case object PrimT extends PureType
case object ArithT extends PureType
case object NumericT extends PureType
case object NumT extends PureType
case object BigIntT extends PureType
case object StrT extends PureType
case object BoolT extends PureType

// single concrete type
sealed trait SingleT extends PureType
case class Num(double: Double) extends SingleT {
  override def equals(that: Any): Boolean = that match {
    case that: Num => doubleEquals(this.double, that.double)
    case _ => false
  }
}
case class BigInt(bigint: scala.BigInt) extends SingleT
case class Str(str: String) extends SingleT
case class Bool(bool: Boolean) extends SingleT
case object Undef extends SingleT
case object Null extends SingleT
case object Absent extends SingleT

// modeling
object Type {
  // type aliases
  val typeAlias: List[(Type, Set[Type])] = List(
    BoolT -> Set[Type](Bool(true), Bool(false)),
    NumericT -> Set[Type](NumT, BigIntT),
    ArithT -> Set[Type](NumericT, StrT),
    PrimT -> Set[Type](Null, Undef, BoolT, ArithT, SymbolT),
    ESValueT -> Set[Type](NameT("Object"), PrimT),
  )
  val typeAliasMap: Map[Type, Set[Type]] = typeAlias.toMap
  val baseMap: Map[Type, Set[Type]] = {
    var map = Map[Type, Set[Type]]()
    for ((t, set) <- typeAlias) map += t -> set.flatMap(x => {
      map.get(x).getOrElse(Set(x))
    })
    map
  }

  // abstraction
  val abs: Type => AbsType = cached(AbsType(_))

  //////////////////////////////////////////////////////////////////////////////
  // Type Information
  //////////////////////////////////////////////////////////////////////////////
  case class Info(
    name: String,
    parent: Option[String],
    lazyProps: () => Map[String, AbsType]
  ) { lazy val props: Map[String, AbsType] = lazyProps() }

  // constructors
  def I(name: String, parent: String, props: => Map[String, AbsType]): Info =
    Info(name, Some(parent), () => props)
  def I(name: String, props: => Map[String, AbsType]): Info =
    Info(name, None, () => props)

  // property map
  type PropMap = Map[String, AbsType]

  // get type information
  lazy val infos: List[Info] = TypeModel.infos

  // type info map
  lazy val infoMap: Map[String, Info] =
    infos.map(info => info.name -> info).toMap

  // direct subtypes
  lazy val subTypes: Map[String, Set[String]] = {
    var children = Map[String, Set[String]]()
    for {
      info <- infos
      parent <- info.parent
      set = children.getOrElse(parent, Set())
    } children += parent -> (set + info.name)
    children
  }

  // recursive subtypes
  lazy val recSubTypes: Map[String, Set[String]] = {
    var descs = Map[String, Set[String]]()
    def aux(name: String): Set[String] = descs.get(name) match {
      case Some(set) => set
      case None =>
        val set = (for {
          sub <- subTypes.getOrElse(name, Set())
          elem <- aux(sub)
        } yield elem) + name
        descs += name -> set
        set
    }
    infos.collect { case Info(name, None, _) => aux(name) }
    descs
  }

  // property map
  lazy val propMap: Map[String, PropMap] =
    infos.map(info => info.name -> getPropMap(info.name)).toMap

  //////////////////////////////////////////////////////////////////////////////
  // Private Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // get property map
  private def getPropMap(name: String): PropMap = {
    val upper = getUpperPropMap(name)
    val lower = getLowerPropMap(name)
    lower.foldLeft(upper) {
      case (map, (k, t)) =>
        val newT = t ⊔ map.getOrElse(k, AbsType.Bot)
        map + (k -> newT)
    }
  }

  // get property map from ancestors
  private def getUpperPropMap(name: String): PropMap = infoMap.get(name) match {
    case Some(info) =>
      val parentProps = info.parent.map(getUpperPropMap).getOrElse(Map())
      val props = info.props
      weakMerge(parentProps, props)
    case None => Map()
  }

  // get property map of name
  private def getSamePropMap(name: String): PropMap =
    infoMap.get(name).map(_.props).getOrElse(Map())

  // get property map from ancestors
  private def getLowerPropMap(name: String): PropMap = subTypes.get(name) match {
    case Some(children) => children.map(child => {
      val lower = getLowerPropMap(child)
      val props = getSamePropMap(child)
      weakMerge(lower, props)
    }).reduce(parallelWeakMerge)
    case None => getSamePropMap(name)
  }

  // weak merge
  private def weakMerge(lmap: PropMap, rmap: PropMap): PropMap = {
    val keys = lmap.keySet ++ rmap.keySet
    keys.toList.map(k => {
      val lt = lmap.getOrElse(k, AbsType.Bot)
      val rt = rmap.getOrElse(k, AbsType.Bot)
      k -> (lt ⊔ rt)
    }).toMap
  }

  // parallel weak merge
  private def parallelWeakMerge(lmap: PropMap, rmap: PropMap): PropMap = {
    val keys = lmap.keySet ++ rmap.keySet
    keys.toList.map(k => {
      k -> (lmap.getOrElse(k, Absent.abs) ⊔ rmap.getOrElse(k, Absent.abs))
    }).toMap
  }
}
