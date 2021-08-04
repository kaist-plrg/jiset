package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.ir.{ doubleEquals, Expr, Addr, Value, PureValue, State }
import kr.ac.kaist.jiset.js
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec

sealed trait Type extends CheckerComponent {
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
    case NumT | ANum(_) => "Number"
    case BigIntT | ABigInt(_) => "BigInt"
    case StrT | AStr(_) => "String"
    case BoolT | ABool(_) => "Boolean"
    case AUndef => "Undefined"
    case ANull => "Null"
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
    case ANum(n) => NumT
    case ABigInt(b) => BigIntT
    case AStr(str) => StrT
    case ABool(b) => BoolT
    case AUndef => PrimT
    case ANull => PrimT
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
      Stat.doCheck(typeBug(s"unchecked abrupt completions: ${expr.beautified}"))
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
}

// completion types
sealed trait CompType extends Type
object CompType {
  // type creation from values
  def apply(addr: Addr, st: State): CompType = ???
  // {
  //   import kr.ac.kaist.jiset.ir._
  //   import CompletionType._
  //   addr.completionType(st) match {
  //     case Normal =>
  //       val value = st(addr, Str("Value"))
  //       NormalT(PureType(value, st))
  //     case _ => AbruptT
  //   }
  // }
}
case class NormalT(value: PureType) extends CompType
case object AbruptT extends CompType

// pure types
sealed trait PureType extends Type {
  // upcast
  override def upcast: PureType = this match {
    case ListT(t) => ListT(t.upcast)
    case MapT(t) => MapT(t.upcast)
    case ANum(_) => NumT
    case ABigInt(_) => BigIntT
    case AStr(_) => StrT
    case ABool(_) => BoolT
    case _ => this
  }
}
object PureType {
  // pure type creation from values
  def apply(pure: PureValue, st: State): PureType = {
    import kr.ac.kaist.jiset.ir._
    import kr.ac.kaist.jiset.js._
    pure match {
      case Const(name) => ConstT(name)
      case addr: Addr => st(addr) match {
        case m @ IRMap(Ty(tname), props, _) => tname match {
          case "" => RecordT() // TODO fill props
          case "SubMap" => MapT(???) // TODO fill elem
          case _ => NameT(tname)
        }
        case IRList(values) => values.toList match {
          case Nil => NilT
          case hd :: _ => PureType(hd, st) // TODO current: only head
        }
        case IRSymbol(_) => SymbolT
        case IRNotSupported(tname, _) => NameT(tname)
      }
      case ast: ASTVal => AstT(ast.ast.kind)
      case func: Func => FuncT(cfg.funcMap(func.algo.name).uid)
      case clo: Clo => ??? // TODO
      case cont: Cont => ??? // TODO
      case Num(double) => NumT
      case INum(long) => NumT
      case BigINum(bigint) => BigIntT
      case Str(str) => StrT
      case Bool(bool) => BoolT
      case Undef => AUndef
      case Null => ANull
      case Absent => AAbsent
    }
  }
}

// ECMAScript value types
case object ESValueT extends PureType

// single concrete type
sealed trait SingleT extends PureType

// norminal types
case class NameT(name: String) extends PureType {
  // lookup properties
  def apply(prop: String): AbsType = name match {
    case "ALGORITHM" => (for {
      algo <- js.algoMap.get(prop)
      func <- cfg.funcMap.get(algo.name)
    } yield FuncT(func.uid)).getOrElse(AAbsent).abs
    case _ => Type.propMap
      .getOrElse(name, Map())
      .getOrElse(prop, AAbsent)
  }
}

// record types
case class RecordT(props: Map[String, AbsType]) extends PureType {
  // lookup properties
  def apply(prop: String): AbsType = props.getOrElse(prop, AAbsent)

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

// constant types
case class ConstT(name: String) extends PureType with SingleT

// symbol types
case object SymbolT extends PureType

// list types
case object NilT extends PureType with SingleT
case class ListT(elem: PureType) extends PureType

// sub mapping types
case class MapT(elem: PureType) extends PureType

// function types
case class FuncT(fid: Int) extends PureType with SingleT

// TODO closure types
// TODO continuation types

// AST types
case class AstT(name: String) extends PureType

// primitive types
case object PrimT extends PureType

// arithmetic types
case object ArithT extends PureType

// numeric types
case object NumericT extends PureType

// floating-point number types
case object NumT extends PureType
case class ANum(double: Double) extends SingleT {
  override def equals(that: Any): Boolean = that match {
    case that: ANum => doubleEquals(this.double, that.double)
    case _ => false
  }
}

// TODO integer types

// big integer types
case object BigIntT extends PureType
case class ABigInt(bigint: scala.BigInt) extends SingleT

// string types
case object StrT extends PureType
case class AStr(str: String) extends SingleT

// boolean types
case object BoolT extends PureType
case class ABool(bool: Boolean) extends SingleT

// undefined type
case object AUndef extends SingleT

// null type
case object ANull extends SingleT

// absent type
case object AAbsent extends SingleT

// modeling
object Type extends Parser[Type] {
  // type creation from values
  def apply(value: Value, st: State): Type = ???
  // {
  //   import kr.ac.kaist.jiset.ir._
  //   value match {
  //     case addr: Addr if value.isCompletion(st) => CompType(addr, st)
  //     case _ => PureType(value, st)
  //   }
  // }

  // type aliases
  val typeAlias: List[(Type, Set[Type])] = List(
    BoolT -> Set[Type](ABool(true), ABool(false)),
    NumericT -> Set[Type](NumT, BigIntT),
    ArithT -> Set[Type](NumericT, StrT),
    PrimT -> Set[Type](ANull, AUndef, BoolT, ArithT, SymbolT),
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
      k -> (lmap.getOrElse(k, AAbsent.abs) ⊔ rmap.getOrElse(k, AAbsent.abs))
    }).toMap
  }
}
